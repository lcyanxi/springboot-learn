package com.lcyanxi.finish;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/10/11:37 上午
 */
@Data
@NoArgsConstructor
public class BaseSection implements ISection{

    private String getType;

    private String getName;

    private int getIndex;

    public BaseSection(SectionType sectionType) {
        this.getType = sectionType.getType();
        this.getIndex = sectionType.getIndex();
        this.getName = sectionType.getName();
    }
}
