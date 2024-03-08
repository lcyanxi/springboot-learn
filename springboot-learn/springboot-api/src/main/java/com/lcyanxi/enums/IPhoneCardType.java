package com.lcyanxi.enums;

import com.lcyanxi.home.CardType;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/07/4:30 下午
 */
public enum IPhoneCardType implements CardType {
    SECTION_ONE(10, "section_1", "模块一"),
    SECTION_TWO(20, "section_2", "模块二"),
    SECTION_THREE(5, "section_3", "模块三");

    private final int index;
    private final String sectionType;
    private final String sectionName;

    IPhoneCardType(int index, String sectionType, String sectionName) {
        this.index = index;
        this.sectionType = sectionType;
        this.sectionName = sectionName;
    }


    @Override
    public String getType() {
        return sectionType;
    }

    @Override
    public String getName() {
        return sectionName;
    }

    @Override
    public int getIndex() {
        return index;
    }
}
