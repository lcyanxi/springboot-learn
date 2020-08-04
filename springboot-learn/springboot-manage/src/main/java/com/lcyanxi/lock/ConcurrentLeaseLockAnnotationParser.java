package com.lcyanxi.lock;

import com.xxl.job.core.biz.model.ReturnT;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import static java.util.concurrent.TimeUnit.SECONDS;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author lichang
 * @date 2020/6/16
 */
@Slf4j
@Aspect
public class ConcurrentLeaseLockAnnotationParser implements InitializingBean {

    /**
     * hold registered thread
     */
    private static ConcurrentHashMap<Thread, ThreadLockInfo> THREAD_HOLD = new ConcurrentHashMap<>();

    private ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(1);

    private static final int CHECK_TIME_SEC = 10;

    private static final int TTL_TIME = 60;

    /**
     * 线程池是否创建
     */
    private volatile boolean poolExecute = false;


    /**
     * 直接用的好处是各种命令都能用，但局限了codis，codis改了这个就要改
     * 限制使用2.3.3以上的codis
     */

    @Autowired
    private RedisTemplate<String, String> redisClient;


    @Pointcut("@annotation(com.lcyanxi.lock.ConcurrentLeaseLock)")
    public void cutPoint() {}

    /**
     * @param joinPoint joinPoint
     * @return Object
     */
    @Around("cutPoint()")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        Object obj = null;
        boolean isJob = false;
        try {
            //获取方法
            Method method = this.getMethod(joinPoint);
            ConcurrentLeaseLock annotation = method.getAnnotation(ConcurrentLeaseLock.class);
            method = doCheck(method, annotation);
            if (null == method) {
                // 执行目标方法
                return joinPoint.proceed();
            }

            if (null == redisClient) {
                if (log.isErrorEnabled()) {
                    log.error("ConcurrentLeaseLock fail !, KooJedisClient is necessary! " + "and it's version must >= 2.2.3 ");
                }
                return joinPoint.proceed();
            }
            if (tryAcquireLock(annotation.lockKey())) {
                obj = joinPoint.proceed();
                unlock(annotation.lockKey());
                if (obj instanceof ReturnT) {
                    isJob = true;
                }
            } else {
                if (log.isInfoEnabled()) {
                    log.info("ConcurrentLeaseLock tryAcquireLock fail, lockKey: {}", annotation.lockKey());
                }
            }

        } catch (Throwable throwable) {
            log.error("ConcurrentLeaseLock error ", throwable);
            throwable.printStackTrace();

        }
        // 对于xxl-job, 只要调度成功，要求业务上必须返回ReturnT<>("success")
        return isJob ? new ReturnT<>("success") : obj;
    }


    /**
     * getMethod
     * @param joinPoint joinPoint
     * @return Method
     * @throws Exception
     */
    private Method getMethod(JoinPoint joinPoint) throws Exception {
        return ((MethodSignature) joinPoint.getSignature()).getMethod();
    }


    /**
     * 一些检查
     * @param method     method
     * @param annotation annotation
     * @return Method
     * @throws Throwable
     */
    private Method doCheck(Method method, ConcurrentLeaseLock annotation) throws Throwable {
        if (null == annotation) {
            return null;
        }
        String key = annotation.lockKey();
        if (StringUtils.isBlank(key)) {
            if (log.isErrorEnabled()) {
                log.error("ConcurrentLeaseLock fail!, miss lockKey");
            }
            return null;
        }
        return method;
    }


    public boolean tryAcquireLock(String lockName) {
        Thread lockThread = Thread.currentThread();
        boolean success;
        Object redisRandomValue = redisClient.opsForValue().get(lockName);
        // set的时候保证必须都有
        ThreadLockInfo threadLockInfo = THREAD_HOLD.get(lockThread);

        // 可重入
        if (null != threadLockInfo && null != redisRandomValue && redisRandomValue.equals(threadLockInfo.getSeed())) {
            success = true;
        } else {
            //尝试获取锁
            String seed = String.valueOf(ThreadLocalRandom.current().nextLong());
            try {
                redisClient.opsForList().set(lockName,TTL_TIME,seed);
                success = true;
            }catch (Exception e){
                if (log.isErrorEnabled()) {
                    log.error("ConcurrentLeaseLock set key is fail!, miss lockKey");
                }
                success = false;
            }
            if (success){
                // who lock, who release
                threadLockInfo = ThreadLockInfo.builder().thread(lockThread).seed(seed).lockName(lockName).build();
                THREAD_HOLD.put(lockThread, threadLockInfo);
            }
        }
        return success;
    }

    public void startTtlDaemon() {
        log.info("ConcurrentLeaseLock TTL daemon thread start");
        if (!poolExecute) {
            synchronized (this) {
                if (!poolExecute) {
                    poolExecute = true;
                    pool.scheduleWithFixedDelay(() -> THREAD_HOLD.forEach((t, info) -> {
                        if (!Objects.isNull(t) && !Objects.isNull(info)) {
                            String redisRandomValue = redisClient.opsForValue().get(info.getLockName());
                            if (t.isAlive() && info.getSeed().equals(redisRandomValue)) {
                                redisClient.expire(info.getLockName(), TTL_TIME, SECONDS);
                            } else {
                                THREAD_HOLD.remove(t);
                            }
                        }
                    }), 1, CHECK_TIME_SEC, SECONDS);
                }
            }
        }
    }

    public void stopTtlDaemon() {
        log.info("ConcurrentLeaseLock TTL daemon thread stop");
        THREAD_HOLD.keySet().forEach(thread -> THREAD_HOLD.remove(thread));
        stopPool();
    }

    private synchronized void stopPool() {
        poolExecute = false;
        if (pool != null) {
            pool.shutdown();
        }
    }

    public void unlock(String lockName) {
        String redisRandomValue = redisClient.opsForValue().get(lockName);
        if (StringUtils.isNotBlank(redisRandomValue)
                && THREAD_HOLD.get(Thread.currentThread()).getSeed().equals(redisRandomValue)) {
            redisClient.delete(lockName);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        startTtlDaemon();
        // 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(this::stopTtlDaemon));
    }

}
