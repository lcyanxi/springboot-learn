package com.lcyanxi.designPatterns.observer.springEvent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @author lichang
 * @date 2021/1/2
 */
@Slf4j
@Component
public class EventPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishCreateOrderEvent(OrderEvent createOrderEvent) {
        log.info("发布了一个[订单创建]事件：{}", createOrderEvent);
        applicationEventPublisher.publishEvent(createOrderEvent);
    }
}
