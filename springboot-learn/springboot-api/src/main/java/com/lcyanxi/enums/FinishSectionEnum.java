package com.lcyanxi.enums;

import com.lcyanxi.finish.SectionType;

import lombok.Getter;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/12/6:21 下午
 */
@Getter
public enum FinishSectionEnum implements SectionType {
    MAP(0, "map", "地图"),
    RUN_DATA(1, "runData", "运动数据");

    private int index;
    private String name;
    private String desc;

    FinishSectionEnum(int index, String name, String desc) {
        this.desc = desc;
        this.name = name;
        this.index = index;
    }


    @Override
    public String getType() {
        return name;
    }

    @Override
    public int getIndex() {
        return 0;
    }

}
