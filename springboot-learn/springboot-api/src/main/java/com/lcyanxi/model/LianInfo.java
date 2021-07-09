package com.lcyanxi.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author lichang
 * Date: 2021/07/09/11:24 上午
 */
@Data
@Builder
public class LianInfo {

    /**
     * 房屋链接
     */
    private String hoursUrl;

    /**
     * 地址
     */
    private String address;
}
