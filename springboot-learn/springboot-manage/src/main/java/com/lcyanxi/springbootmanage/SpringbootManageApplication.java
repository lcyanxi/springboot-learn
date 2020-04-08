package com.lcyanxi.springbootmanage;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@EnableDubbo
@ComponentScan(basePackages = {"com.lcyanxi.controller"})
@ImportResource("classpath:dubbo_config/springboot-manage-consumer.xml")
public class SpringbootManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootManageApplication.class, args);
    }

}
