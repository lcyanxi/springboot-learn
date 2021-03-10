package com.lcyanxi.ratelimiter;

import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.lcyanxi.config.RateLimiterConfigChangeEvent;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author lichang
 * @date 2021/3/8
 */
@Slf4j
@Data
public class MyRateLimiterConfig implements ApplicationEventPublisherAware, ApplicationListener<ContextRefreshedEvent> {

    private List<MyPredicateConfig> configs;

    private boolean enable = true;


    @Setter
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            String content = ConfigService.getConfigFile("ratelimiter", ConfigFileFormat.YAML).getContent();
            MyRateLimiterConfig config;
            if (StringUtils.isEmpty(content)) {
                config = new MyRateLimiterConfig();
            } else {
                config = YamlUtils.readConfig(content);
            }
            this.setConfigs(config.configs);
            if (!CollectionUtils.isEmpty(configs)) {
                AnnotationAwareOrderComparator.sort(configs);
            }
            applicationEventPublisher.publishEvent(new RateLimiterConfigChangeEvent(this));
            this.setEnable(config.enable);
            log.info("MyRateLimiterConfig get ratelimiter is start config:[{}]",content);
        } catch (Exception e) {
            log.error("loadConfigs occur error~~", e);
        }

    }

    /**
     * 如果 predicateType 是Global 则表示都是全局,否则是接口级
     * 如果 resourceSelector 是DubboUpstream 则按不同应用上游做限流
     */
    @Data
    public static class MyPredicateConfig  {

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

    }

    @UtilityClass
    private static class YamlUtils {

        private final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

        static {
            MAPPER.findAndRegisterModules();
        }

        @SneakyThrows
        public MyRateLimiterConfig readConfig(String content) {
            return MAPPER.readValue(content, MyRateLimiterConfig.class);
        }
    }


}
