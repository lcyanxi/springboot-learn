package com.lcyanxi.controller;

import com.google.common.eventbus.AsyncEventBus;
import com.lcyanxi.designPatterns.observer.spring.EventPublisher;
import com.lcyanxi.designPatterns.observer.spring.OrderEvent;
import com.lcyanxi.designPatterns.observer.springEvent.OrderInfoEvent;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lichang
 * @date 2021/1/2
 */
@RestController
public class EventPublishController {

    @Resource
    @Qualifier("orderSendMsgHandleEventBus")
    private AsyncEventBus orderSendMsgHandleEventBus;

    @Autowired
    private EventPublisher createOrderEventPublisher;

    @GetMapping(value = "/order")
    public String order(Integer productId, Integer userId){
        OrderInfoEvent event = new OrderInfoEvent();
        event.setId(1);
        event.setOrderNo("202101021020");
        event.setProductId(productId);
        event.setUserId(userId);
        event.setProductName("53度飞天茅台售价只需1499");
        orderSendMsgHandleEventBus.post(event);
        return  "下单成功";
    }


    @PostMapping("/spring/createOrder")
    public String createOrder(Integer productId, Integer userId) {
        OrderInfoEvent event = new OrderInfoEvent();
        event.setId(1);
        event.setOrderNo("202101021020");
        event.setProductId(productId);
        event.setUserId(userId);
        event.setProductName("53度飞天茅台售价只需1499");
        createOrderEventPublisher.publishCreateOrderEvent(new OrderEvent(event));
        return  "下单成功";
    }
}
