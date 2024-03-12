package com.lcyanxi.finish.section;

import lombok.Builder;
import lombok.Data;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/12/6:37 下午
 */
@Builder
@Data
public class RunDataSectionDto {
    private int duration;
    private double distance;
}
