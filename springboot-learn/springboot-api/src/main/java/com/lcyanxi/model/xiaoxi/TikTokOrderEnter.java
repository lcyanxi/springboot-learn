package com.lcyanxi.model.xiaoxi;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2022/12/11/4:11 下午
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ExcelTarget("tikTokOrderEnterData")
public class TikTokOrderEnter {

    @Excel(name = "销售渠道")
    private String origin;

    @Excel(name = "订单编号")
    private String orderCode;

    @Excel(name = "网店订单号")
    private String orderNo;

    @Excel(name = "下单时间")
    private String orderTime;

    // min
    @Excel(name = "发货时间")
    private String sendTime;

    // 不去重
    @Excel(name = "货品编号")
    private String productCode;

    @Excel(name = "货品名称")
    private String name;

    @Excel(name = "数量")
    private int num;

    @Excel(name = "分摊后金额")
    private double totalMoney;

    @Excel(name = "订单类型")
    private String orderType;

    // 去重
    @Excel(name = "订单来源")
    private String orderOrigin;

    private String groupId;
}
