package com.lcyanxi.finish;

import lombok.Builder;
import lombok.Data;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/11/6:23 下午
 */
@Builder
@Data
public class FinishPageContext {

    private String logId;

    private String userId;

    private long creatAt;
}
