package com.lcyanxi.serviceImpl.order;

import java.util.UUID;

/**
 * @author chang.li
 * @version 1.0
 * @date 2025/7/28
 */
public class OrderServiceTest {
    public static void main(String[] args) {
        OrderIdGeneratorService orderService = new OrderIdGeneratorService(1L, 1L);
        String orderNumber = orderService.generateOrderNumber("01");
        System.out.println("生成的订单号：" + orderNumber);
        System.out.println("UUID 生成订单号："+UUID.randomUUID().toString());
    }
}
