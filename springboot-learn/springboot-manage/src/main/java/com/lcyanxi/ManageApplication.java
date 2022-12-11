package com.lcyanxi;

import com.lcyanxi.config.MyApplicationListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alicp.jetcache.autoconfigure.JetCacheAutoConfiguration;
import com.lcyanxi.redis.annotation.EnableCacheConfiguration;
import com.lcyanxi.rocketmq.annotation.EnableMQConfiguration;
import org.springframework.context.annotation.Bean;


@SpringBootApplication(exclude = {
        JetCacheAutoConfiguration.class
})
@EnableCacheConfiguration
@EnableMQConfiguration
public class ManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManageApplication.class, args);
    }
    @Bean
    public MyApplicationListener myApplicationListener(){
        return new MyApplicationListener();
    }
}
