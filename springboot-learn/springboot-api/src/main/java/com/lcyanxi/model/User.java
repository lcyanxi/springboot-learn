package com.lcyanxi.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * @author lichang
 * @date 2020/11/28
 */
@Data
@Builder
public class User implements Serializable,Cloneable {
    private String id;
    private String userName;
    private Integer userId;
    private String password;

    @Override
    public String toString() {
        return "User{}";
    }
}
