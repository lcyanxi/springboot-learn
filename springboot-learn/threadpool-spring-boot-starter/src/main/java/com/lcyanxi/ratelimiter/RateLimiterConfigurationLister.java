package com.lcyanxi.ratelimiter;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lichang
 * @date 2021/3/8
 */
@Configuration
@EnableApolloConfig("ratelimiter.yaml")
public class RateLimiterConfigurationLister {
    @Bean
    public MyRateLimiterConfig rateLimiterConfig() {
        return new MyRateLimiterConfig();
    }
}
