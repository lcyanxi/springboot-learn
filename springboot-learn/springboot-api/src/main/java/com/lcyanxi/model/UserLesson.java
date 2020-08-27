package com.lcyanxi.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserLesson implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 班级课程ID
     */
    private Integer classCourseId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 产品ID
     */
    private Integer productId;

    /**
     * 班级ID
     */
    private Integer classId;

    /**
     * 父班级ID
     */
    private Integer parentClassId;

    /**
     * 课次ID（对应pe_class_lesson的主键ID）
     */
    private Integer lessonId;

    /**
     * 购买时间
     */
    private Date buyTime;

    /**
     * 购买状态(0:未购买,1:已购买)
     */
    private Boolean buyStatus;

    /**
     * 状态 ( 1: 生效， 0 失效 ) 后期有可能会删除学习路径节点
     */
    private Integer status = 1;
    private Date createTime;
    /**
     * 创建人账号
     */
    private String createUid;
    private String createUsername;
    private String updateUsername;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 修改人账号
     */
    private String updateUid;

    /**
     * 删除状态 1:已删除；0：未删除
     */
    private Boolean isDeleted = false;


}
