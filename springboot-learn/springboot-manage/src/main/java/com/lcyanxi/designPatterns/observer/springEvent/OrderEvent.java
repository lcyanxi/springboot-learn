package com.lcyanxi.designPatterns.observer.springEvent;


import org.springframework.context.ApplicationEvent;

/**
 * @author lichang
 * @date 2021/1/2
 */
public class OrderEvent  extends ApplicationEvent {

    public OrderEvent(Object source) {
        super(source);
    }
}
