package com.lcyanxi.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author lichang
 * Date: 2021/07/08/10:53 上午
 */
@Data
@Builder
public class JobInfo {
    private String teacherName;

    private String title;

    private String job;
}
