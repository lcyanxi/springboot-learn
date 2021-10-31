package com.lcyanxi.lock;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author : lichang
 * @desc : redisson 分布式锁切面解析
 * @since : 2021/10/28/10:55 上午
 */
@Slf4j
@Aspect
@Component
public class RedissonLockAspect {
    private final static String SPLIT_STR = "_";
    private final ExpressionParser parser = new SpelExpressionParser();
    private final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @Resource
    private RedissonClient redisson;

    @Around("@annotation(com.lcyanxi.lock.RedissonLock))")
    public Object aroundMethod(ProceedingJoinPoint pjd) {
        RLock rLock = null;
        String key = null;
        Method method = null;
        try {
            // 获取方法
            method = getMethod(pjd);
            RedissonLock annotation = method.getAnnotation(RedissonLock.class);
            key = getLockKey(pjd, annotation, method);
            log.info("redissonLockAspect aroundMethod is start key:{}...........", key);
            // 获取锁
            rLock = redisson.getLock(key);
            boolean tryLock = tryLock(rLock, key, annotation.retryMax(), annotation.waitSeconds(), annotation.leaseSeconds());
            if (tryLock) {
                return pjd.proceed();
            }
        } catch (Throwable throwable) {
            log.error("redissonLockAspect is  error key:{}", key, throwable);
        } finally {
            // 释放锁
            try {
                unlock(rLock);
            } catch (Exception e) {
                log.error("redissonLockAspect unlock is error key:{} e:", key, e);
            }
        }
        return getReturnDefault(method);
    }

    /**
     * 将方法的参数名和参数值绑定
     *
     * @param method 方法，根据方法获取参数名
     * @param args   方法的参数值
     */
    private EvaluationContext bindParam(Method method, Object[] args) {
        // 获取方法的参数名
        String[] params = discoverer.getParameterNames(method);
        // 将参数名与参数值对应起来
        EvaluationContext context = new StandardEvaluationContext();
        if (Objects.nonNull(params)) {
            for (int len = 0; len < params.length; len++) {
                context.setVariable(params[len], args[len]);
            }
        }
        return context;
    }

    /**
     * 将 lockPre 和 value 拼装为 key 值
     */
    private String getLockKey(ProceedingJoinPoint pjd, RedissonLock annotation, Method method) {
        // 获取方法的参数值
        Object[] args = pjd.getArgs();
        EvaluationContext context = this.bindParam(method, args);
        String[] split = annotation.value().split(SPLIT_STR);
        StringBuilder builder = new StringBuilder();
        builder.append(annotation.lockPre());
        // 根据 SpEl 表达式获取值
        for (String str : split) {
            Expression expression = parser.parseExpression(str);
            Object key = expression.getValue(context);
            builder.append(SPLIT_STR).append(key);
        }
        return builder.toString();
    }

    /**
     * 获取当前执行的方法
     */
    private Method getMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        return pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
    }

    /**
     * 设定默认的返回值类型
     */
    private Object getReturnDefault(Method method) {
        if (Objects.isNull(method)) {
            return null;
        }
        Class<?> returnType = method.getReturnType();
        if (Collection.class.isAssignableFrom(returnType)) {
            return Lists.newArrayList();
        }
        return Map.class.isAssignableFrom(returnType) ? Maps.newHashMap() : null;
    }

    /**
     * 尝试获取锁
     *
     * @param rLock        锁对象
     * @param lockName     key 名称
     * @param retryMax     最大重试次数
     * @param waitSeconds  等待时间
     * @param leaseSeconds 锁失效时间
     * @return 是否获取锁
     */
    public boolean tryLock(RLock rLock, String lockName, int retryMax, long waitSeconds, long leaseSeconds) {
        boolean lockSuccess = false;
        if (rLock == null) {
            log.error("rLock is null");
            return false;
        }
        int retryCount = 0;
        while (retryCount < retryMax) {
            try {
                lockSuccess = rLock.tryLock(waitSeconds, leaseSeconds, TimeUnit.SECONDS);
                if (lockSuccess) {
                    break;
                }
                log.warn("tryLock failed, retryCount: {}, lockName: {}", retryCount, lockName);
            } catch (Exception exception) {
                log.error("tryLock Exception, uid: {}", lockName, exception);
            } finally {
                ++retryCount;
            }
        }
        return lockSuccess;
    }

    /**
     * 释放锁
     * @param rLock 锁对象
     */
    public void unlock(RLock rLock) {
        if (rLock != null &&  rLock.isLocked() && rLock.isHeldByCurrentThread()) {
            rLock.unlock();
        }
    }
}
