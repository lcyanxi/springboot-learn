package com.lcyanxi.consumer;

import com.lcyanxi.rocketmq.annotation.MQConsumer;
import com.lcyanxi.rocketmq.base.AbstractMQPushConsumer;
import com.lcyanxi.rocketmq.base.MessageExtConst;
import com.lcyanxi.topic.RocketTopic;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.lcyanxi.topic.RocketTopic.SEND_DEDUP_TOPIC;
import static com.lcyanxi.topic.RocketTopic.USER_LESSON_TOPIC;

@Slf4j
@Component
@MQConsumer(consumerGroup = "send-dedup-topic-consumer", topic = SEND_DEDUP_TOPIC )
public class UserLessonConsumer extends AbstractMQPushConsumer<String> {
    @Override
    public boolean process(String s, Map<String, Object> map) {
        log.info("userLessonConsumer process msg:{}，map:{}",s,map);
        return true;
    }
}
