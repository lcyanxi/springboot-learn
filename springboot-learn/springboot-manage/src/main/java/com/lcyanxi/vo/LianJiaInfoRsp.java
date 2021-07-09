package com.lcyanxi.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author lichang
 * Date: 2021/07/08/11:24 上午
 */
@Data
@Builder
public class LianJiaInfoRsp {
    /**
     * 房屋名称
     */
    @ExcelProperty(value = "房屋名称")
    private String  title;

    /**
     * 出租类型
     */
    @ExcelProperty(value = "出租类型")
    private String rentType;

    /**
     * 价格
     */
    @ExcelProperty(value = "价格")
    private String  price;

    /**
     * 楼层
     */
    @ExcelProperty(value = "楼层")
    private String floor;

    /**
     * 房屋面积
     */
    @ExcelProperty(value = "房屋面积")
    private String area;

    /**
     * 户型
     */
    @ExcelProperty(value = "户型")
    private String houseType;

    /**
     * 朝向
     */
    @ExcelProperty(value = "朝向")
    private String orientation;

    /**
     * 电梯
     */
    @ExcelProperty(value = "电梯")
    private String elevator;

    /**
     * 房屋url
     */
    @ExcelProperty(value = "房屋url")
    private String houseUrl;

    /**
     * 房屋发布时间
     */
    @ExcelProperty(value = "房屋发布时间")
    private String pushTime;
}
