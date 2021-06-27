package com.lcyanxi.basics.guava;

import com.google.common.util.concurrent.RateLimiter;
import java.util.concurrent.TimeUnit;

/**
 * @author lichang
 * @date 2021/1/28
 */
public class GuavaRateLimiterDemo {
    public static void main(String[] args) throws InterruptedException {
        // 0.25表示4秒中产生一个令牌  1表示一秒产生一个令牌
        RateLimiter rateLimiter = RateLimiter.create(1);
        for (int i =0 ; i < 1000 ; i++){
            System.out.println("index :" + i + " ***  result:"+ rateLimiter.tryAcquire());
            TimeUnit.MILLISECONDS.sleep(250);
        }
    }


    public static void rateLimiterTest() throws InterruptedException {
        // 0.25表示4秒中产生一个令牌  1表示一秒产生一个令牌
        RateLimiter rateLimiter = RateLimiter.create(1);
        for (int i =0 ; i < 1000 ; i++){
            System.out.println("index :" + i + " ***  result:"+ rateLimiter.tryAcquire());
            TimeUnit.MILLISECONDS.sleep(250);
        }
    }
}
