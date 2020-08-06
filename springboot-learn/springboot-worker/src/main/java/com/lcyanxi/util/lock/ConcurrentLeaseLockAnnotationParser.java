package com.lcyanxi.util.lock;

import com.xxl.job.core.biz.model.ReturnT;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.SECONDS;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author lichang
 * @date 2020/8/4
 */
@Aspect
@Slf4j
@Component
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


    @Autowired
    private RedisTemplate<String, String> redisClient;

    /**
     * 定义一个切入点表达式,用来确定哪些类需要代理
     */
    @Pointcut("@annotation(com.lcyanxi.util.lock.ConcurrentLeaseLock)")
    public void declareJoinPointerExpression() {}

    /**
     * 环绕方法,可自定义目标方法执行的时机
     * @return 此方法需要返回值,返回值视为目标方法的返回值
     */
    @Around("declareJoinPointerExpression()")
    public Object aroundMethod(ProceedingJoinPoint pjd){
        log.info("ConcurrentLeaseLockAnnotationParser aroundMethod is start...........");

        Object obj = null;
        boolean isJob = false;
        try {
            //获取方法
            Method method = ((MethodSignature) pjd.getSignature()).getMethod();
            ConcurrentLeaseLock annotation = method.getAnnotation(ConcurrentLeaseLock.class);
            method = doCheck(method, annotation);
            if (null == method) {
                // 执行目标方法
                return pjd.proceed();
            }

            if (null == redisClient) {
                if (log.isErrorEnabled()) {
                    log.error("ConcurrentLeaseLock fail !, KooJedisClient is necessary! " + "and it's version must >= 2.2.3 ");
                }
                return pjd.proceed();
            }
            if (tryAcquireLock(annotation.lockKey())) {
                obj = pjd.proceed();
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

    @Override
    public void afterPropertiesSet() throws Exception {
        startTtlDaemon();
        // 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(this::stopTtlDaemon));
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


    /**
     * 获取锁
     * @param lockName key
     * @return
     */
    public boolean tryAcquireLock(String lockName) {

        Thread lockThread = Thread.currentThread();
        boolean success = false;
        Object redisRandomValue = redisClient.opsForValue().get(lockName);
        // set的时候保证必须都有
        ThreadLockInfo threadLockInfo = THREAD_HOLD.get(lockThread);

        // 可重入
        if (null != threadLockInfo && null != redisRandomValue && redisRandomValue.equals(threadLockInfo.getSeed())) {
            success = true;
        } else {
            //尝试获取锁
            String seed = String.valueOf(ThreadLocalRandom.current().nextLong());
            Boolean ifAbsent = redisClient.opsForValue().setIfAbsent(lockName, seed, TTL_TIME, TimeUnit.MILLISECONDS);
            success = ifAbsent == null ? false : ifAbsent;
            if (success){
                // who lock, who release
                threadLockInfo = ThreadLockInfo.builder().thread(lockThread).seed(seed).lockName(lockName).build();
                THREAD_HOLD.put(lockThread, threadLockInfo);
            }
        }
        return success;
    }

    /**
     * 释放锁
     * @param lockName key
     */
    public void unlock(String lockName) {
        String redisRandomValue = redisClient.opsForValue().get(lockName);
        if (StringUtils.isNotBlank(redisRandomValue)
                && THREAD_HOLD.get(Thread.currentThread()).getSeed().equals(redisRandomValue)) {
            redisClient.delete(lockName);
        }
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
}



