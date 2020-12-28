package com.lcyanxi.config.rocketConfig;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ RocketMQProperties.class })
public class RocketMqConfig {
    private RocketMQProperties properties;

    public RocketMqConfig(RocketMQProperties properties) {
        this.properties = properties;
    }

    @Bean
    public DefaultMQProducer getRocketMQProducer() {
        DefaultMQProducer producer;
        producer = new DefaultMQProducer(properties.getGroupName());
        producer.setNamesrvAddr(properties.getNamesrvAddr());
        producer.setVipChannelEnabled(false);
        //如果需要同一个jvm中不同的producer往不同的mq集群发送消息，需要设置不同的instanceName
        producer.setSendMsgTimeout(properties.getProducerSendMsgTimeout());
        producer.setMaxMessageSize(properties.getProducerMaxMessageSize());

        //如果发送消息失败，设置重试次数，默认为2次
        producer.setRetryTimesWhenSendFailed(properties.getRetryTimesWhenSendFailed());
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        return producer;
    }
}
