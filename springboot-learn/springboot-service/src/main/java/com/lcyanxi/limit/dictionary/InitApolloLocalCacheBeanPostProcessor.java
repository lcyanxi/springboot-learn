package com.lcyanxi.limit.dictionary;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.google.common.collect.Maps;
import com.lcyanxi.limit.constent.GuavaRateLimiterKeys;
import static com.lcyanxi.limit.constent.GuavaRateLimiterKeys.INIT_PROJECT_DEFAULT_NAMESPACE;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author lichang
 * @date 2020/7/9
 */
@Slf4j
@Component
public class InitApolloLocalCacheBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

    /**
     * local cache
     */
    private static final Map<String, String> LOCAL_CACHE_MAP = new ConcurrentHashMap<>();

    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if (event.getApplicationContext().getParent() != null) {
                return;
            }
            Config config = ConfigService.getConfig(GuavaRateLimiterKeys.CONFIG_JWYTH_DATA_DICTIONARY);
            if (Objects.isNull(config)) {
                return;
            }
            Set<String> propertyNames = config.getPropertyNames();
            if (CollectionUtils.isEmpty(propertyNames)) {
                return;
            }
            propertyNames.forEach(propertyName -> {
                String value = config.getProperty(propertyName, "");
                LOCAL_CACHE_MAP.put(propertyName, value);
            });
            applicationContext.publishEvent(new ApolloChangeEvent(new ConfigChangeEvent(INIT_PROJECT_DEFAULT_NAMESPACE, Maps.newHashMap())));
            config.addChangeListener(changeEvent -> {
                Set<String> changedKeys = changeEvent.changedKeys();
                changedKeys.forEach(key -> {
                    ConfigChange change = changeEvent.getChange(key);
                    String newValue = change.getNewValue();
                    String oldVale = LOCAL_CACHE_MAP.get(key);
                    LOCAL_CACHE_MAP.put(key, newValue);
                    log.info("initApolloLocalCache namespace is application listener key:[{}],oldValue:[{}],newValue:[{}]",key,oldVale,newValue);
                });
                applicationContext.publishEvent(new ApolloChangeEvent(changeEvent));
            });
            Set<String> keySet = LOCAL_CACHE_MAP.keySet();

            log.info("###############################################");
            log.info("#					                            #");
            keySet.forEach(s -> {
                String value = LOCAL_CACHE_MAP.get(s);
                log.info("#	 sysDictConfService namespace is application init key:{} -> value:{}   #", s, value);
            });
            log.info("#					                            #");
            log.info("###############################################");
        } catch (Exception e) {
            log.error("dictConfService namespace is application apollo is error,e ", e);
        }
    }


    /**
     * @param key
     * @return
     */
    public static String get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return LOCAL_CACHE_MAP.get(key);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
