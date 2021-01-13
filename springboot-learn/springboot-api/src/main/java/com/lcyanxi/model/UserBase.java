package com.lcyanxi.model;

import java.util.Date;
import lombok.Data;

/**
 * @author lichang
 * @date 2021/1/13
 */
@Data
public class UserBase {
    /**
     * 用户名
     */
    private String userName;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 增加时间
     */
    private Date addTime;

    @Override
    public String toString() {
        return "UserBase{" +
                "userName='" + userName + '\'' +
                ", age=" + age +
                ", addTime=" + addTime +
                '}';
    }
}
