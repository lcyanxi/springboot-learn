package com.lcyanxi.dictionary;


import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @author lichang
 * @date 2021/1/29
 */
@Data
public class MyRateLimiterConfig implements Serializable {

    private List<MyPredicateConfig> configs;


    @Data
    public static class MyPredicateConfig  {

        private String predicateType;

        private Map<String, Object> params;

        private ResourceRateLimiterConfig rateLimiterConfig;
    }

    /**
     * 资源限流配置类
     */
    @Data
    public static class ResourceRateLimiterConfig {

        /**
         * 令牌数
         */
        private double permits;

        /**
         * 获取令牌的最大等待时间，默认是0，获取不到立刻失败
         */
        private long waitTimeMillis;


        private String resourceSelector;

    }
}
