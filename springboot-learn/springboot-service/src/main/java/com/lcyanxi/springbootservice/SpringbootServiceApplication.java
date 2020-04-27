package com.lcyanxi.springbootservice;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@EnableDubbo
@MapperScan("com.lcyanxi.dto")
@ComponentScan(basePackages = {"com.lcyanxi.service.impl"})
@ImportResource("classpath:dubbo_config/springboot-service-provider.xml")
public class SpringbootServiceApplication {

    public static void main(String[] args) throws Exception{
        SpringApplication.run(SpringbootServiceApplication.class, args);
        Thread.sleep(Long.MAX_VALUE);
        //pom中没有加spring-boot-starter-web依赖，启动时没有tomcat容器，会自动退出，所以加了一个sleep防止自动退出
    }

}
