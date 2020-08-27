package com.lcyanxi.canal;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lichang
 * @date 2020/8/27
 */
@Data
@Configuration
@ConditionalOnProperty(value = "enabled", matchIfMissing = true)
@ConfigurationProperties(prefix = "com.lcyanxi.canal")
public class CanalProperties {


    /**
     * cancal服务ip
     */
    private String hostname = "127.0.0.1";

    /**
     * 端口
     */
    private Integer port = 11111;

    /**
     * 名字
     */
    private String destination="example";

    /**
     * canal服务端用户名
     */
    private String username = "";
    /**
     * canal服务端密码
     */
    private String password = "";

    /**
     * 数据库表名过滤规则
     */
    private String filter = ".*\\..*";

    /**
     * 获取不到数据，休眠时间
     */
    private long sleepTime = 10000;

    /**
     * 一次取出多少条记录
     */
    private Integer batchSize = 1000;
}
