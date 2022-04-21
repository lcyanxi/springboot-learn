package com.lcyanxi.basics.designPatterns.template;

/**
 * @author : lichang
 * @desc : 模版方法：父类定义骨架，子类实现某些细节
 * @since : 2022/04/21/6:03 下午
 */
public abstract class AbstractSetting {
    public final String getSetting(String key) {
        String value = lookupCache(key);
        if (value == null) {
            value = readFromDatabase(key);
            putIntoCache(key, value);
        }
        return value;
    }

    private String readFromDatabase(String key) {
        // TODO: 从数据库读取
        return key;
    }

    protected abstract String lookupCache(String key);

    protected abstract void putIntoCache(String key, String value);
}
