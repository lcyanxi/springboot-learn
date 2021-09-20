package com.lcyanxi;



import com.alicp.jetcache.autoconfigure.JetCacheAutoConfiguration;
import com.lcyanxi.annotation.EnableCacheConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(exclude = {
        JetCacheAutoConfiguration.class
})
@EnableCacheConfiguration
public class ManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManageApplication.class, args);
    }

}
