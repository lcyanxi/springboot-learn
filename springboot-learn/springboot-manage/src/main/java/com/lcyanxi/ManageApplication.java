package com.lcyanxi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.lcyanxi.config.MyApplicationListener;
import com.lcyanxi.rocketmq.annotation.EnableMQConfiguration;


@SpringBootApplication
//@EnableCacheConfiguration
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
