package com.lcyanxi.model;

import lombok.Data;

@Data
public class TeacherInfoVO {
    private Integer teacherId;
    private Integer ssoUserId;
    private String teacherName;
    private String courseTypeNames;

}
