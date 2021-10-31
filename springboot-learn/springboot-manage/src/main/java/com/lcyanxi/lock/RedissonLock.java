package com.lcyanxi.lock;

import java.lang.annotation.*;

/**
 * @author : lichang
 * @desc : redisson 分布式锁注解
 * @since : 2021/10/28/10:49 上午
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {
    /**
     * 锁 key 的前缀
     */
    String lockPre();

    /**
     * 锁 key 的 value
     */
    String value();

    /**
     * 重试最大次数
     */
    int retryMax() default 1;

    /**
     * 锁等待时间
     */
    long waitSeconds() default 0;

    /**
     * 锁失效的时间
     */
    long leaseSeconds() default 2000;
}
