package com.lcyanxi.finish;

import lombok.Builder;
import lombok.Data;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/10/11:27 上午
 */
@Builder
@Data
public class FinishPageReq {
    private String logId;
    private String userId;
    private String clientVersion;
    private String timezone;
    private String os;
    private long timestamp;
}
