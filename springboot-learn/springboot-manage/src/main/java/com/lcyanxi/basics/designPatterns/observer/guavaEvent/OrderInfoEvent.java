package com.lcyanxi.basics.designPatterns.observer.guavaEvent;

import lombok.Data;

/**
 * @author lichang
 * @date 2021/1/2
 */
@Data
public class OrderInfoEvent {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 产品ID
     */
    private Integer productId;
    /**
     * 产品名称
     */
    private String productName;
}
