package com.lcyanxi.dictionary;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Spring容器初始化完成后触发事件将apollo的配置信息加载到本地local cache
 * @author lichang
 * @date 2020/12/19
 */
@Slf4j
@Component
public class InitApolloLocalCache implements ApplicationListener<ContextRefreshedEvent> {
    // ContextRefreshedEvent 事件会在Spring容器初始化完成会触发该事件
    // namespace
    static final String LOAD_NAMESPACE = "commonConfig";
    /**
     * local cache
     */
    private static final Map<String, String> LOCAL_CACHE_MAP = new ConcurrentHashMap<>(16);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("===========initApolloLocalCache onApplicationEvent start=====================");
        try{
            if (event.getApplicationContext().getParent() != null) {
                return;
            }
            // 获取指定namespace下的配置
            Config config = ConfigService.getConfig(LOAD_NAMESPACE);
            if (Objects.isNull(config)) {
                return;
            }
            Set<String> propertyNames = config.getPropertyNames();
            if (CollectionUtils.isEmpty(propertyNames)){
                return;
            }
            propertyNames.forEach(propertyName -> {
                String value = config.getProperty(propertyName, "");
                LOCAL_CACHE_MAP.put(propertyName, value);
            });
            config.addChangeListener(changeEvent -> {
                Set<String> changedKeys = changeEvent.changedKeys();
                changedKeys.forEach(key -> {
                    ConfigChange change = changeEvent.getChange(key);
                    String newValue = change.getNewValue();
                    String oldVale = LOCAL_CACHE_MAP.get(key);
                    log.info("initApolloLocalCache namespace is commonConfig listener key:[{}],oldValue:[{}],newValue:[{}]",key,oldVale,newValue);
                    LOCAL_CACHE_MAP.put(key, newValue);
                });
            });

            Set<String> keySet = LOCAL_CACHE_MAP.keySet();

            log.info("###############################################");
            log.info("#					                            #");
            keySet.forEach(s -> {
                String value = LOCAL_CACHE_MAP.get(s);
                log.info("#	 initApolloLocalCache namespace is commonConfig init key:[{}] -> value:[{}]   #", s, value);
            });
            log.info("#					                            #");
            log.info("###############################################");

        }catch (Exception e){
            log.error("initApolloLocalCache namespace is commonConfig apollo is error,e ", e);
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
}
