package com.lcyanxi.serviceImpl;

import com.lcyanxi.rocketmq.base.AbstractMQProducer;
import com.lcyanxi.rocketmq.base.MQException;
import com.lcyanxi.rocketmq.base.Result;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2021/10/13/10:41 下午
 */
@com.lcyanxi.rocketmq.annotation.MQProducer
public class MQProducer extends AbstractMQProducer {
    @Override
    public Result<SendResult> syncSend(Message message) throws MQException {
        return super.syncSend(message);
    }

    @Override
    public Result<SendResult> syncSendOrderly(Message message, String hashKey) throws MQException {
        return super.syncSendOrderly(message, hashKey);
    }

    @Override
    public void doAfterSyncSend(Message message, org.apache.rocketmq.client.producer.SendResult sendResult) {
        super.doAfterSyncSend(message, sendResult);
    }

    @Override
    public void asyncSend(Message message, org.apache.rocketmq.client.producer.SendCallback sendCallback) throws MQException {
        super.asyncSend(message, sendCallback);
    }
}
