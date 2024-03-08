package com.lcyanxi.home;

import lombok.Builder;
import lombok.Data;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/07/4:40 下午
 */
@Builder
@Data
public class DeviceInfo {
    private String mac;
    private String sn;
    /**
     * yyMMdd
     */
    private String bindDate;
}
