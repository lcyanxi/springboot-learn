package com.lcyanxi.config.rocket;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lichang
 * @date 2020/12/28
 */
@Data
@ConfigurationProperties(prefix = "rocketmq")
public class RocketMQProperties {
    private String namesrvAddr = "lcyanxi.com:9876";
    private String groupName = "lcyanxi-worker-consumer";
    private int producerMaxMessageSize = 1024;
    private int producerSendMsgTimeout = 2000;
    private int producerRetryTimesWhenSendFailed = 2;
    private int consumerConsumeThreadMin = 5;
    private int consumerConsumeThreadMax = 30;
    private int consumerConsumeMessageBatchMaxSize = 1;
}
