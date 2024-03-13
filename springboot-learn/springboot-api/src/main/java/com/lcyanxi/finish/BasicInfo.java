package com.lcyanxi.finish;

import lombok.Builder;
import lombok.Data;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/13/10:08 上午
 */
@Data
@Builder
public class BasicInfo {
    private String userName;

    private String userId;

    private String gender;

    private String trainingType;
}
