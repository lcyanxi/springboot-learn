package com.lcyanxi.config.rocketConfig;

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
    private String groupName = "lcyanxi-manage-producer";
    private int producerMaxMessageSize = 1024;
    private int producerSendMsgTimeout = 3000;
    private int retryTimesWhenSendFailed = 2;
}
