package com.lcyanxi.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lichang
 * @date 2020/12/14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User1 implements Serializable,Cloneable {

    private Integer id;


    private String name;
}
