package com.lcyanxi.model;

import java.io.Serializable;
import lombok.Data;

/**
 * @author lichang
 * @date 2020/11/28
 */
@Data
public class User implements Serializable,Cloneable {
    private String id;
    private String userName;
    private Integer userId;
    private String password;

    static {
        System.out.println("User static start");
    }

    public User() {
    }

    private User(String id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "User{}";
    }
}
