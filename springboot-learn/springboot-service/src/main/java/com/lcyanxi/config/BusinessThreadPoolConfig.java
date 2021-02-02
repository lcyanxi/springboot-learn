package com.lcyanxi.config;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lichang
 * @date 2021/1/29
 */
@Configuration
public class BusinessThreadPoolConfig {

    @Bean(name = "businessThreadPoolExecutor")
    public ThreadPoolExecutor asyncRunBusinessThreadPoolExecutor() {
        return new ThreadPoolExecutor(5, 6, 3,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(3),new ThreadPoolExecutor.AbortPolicy());
    }
}
