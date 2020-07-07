package com.lcyanxi.config;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketProducerConfig {

    @Value("${rocketmq.producer.groupName}")
    private String groupName;
    @Value("${rocketmq.producer.namesrvAddr}")
    private String namesrvAddr;
    @Value("${rocketmq.producer.maxMessageSize}")
    private Integer maxMessageSize ;
    @Value("${rocketmq.producer.sendMsgTimeout}")
    private Integer sendMsgTimeout;
    @Value("${rocketmq.producer.retryTimesWhenSendFailed}")
    private Integer retryTimesWhenSendFailed;

    @Bean
    public DefaultMQProducer getRocketMQProducer() {
        DefaultMQProducer producer;
        producer = new DefaultMQProducer(this.groupName);
        producer.setNamesrvAddr(this.namesrvAddr);
        producer.setVipChannelEnabled(false);
        //如果需要同一个jvm中不同的producer往不同的mq集群发送消息，需要设置不同的instanceName
        if(this.maxMessageSize != null){
            producer.setMaxMessageSize(this.maxMessageSize);
        }
        if(this.sendMsgTimeout != null){
            producer.setSendMsgTimeout(this.sendMsgTimeout);
        }
        //如果发送消息失败，设置重试次数，默认为2次
        if(this.retryTimesWhenSendFailed != null){
            producer.setRetryTimesWhenSendFailed(this.retryTimesWhenSendFailed);
        }
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        return producer;
    }

//
//    @Bean("OrderlyRocketMQProducer")
//    public DefaultMQProducer getOrderlyRocketMQProducer() {
//        DefaultMQProducer producer = new DefaultMQProducer("lcyanxi-orderly");
//        producer.setNamesrvAddr(this.namesrvAddr);
//        producer.setVipChannelEnabled(false);
//        //如果需要同一个jvm中不同的producer往不同的mq集群发送消息，需要设置不同的instanceName
//        if(this.maxMessageSize != null){
//            producer.setMaxMessageSize(this.maxMessageSize);
//        }
//        if(this.sendMsgTimeout != null){
//            producer.setSendMsgTimeout(this.sendMsgTimeout);
//        }
//        //如果发送消息失败，设置重试次数，默认为2次
//        if(this.retryTimesWhenSendFailed != null){
//            producer.setRetryTimesWhenSendFailed(this.retryTimesWhenSendFailed);
//        }
//        try {
//            producer.start();
//        } catch (MQClientException e) {
//            e.printStackTrace();
//        }
//        return producer;
//    }


}
