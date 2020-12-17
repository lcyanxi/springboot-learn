package com.lcyanxi.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 注解的生命周期
@Target(ElementType.FIELD) // 表示注解用在属性上
@Documented //该注解将被包含在javaDoc中
@Inherited  // 子类可以继承父类中的注解
public @interface MyAutowired {
}
