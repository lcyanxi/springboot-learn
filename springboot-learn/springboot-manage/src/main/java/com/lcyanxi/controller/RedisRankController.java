package com.lcyanxi.controller;

import com.lcyanxi.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * @author : lichang
 * @desc : 模拟微博排行榜
 * @since : 2021/11/16/11:16 上午
 */
@RestController
public class RedisRankController {
    @Autowired
    private StringRedisTemplate redisTemplate;


    @GetMapping(value = "/getHour")
    public Set getHour() {
        long hour = System.currentTimeMillis() / (1000 * 60 * 60);
        // ZREVRANGE 返回有序集key中，指定区间内的成员,降序。
        return redisTemplate.opsForZSet().reverseRangeWithScores(Constants.HOUR_KEY + hour, 0, 30);
    }

    @GetMapping(value = "/getDay")
    public Set getDay() {
        return redisTemplate.opsForZSet().reverseRangeWithScores(Constants.DAY_KEY, 0, 30);
    }

    @GetMapping(value = "/getWeek")
    public Set getWeek() {
        return redisTemplate.opsForZSet().reverseRangeWithScores(Constants.WEEK_KEY, 0, 30);
    }

    @GetMapping(value = "/getMonth")
    public Set getMonth() {
        return redisTemplate.opsForZSet().reverseRangeWithScores(Constants.MONTH_KEY, 0, 30);
    }
}
