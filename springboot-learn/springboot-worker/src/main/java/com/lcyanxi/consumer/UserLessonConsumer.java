package com.lcyanxi.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.lcyanxi.canal.CanalListener;
import com.lcyanxi.model.UserLesson;
import com.lcyanxi.config.redis.RedisClient;
import com.lcyanxi.util.lock.ConcurrentLeaseLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserLessonConsumer implements MessageListenerConcurrently {

    @Autowired
    private RedisClient redisClient;

    @Override
//    @ConcurrentLeaseLock(lockKey = "consumeMessage::lock")
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        MessageExt messageExt = list.get(0);
        log.info("topic:[{}] 接受到的消息为:[{}]",messageExt.getTopic(),messageExt);
//        log.info("接受到的消息为22：{}",userLesson);
//        redisClient.set("consume", userLesson.getUserId());
        // 消息消费成功
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }

}
