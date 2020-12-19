package com.lcyanxi.dictionary;

import com.lcyanxi.limit.dictionary.InitApolloLocalCacheBeanPostProcessor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author lichang
 * @date 2020/12/19
 */
@Component
public class ApolloDictionary {

    /**
     * 获取配置，如果传入的是key是null或者""，直接返回null
     * @param key
     */
    public String get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return InitApolloLocalCache.get(key);
    }
}
