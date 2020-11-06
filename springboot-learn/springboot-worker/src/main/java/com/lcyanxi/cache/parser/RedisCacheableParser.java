package com.lcyanxi.cache.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.otter.canal.common.utils.CommonUtils;
import com.google.common.cache.LoadingCache;
import com.lcyanxi.cache.RedisCacheable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.EvaluationContext;

/**
 * @author lichang
 * @date 2020/9/11
 */
public class RedisCacheableParser {
    private static Logger logger = LoggerFactory.getLogger(RedisCacheableParser.class);
    LoadingCache<String, String> sysDictConfCache;

    private final ExpressionEvaluator evaluator =  new ExpressionEvaluator();
    @Autowired
    private RedisTemplate<String, String> redisClient;
    @Autowired
    private ApplicationContext applicationContext;



    @Pointcut("@annotation(com.lcyanxi.cache.RedisCacheable)")
    public void cutPoint() {
    }

    @Around("cutPoint()")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            if (null == this.redisClient) {
                return joinPoint.proceed();
            }
            Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
            RedisCacheable annotation = method.getAnnotation(RedisCacheable.class);
            method = this.doCheck(method, annotation);
            if (null == method) {
                return joinPoint.proceed();
            }
            String key = this.analysisKeyToValue(joinPoint, method, annotation);
            logger.debug("springboot worker redisCacheable key is {}", key);
            String cacheKey = annotation.keyPrefix() + key;
            String cacheValue = this.redisClient.opsForValue().get(cacheKey);
            if (StringUtils.isNotBlank(cacheValue)) {
                return this.getReturn(method, cacheValue);
            }
            Object obj = joinPoint.proceed();
            if (null != obj || annotation.impervious()) {
                this.putCache(cacheKey, annotation.expire(), annotation.randomMax(), obj);
            }
            return obj;

        } catch (Throwable e) {
            e.printStackTrace();
            logger.error("springboot worker redisCacheable error, ", e);
            return joinPoint.proceed();
        }
    }

    private Object getReturn( Method method, String cacheValue) {
        Class<?> returnType = method.getReturnType();
        if (Collection.class.isAssignableFrom(returnType)) {
            Type genericReturnType = method.getGenericReturnType();
            Class entityClass = (Class)((ParameterizedType)genericReturnType).getActualTypeArguments()[0];
            return JSON.parseArray(cacheValue, entityClass);
        } else {
            return JSON.parseObject(cacheValue, returnType);
        }
    }


    private void putCache(String key, int expire, int randomMax, Object resultObject) {
        int randomTTL = expire + ThreadLocalRandom.current().nextInt(0, randomMax);
        this.retriesCall(() -> {
            return this.redisClient.opsForValue().setIfAbsent(key, JSON.toJSONString(resultObject),randomTTL , TimeUnit.MILLISECONDS);
            }, 1, (e) -> {
            logger.error("Jspringboot worker RedisCacheable 设置缓存失败[cacheKey:[{}]]", key, e);
            return null;
        });
    }

    private <V> V retriesCall(Callable<V> callable, int retries, RedisCacheableParser.RetriesFallback<V> fallback) {
        Exception exception = null;
        int i = 0;

        while(i <= retries) {
            try {
                return callable.call();
            } catch (Exception var7) {
                exception = var7;
                ++i;
            }
        }

        return fallback.fallback(exception);
    }

    private Method doCheck(Method method, RedisCacheable annotation) throws Throwable {
        if (null == annotation) {
            return null;
        }

        String key = annotation.key();
        if (StringUtils.isBlank(key)) {
            return null;
        }
        String fullMethodPath = method.getDeclaringClass().getName() + "." + method.getName();
        String notEnableMethod = this.sysDictConfCache.get("springboot_worker_not_enable_method");
        return StringUtils.isNotBlank(notEnableMethod) && notEnableMethod.contains(fullMethodPath) ? null : method;
    }


    private String analysisKeyToValue(ProceedingJoinPoint joinPoint, Method method, RedisCacheable annotation) {
        Object target = joinPoint.getTarget();
        EvaluationContext evalContext = this.evaluator.createEvaluationContext(this.applicationContext, method, joinPoint.getArgs(), target, AopUtils.getTargetClass(target));
        StringBuffer sb = new StringBuffer();
        String[] paramKey = annotation.key().split("&");

        for (String keyItem : paramKey) {
            String[] key = this.evaluator.key(keyItem.trim(), method, evalContext, String[].class);
            if (null != key && !StringUtils.isBlank(key[0])) {
                sb.append(":").append(key[0]);
            } else {
                sb.append(":").append("null");
            }
        }
        return sb.toString();
    }

    private interface RetriesFallback<V> {
        V fallback(Exception var1);
    }
}
