package com.lcyanxi.model;


import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ExcelTarget("saleOrder")
public class SaleOrder {
    @Excel(name = "网店订单号")
    private String orderNo;

    @Excel(name = "分摊后金额")
    private double totalSaleMoney;

    @Excel(name = "发货时间")
    private String saleTime;

    @Excel(name = "货品编号")
    private String productNum;

    @Excel(name = "货品名称")
    private String productName;

    @Excel(name = "订单类型")
    private String orderType;

    @Excel(name = "订单状态")
    private String orderStatus;
}
