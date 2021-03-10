package com.lcyanxi.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lichang
 * @date 2021/3/8
 */
@Configuration
@Slf4j
public class ThreadPoolAutoConfiguration {

    @Bean
    @ConditionalOnClass(ThreadPoolExecutor.class)// 项目中需要ThreadPoolExecutor类，该类为jdk自带的 所有一定成立
    public ThreadPoolExecutor MyThreadPool(){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 3,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(3));
        log.info("threadPoolAutoConfiguration MyThreadPool is init coreSize:[{}],maxSize:[{}],poolSize:[{}]",
                threadPoolExecutor.getCorePoolSize(),threadPoolExecutor.getMaximumPoolSize(),threadPoolExecutor.getPoolSize());
        return threadPoolExecutor;
    }
}
