package com.lcyanxi.dictionary;

import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author lichang
 * @date 2021/1/29
 */
@Slf4j
@Component
public class RateLimiterConfigListener implements ApplicationEventPublisherAware, ApplicationListener<ContextRefreshedEvent> {


    @Setter
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        loadConfigs();
    }

    private void loadConfigs() {
        try {
            String content = ConfigService.getConfigFile("ratelimiter", ConfigFileFormat.YAML).getContent();
            MyRateLimiterConfig config;
            if (StringUtils.isEmpty(content)) {
                config = new MyRateLimiterConfig();
            } else {
                config = YamlUtils.readConfig(content);
            }
            log.error("配置变动成功:[{}]",config);
//            applicationEventPublisher.publishEvent(new RateLimiterConfigChangeEvent(this));
        } catch (Exception e) {
            log.error("RateLimiterConfigListener loadConfigs occur error~~", e);
        }
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
