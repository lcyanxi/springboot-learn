package com.lcyanxi.basics.designPatterns.observer.guavaEvent;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lichang
 * @date 2021/1/2
 */
@Slf4j
public class OrderSendMsgListener {
    @Subscribe
    public void orderSendMsg(OrderInfoEvent event) {
        try {
            log.info("监听到了【订单支付成功消息】 event: [{}]", JSON.toJSONString(event));
            Preconditions.checkArgument(event.getUserId() > 0);
        } catch (Exception e) {
            log.error("event信息错误 ",e);
            return;
        }
    }
}
