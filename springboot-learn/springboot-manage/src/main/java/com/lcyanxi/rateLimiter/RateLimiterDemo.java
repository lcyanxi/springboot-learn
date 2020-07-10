package com.lcyanxi.rateLimiter;

import com.google.common.util.concurrent.RateLimiter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lichang
 * @date 2020/7/9
 */
public class RateLimiterDemo {
    public static void main(String[] args) {
        // 创建一个限流器，参数代表每秒生成的令牌数
        RateLimiter limiter = RateLimiter.create(1);

        for(int i = 1; i < 10; i = i + 2 ) {
            double waitTime = limiter.acquire(i);
            System.out.println("cutTime=" + stampToDate(System.currentTimeMillis()) + " acq:" + i + " waitTime:" + waitTime);
        }
    }


    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(long time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time_Date = sdf.format(new Date(time * 1000L));
        return time_Date;

    }

}
