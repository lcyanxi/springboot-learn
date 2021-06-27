package com.lcyanxi.basics.designPatterns.observer.springEvent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author lichang
 * @date 2021/1/2
 */
@Slf4j
@Component
public class CreateOrderListener {
    @EventListener
    public void processCreateOrderEvent(OrderEvent createOrderEvent) {
        log.info("注解式 springEvent event 收到消息，data:[{}] ; 开始处理相应的事件。",createOrderEvent.getSource());
    }
}
