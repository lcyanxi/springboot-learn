package com.lcyanxi.consumer;

import com.alibaba.fastjson.JSON;
import java.util.List;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 顺序消费:
 * 由于顺序消费是要前者消费成功才能继续消费，所以没有RECONSUME_LATER的这个状态，
 * 只有SUSPEND_CURRENT_QUEUE_A_MOMENT来暂停队列的其余消费，直到原消息不断重试成功为止才能继续消费。
 */
@Component
public class OrderlyConsumer implements MessageListenerOrderly {

    private static final Logger LOG = LoggerFactory.getLogger(OrderlyConsumer.class) ;

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        if (CollectionUtils.isEmpty(msgs)){
            return ConsumeOrderlyStatus.SUCCESS;
        }
        LOG.info("接受到的消息为 message:{}", JSON.toJSONString(msgs));

        int a = 0 ;
        int b = 1 ;
        int c = b/a ;

        return ConsumeOrderlyStatus.SUCCESS;
    }
}
