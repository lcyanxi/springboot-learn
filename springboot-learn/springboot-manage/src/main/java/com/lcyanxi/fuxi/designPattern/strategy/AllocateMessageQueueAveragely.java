package com.lcyanxi.fuxi.designPattern.strategy;

import org.apache.rocketmq.common.message.MessageQueue;

import java.util.Collections;
import java.util.List;

/**
 * 平均分配
 */
public class AllocateMessageQueueAveragely implements AllocateMessageQueueStrategy{
    @Override
    public List<MessageQueue> allocate(String consumerGroup, String currentCID, List<MessageQueue> mqAll, List<String> cidAll) {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "平均分配";
    }
}
