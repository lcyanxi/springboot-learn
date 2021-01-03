package com.lcyanxi.designPatterns.observer.spring;

import com.lcyanxi.designPatterns.observer.springEvent.OrderInfoEvent;
import org.springframework.context.ApplicationEvent;

/**
 * @author lichang
 * @date 2021/1/2
 */
public class OrderEvent extends ApplicationEvent {

    public OrderEvent(Object source) {
        super(source);
    }
    public OrderInfoEvent getOrder() {
        return (OrderInfoEvent) this.source;
    }
}
