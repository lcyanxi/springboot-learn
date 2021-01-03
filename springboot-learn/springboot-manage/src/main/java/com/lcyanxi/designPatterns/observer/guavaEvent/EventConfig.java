package com.lcyanxi.designPatterns.observer.guavaEvent;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lichang
 * @date 2021/1/2
 */
@Configuration
public class EventConfig {

    /**
     * @return EventBus
     */
    @Bean
    @Qualifier("orderSendMsgHandleEventBus")
    public AsyncEventBus orderSendMsgHandleEventBus() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("orderSendMsgHandleEventBus-pool-%d").build();
        ExecutorService threadPoolExecutor = new ThreadPoolExecutor(3, 5,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        // 可以支持同步
        AsyncEventBus asyncEventBus = new AsyncEventBus("orderSendMsgHandleEventBus", threadPoolExecutor);
        asyncEventBus.register(new OrderSendMsgListener());
        return asyncEventBus;
    }

}
