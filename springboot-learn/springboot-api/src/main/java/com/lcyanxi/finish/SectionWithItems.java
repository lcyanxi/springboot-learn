package com.lcyanxi.finish;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/10/11:42 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SectionWithItems<T> extends BaseSection {
    private List<T> items;

    private Map<String, String> tracks;

    public SectionWithItems(SectionType sectionType) {
        super(sectionType);
    }
}
