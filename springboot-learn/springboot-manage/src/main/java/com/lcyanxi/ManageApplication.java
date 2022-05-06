package com.lcyanxi;



import com.alicp.jetcache.autoconfigure.JetCacheAutoConfiguration;
import com.lcyanxi.annotation.EnableCacheConfiguration;

import com.lcyanxi.rocketmq.annotation.EnableMQConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


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
