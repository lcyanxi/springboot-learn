package com.lcyanxi.basics.designPatterns.template;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2022/04/21/6:04 下午
 */
public class LocalSetting extends AbstractSetting {

    private Map<String, String> cache = new HashMap<>();

    protected String lookupCache(String key) {
        return cache.get(key);
    }

    protected void putIntoCache(String key, String value) {
        cache.put(key, value);
    }
}
