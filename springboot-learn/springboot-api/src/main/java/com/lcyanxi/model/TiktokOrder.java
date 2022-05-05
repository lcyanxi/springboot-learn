package com.lcyanxi.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ExcelTarget("tiktokOrder")
public class TiktokOrder {
    // 订单号
    @Excel(name = "订单号")
    private String orderNo;
    // 总收入
    @Excel(name = "结算金额")
    private double totalMoney;
    // 用户总支付钱
    @Excel(name = "收入合计")
    private double userPlayMoney;
    // 服务费用
    @Excel(name = "支出合计")
    private double serverMoney;
}
