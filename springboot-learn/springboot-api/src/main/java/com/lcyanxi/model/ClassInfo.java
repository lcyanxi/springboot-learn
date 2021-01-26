package com.lcyanxi.model;

import java.io.Serializable;
import lombok.Data;

/**
 * @author lichang
 * @date 2021/1/26
 */
@Data
public class ClassInfo implements Serializable {
    private Integer id;

    private Integer classId;

    private Integer parentClassId;

    private Integer stock;

}
