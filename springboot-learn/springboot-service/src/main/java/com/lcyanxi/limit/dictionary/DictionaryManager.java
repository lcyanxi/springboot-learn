package com.lcyanxi.limit.dictionary;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author lichang
 * @date 2020/7/11
 */
@Component
public class DictionaryManager {

    /**
     * 获取配置，如果传入的是key是null或者""，直接返回null
     *
     * @param key
     */
    public String get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return InitApolloLocalCacheBeanPostProcessor.get(key);
    }
}
