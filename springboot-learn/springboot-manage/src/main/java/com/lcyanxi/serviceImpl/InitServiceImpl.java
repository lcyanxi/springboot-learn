package com.lcyanxi.serviceImpl;

import com.lcyanxi.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author : lichang
 * @desc : 模拟微博排行榜
 * @since : 2021/11/16/11:21 上午
 */
@Service
@Slf4j
public class InitServiceImpl {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 先初始化1个月的历史数据
     */
    public void init30day() {
        // 计算当前的小时key
        long hour = System.currentTimeMillis() / (1000 * 60 * 60);
        // 初始化近30天，每天24个key
        for (int i = 1; i < 24 * 30; i++) {
            // 倒推过去30天
            String key = Constants.HOUR_KEY + (hour - i);
            this.initMember(key);
            System.out.println(key);
        }
    }

    /**
     * 初始化某个小时的key
     */
    public void initMember(String key) {
        Random rand = new Random();
        // 采用26个英文字母来实现排行，随机为每个字母生成一个随机数作为score
        for (int i = 1; i <= 26; i++) {
            this.redisTemplate.opsForZSet().add(key, String.valueOf((char) (96 + i)), rand.nextInt(10));
        }
    }
}
