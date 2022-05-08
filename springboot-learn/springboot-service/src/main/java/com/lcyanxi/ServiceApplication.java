package com.lcyanxi;

import com.alicp.jetcache.autoconfigure.JetCacheAutoConfiguration;
import com.lcyanxi.dubbolimit.annotation.EnableDubboLimit;
import com.lcyanxi.redis.annotation.EnableCacheConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication(exclude = {
        JetCacheAutoConfiguration.class
})
@EnableDubboLimit
@EnableCacheConfiguration
public class ServiceApplication {

    public static void main(String[] args) throws Exception{
        SpringApplication.run(ServiceApplication.class, args);
        Thread.sleep(Long.MAX_VALUE);
        //pom中没有加spring-boot-starter-web依赖，启动时没有tomcat容器，会自动退出，所以加了一个sleep防止自动退出
    }

}
