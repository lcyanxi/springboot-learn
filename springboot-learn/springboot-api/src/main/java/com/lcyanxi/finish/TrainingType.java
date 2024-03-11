package com.lcyanxi.finish;

import lombok.Getter;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/10/11:25 上午
 */
@Getter
public enum TrainingType {
    Run("跑步", "run"),
    Cyc("骑行", "cyc"),
    Hik("徒步", "hik");

    private String desc;
    private String type;

    TrainingType(String desc, String type) {
        this.desc = desc;
        this.type = type;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getType() {
        return this.type;
    }
}
