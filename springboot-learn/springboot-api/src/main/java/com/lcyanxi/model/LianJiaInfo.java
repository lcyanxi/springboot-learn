package com.lcyanxi.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author lichang
 * Date: 2021/07/08/11:24 上午
 */
@Data
@Builder
public class LianJiaInfo {
    /**
     * 房屋名称
     */
    private String  title;

    /**
     * 出租类型
     */
    private String rentType;

    /**
     * 价格
     */
    private String  price;

    /**
     * 楼层
     */
    private String floor;

    /**
     * 房屋面积
     */
    private String area;

    /**
     * 户型
     */
    private String houseType;

    /**
     * 朝向
     */
    private String orientation;

    /**
     * 电梯
     */
    private String elevator;

    /**
     * 房屋url
     */
    private String houseUrl;

    /**
     * 房屋发布时间
     */
    private String pushTime;

    @Override
    public String toString() {
        return "LianJiaInfo{" +
                "title='" + title + '\'' +
                ", rentType='" + rentType + '\'' +
                ", price='" + price + '\'' +
                ", floor='" + floor + '\'' +
                ", area='" + area + '\'' +
                ", houseType='" + houseType + '\'' +
                ", orientation='" + orientation + '\'' +
                ", elevator='" + elevator + '\'' +
                ", houseUrl='" + houseUrl + '\'' +
                ", pushTime='" + pushTime + '\'' +
                '}';
    }
}
