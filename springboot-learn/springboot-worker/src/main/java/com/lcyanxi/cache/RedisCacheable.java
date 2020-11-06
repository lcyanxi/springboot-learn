package com.lcyanxi.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheable {
    String keyPrefix() default "_springboot:";

    String key();

    String typeDeclareMethod() default "";

    int expire() default 60;

    int randomMax() default 1;

    boolean impervious() default false;

}
