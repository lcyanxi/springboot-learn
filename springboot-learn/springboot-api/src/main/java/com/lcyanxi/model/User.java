package com.lcyanxi.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lichang
 * @date 2020/11/28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
