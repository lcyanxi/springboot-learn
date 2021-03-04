package com.lcyanxi.springbootworker;

import com.google.common.util.concurrent.RateLimiter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author lichang
 * @date 2020/7/9
 */
public class RateLimiterDemo {
    public static void main(String[] args) throws InterruptedException {
//        // 创建一个限流器，参数代表每秒生成的令牌数
//        RateLimiter limiter = RateLimiter.create(1);
//
//        for(int i = 1; i < 10; i = i + 2 ) {
//            double waitTime = limiter.acquire(i);
//            System.out.println("cutTime=" + stampToDate(System.currentTimeMillis()) + " acq:" + i + " waitTime:" + waitTime);
//        }
//        rateLimiterTest();
        rateLimiterTest1();

    }


    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(long time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time_Date = sdf.format(new Date(time * 1000L));
        return time_Date;

    }
    public static  void rateLimiterTest() throws InterruptedException {
        // 创建一个限流器，参数代表每秒生成的令牌数
        RateLimiter limiter = RateLimiter.create(1);
        limiter.tryAcquire(1,1,TimeUnit.MILLISECONDS);

        for(int i = 1; i < 1000; i = i + 2 ) {
            double waitTime = limiter.acquire(i);
            System.out.println("cutTime=" + stampToDate(System.currentTimeMillis()) + " acq:" + i + " waitTime:" + waitTime);

        }
    }

    public static void rateLimiterTest1() throws InterruptedException {
        // 0.25表示4秒中产生一个令牌  1表示一秒产生一个令牌
        RateLimiter rateLimiter = RateLimiter.create(1);
        for (int i =0 ; i < 1000 ; i++){
            System.out.println("index :" + i + " ***  result:"+ rateLimiter.tryAcquire());
            TimeUnit.MILLISECONDS.sleep(250);
        }
    }
}
