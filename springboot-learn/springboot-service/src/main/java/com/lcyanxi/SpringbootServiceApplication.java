package com.lcyanxi;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class SpringbootServiceApplication {

    public static void main(String[] args) throws Exception{
        SpringApplication.run(SpringbootServiceApplication.class, args);
        Thread.sleep(Long.MAX_VALUE);
        //pom中没有加spring-boot-starter-web依赖，启动时没有tomcat容器，会自动退出，所以加了一个sleep防止自动退出
    }

}
