package com.lcyanxi.consumer;

import com.lcyanxi.rocketmq.annotation.MQConsumer;
import com.lcyanxi.rocketmq.base.AbstractMQPushConsumer;
import com.lcyanxi.rocketmq.base.MessageExtConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.lcyanxi.topic.RocketTopic.USER_LESSON_TOPIC;

/**
 * 顺序消费:
 * 由于顺序消费是要前者消费成功才能继续消费，所以没有RECONSUME_LATER的这个状态，
 * 只有SUSPEND_CURRENT_QUEUE_A_MOMENT来暂停队列的其余消费，直到原消息不断重试成功为止才能继续消费。
 */
@Slf4j
@Component
@MQConsumer(consumerGroup = "user-lesson-topic-consumer", topic = USER_LESSON_TOPIC, consumeMode = MessageExtConst.CONSUME_MODE_ORDERLY )
public class OrderlyConsumer extends AbstractMQPushConsumer<String> {
    @Override
    public boolean process(String s, Map<String, Object> map) {
        log.info("orderlyConsumer process msg:{}",s);
        return Boolean.TRUE;
    }
}
