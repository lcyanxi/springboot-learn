package com.lcyanxi.model;

import java.io.Serializable;
import lombok.Data;

/**
 * @author lichang
 * @date 2020/12/14
 */
@Data
public class User1 implements Serializable,Cloneable {

    private Integer id;


    private String name;
}
