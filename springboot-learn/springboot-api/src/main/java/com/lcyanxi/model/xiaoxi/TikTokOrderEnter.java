package com.lcyanxi.model.xiaoxi;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2022/12/11/4:11 下午
 */
@Data
public class TikTokOrderEnter {

    @Excel(name = "订单编号")
    private String orderCode;

    @Excel(name = "网店订单编号")
    private String orderNo;

    @Excel(name = "订单类型")
    private String orderType;

    @Excel(name = "销售渠道")
    private String origin;

    @Excel(name = "发货时间")
    private String sendTime;

    @Excel(name = "货品名称")
    private String name;

    @Excel(name = "数量")
    private int num;

    @Excel(name = "分摊后金额")
    private double totalMoney;
}
