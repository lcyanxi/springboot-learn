package com.lcyanxi.fuxi.designPattern.strategy;

import org.apache.rocketmq.common.message.MessageQueue;

import java.util.Collections;
import java.util.List;

/**
 * 一致性 hash 分配策略
 */
public class AllocateMessageQueueConsistentHash implements AllocateMessageQueueStrategy{
    @Override
    public List<MessageQueue> allocate(String consumerGroup, String currentCID, List<MessageQueue> mqAll, List<String> cidAll) {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "一致性 hash 分配策略";
    }
}
