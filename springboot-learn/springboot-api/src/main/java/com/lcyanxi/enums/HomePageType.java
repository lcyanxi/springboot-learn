package com.lcyanxi.enums;

import lombok.Getter;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/07/3:34 下午
 */
@Getter
public enum HomePageType {
    IPHONE("iphone", "手机"),
    TV("tv", "洗衣机"),
    WATCH("watch", "手表");

    private String name;
    private String desc;

    HomePageType(String name, String desc) {
        this.desc = desc;
        this.name = name;
    }

    public static HomePageType parseByType(String type) {
        for (HomePageType pageType : HomePageType.values()) {
            if (pageType.getName().equals(type)) {
                return pageType;
            }
        }
        return null;
    }
}
