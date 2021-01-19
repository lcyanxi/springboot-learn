package com.lcyanxi.model;

import java.io.Serializable;
import lombok.Data;

/**
 * @author lichang
 * @date 2021/1/19
 */
@Data
public class Course implements Serializable {

    private Long cid;

    private String cname;

    private Long userId;

    private String cstatus;
}
