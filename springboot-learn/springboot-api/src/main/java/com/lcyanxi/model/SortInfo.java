package com.lcyanxi.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2022/05/30/3:50 下午
 */
@Builder
@Data
public class SortInfo {

    private boolean isNewFlag;

    private int difficulty;

    private long createdAt;
}
