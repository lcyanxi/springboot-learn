package com.lcyanxi.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ExcelTarget("resultOrderExportData")
public class ResultOrderExportData {

    @Excel(name = "订单号",needMerge = true)
    private String orderNo;

    @ExcelCollection(name = "订单详情")
    private List<ProductInfo> infos;

    @Excel(name = "应收款",needMerge = true)
    private double totalSaleMoney;

    @Excel(name = "用户支付款",needMerge = true)
    private double userPlayMoney;

    @Excel(name = "已收款",needMerge = true)
    private double totalMoney;

    @Excel(name = "平台服务费",needMerge = true)
    private double serverMoney;

    @Excel(name = "回款状态",needMerge = true)
    private String status;

    @Builder
    @Data
    public static class ProductInfo {
        @Excel(name = "货品名称")
        private String productName;

        @Excel(name = "货品编号")
        private String productNum;

        @Excel(name = "应收款",needMerge = true)
        private double totalSaleMoney;

        @Excel(name = "发货时间")
        private String saleTime;

        @Excel(name = "订单类型")
        private String orderType;

        @Excel(name = "订单状态")
        private String orderStatus;
    }
}
