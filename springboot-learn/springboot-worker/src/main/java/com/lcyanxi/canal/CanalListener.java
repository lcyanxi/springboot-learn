package com.lcyanxi.canal;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lichang
 * @date 2020/8/27
 */

@Target(value = {ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CanalListener {
    /**
     * 实例id
     */
    String id() default "";

    /**
     * 库名
     */
    String databaseName();

    /**
     * 表名
     * @return
     */
    String tableName();

    /**
     * 数据转化类，该类必须交给spring管理
     * @return
     */
    Class<? extends RowDataHandler> handler() default DefaultRowDataHandler.class;
}
