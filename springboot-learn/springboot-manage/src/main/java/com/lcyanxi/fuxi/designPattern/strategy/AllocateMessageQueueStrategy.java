package com.lcyanxi.fuxi.designPattern.strategy;

import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

/**
 * rocketMQ 消费端负载均衡分配策略【策略模式】
 */
public interface AllocateMessageQueueStrategy {
    /**
     * 分配方法
     */
    List<MessageQueue> allocate(final String consumerGroup, final String currentCID, final List<MessageQueue> mqAll, final List<String> cidAll);

    /**
     * Algorithm name
     */
    String getName();
}
