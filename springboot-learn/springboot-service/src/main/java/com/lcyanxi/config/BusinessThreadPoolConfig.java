package com.lcyanxi.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
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
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("businessThreadPoolExecutor-pool-%d").build();
        return new ThreadPoolExecutor(5, 6, 3,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(3),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }
}
