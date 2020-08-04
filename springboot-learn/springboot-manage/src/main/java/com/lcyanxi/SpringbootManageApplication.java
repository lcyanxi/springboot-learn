package com.lcyanxi;


import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableDubbo
@DubboComponentScan
public class SpringbootManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootManageApplication.class, args);
    }

}
