package com.lcyanxi.config.rocket;

import com.lcyanxi.consumer.DedupDemoConsumer;
import com.lcyanxi.consumer.OrderlyConsumer;
import com.lcyanxi.consumer.UserLessonConsumer;
import com.lcyanxi.dedup.DedupConcurrentListener;
import com.lcyanxi.dedup.DedupConfig;
import com.lcyanxi.enums.RocketTopicInfoEnum;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * RocketMQ 消费者配置
 */
@Configuration
@EnableConfigurationProperties({ RocketMQProperties.class })
public class RocketMqConfig {

    private RocketMQProperties properties;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Resource
    private UserLessonConsumer userLessonConsumer;

    @Resource
    private OrderlyConsumer orderlyConsumer;

    @Resource
    private DedupDemoConsumer dedupDemoConsumer;



    public RocketMqConfig(RocketMQProperties properties) {
        this.properties = properties;
    }


    /**
     * PushConsumer为了保证消息肯定消费成功，只有使用方明确表示消费成功，RocketMQ才会认为消息消费成功。
     * 中途断电，抛出异常等都不会认为成功——即都会重新投递。
     * @return DefaultMQPushConsumer
     */
    @Bean
    public DefaultMQPushConsumer getRocketMQConsumer() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(properties.getGroupName());
        consumer.registerMessageListener(userLessonConsumer);
        try{
            consumer.subscribe(RocketTopicInfoEnum.USER_LESSON_TOPIC.getTopic(), "*");
            rocketMQConsumerUtil(consumer);
            consumer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
        return consumer;
    }

    @Bean
    public DefaultMQPushConsumer getDedupDemoRocketMQConsumer() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("send_dedup_group");
        try{
            consumer.subscribe(RocketTopicInfoEnum.SEND_DEDUP_TOPIC.getTopic(), "*");
            rocketMQConsumerUtil(consumer);
            // 开启消息幂等操作
            DedupConfig dedupConfig = DedupConfig.enableDedupConsumeConfig("springboot-worker", jdbcTemplate);
            dedupDemoConsumer.setDedupConfig(dedupConfig);
            consumer.registerMessageListener(dedupDemoConsumer);
            consumer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
        return consumer;
    }

    private void rocketMQConsumerUtil(DefaultMQPushConsumer consumer) {
        consumer.setNamesrvAddr(properties.getNamesrvAddr());
        consumer.setConsumeThreadMin(properties.getConsumerConsumeThreadMin());
        consumer.setConsumeThreadMax(properties.getConsumerConsumeThreadMax());
        consumer.setVipChannelEnabled(false);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setConsumeMessageBatchMaxSize(properties.getConsumerConsumeMessageBatchMaxSize());
    }

}
