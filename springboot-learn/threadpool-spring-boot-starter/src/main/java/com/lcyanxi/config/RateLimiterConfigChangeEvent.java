package com.lcyanxi.config;

import com.lcyanxi.ratelimiter.MyRateLimiterConfig;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author lichang
 * @date 2021/3/8
 */
public class RateLimiterConfigChangeEvent  extends ApplicationEvent {
    @Getter
    private final MyRateLimiterConfig config;

    public RateLimiterConfigChangeEvent(MyRateLimiterConfig config) {
        super(config);
        this.config = config;
    }

}
