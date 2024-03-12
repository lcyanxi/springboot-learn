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
public class BaseSection implements ISection {

    private String type;

    private int index;

    private String sectionName;

    public BaseSection(SectionType sectionType) {
        this.type = sectionType.getType();
        this.index = sectionType.getIndex();
        this.sectionName = sectionType.getName();
    }
}
