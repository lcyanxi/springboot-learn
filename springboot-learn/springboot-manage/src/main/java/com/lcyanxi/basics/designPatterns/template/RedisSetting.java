package com.lcyanxi.basics.designPatterns.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2022/04/21/6:04 下午
 */
public class RedisSetting extends AbstractSetting{
    @Autowired
    private StringRedisTemplate redisTemplate;

    protected String lookupCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    protected void putIntoCache(String key, String value) {
        redisTemplate.opsForValue().set(key,value);
    }
}
