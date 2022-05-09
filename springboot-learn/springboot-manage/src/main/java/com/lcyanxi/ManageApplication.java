package com.lcyanxi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alicp.jetcache.autoconfigure.JetCacheAutoConfiguration;
import com.lcyanxi.redis.annotation.EnableCacheConfiguration;
import com.lcyanxi.rocketmq.annotation.EnableMQConfiguration;


@SpringBootApplication(exclude = {
        JetCacheAutoConfiguration.class
})
@EnableCacheConfiguration
@EnableMQConfiguration
public class ManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManageApplication.class, args);
    }

}
