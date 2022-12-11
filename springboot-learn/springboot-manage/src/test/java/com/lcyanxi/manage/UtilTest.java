package com.lcyanxi.manage;

import java.lang.reflect.Field;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.google.common.collect.Maps;
import com.lcyanxi.model.User;

import lombok.extern.slf4j.Slf4j;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2021/11/16/10:15 上午
 */
@Slf4j
public class UtilTest {
    @Test
    public void test() throws IllegalAccessException {

        User user1 = new User();
        Map<String, Object> map = Maps.newHashMap();
        map.put("userId", 1111);
        map.put("userName", "lichang");
        map.put("password", "3333");
        map.put("id", "111111");

        User user = new User();
        user.setId("11111");
        String kangkang = Optional.ofNullable(user.getUserName()).orElse("kangkang");
        System.out.println(kangkang);

        List<String> aa = Lists.newArrayList(null,null);
        System.out.println("aa"+ aa.size());



//        Set<String> strings = map.keySet();
        System.out.println(JSON.toJSONString(map));
//
//        map.forEach((k, v) -> {
//            setValueByPropName(k, user1, v, User.class);
//        });

//        System.out.println(user1);
    }


    public static Object getValue(Object object, String fieldName) {
        if (object == null || StringUtils.isBlank(fieldName)) {
            return null;
        }
        Field field = null;
        Class<?> clazz = object.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(object);
            } catch (Exception e) {
                log.error("ReflectUtils getValue is error, object:{}, fieldName:{}", object, fieldName, e);
            }
        }
        return null;
    }

    public static Object getValueByPropName(String filedName, Object o, Class clazz) {
        Field field = ReflectionUtils.findField(clazz, filedName);
        field.setAccessible(true);
        return ReflectionUtils.getField(field, o);
    }

    public static void setValueByPropNameaaa(String filedName, Object o, Object val, Class clazz) {
        Field field = ReflectionUtils.findField(clazz, filedName);
        field.setAccessible(true);
        ReflectionUtils.setField(field, o, val);
    }

    public static void setValueByPropName(String filedName, Object o, Float value, Class clazz) {
        if (Objects.isNull(value)) {
            return;
        }
        Field field = ReflectionUtils.findField(clazz, filedName);
        field.setAccessible(true);
        Class<?> type = field.getType();
        if (type.equals(int.class) || type.equals(Integer.class)) {
            ReflectionUtils.setField(field, o, value.intValue());
        }
        if (type.equals(double.class) || type.equals(Double.class)) {
            ReflectionUtils.setField(field, o, value.doubleValue());
        }
        if (type.equals(long.class) || type.equals(Long.class)) {
            ReflectionUtils.setField(field, o, value.longValue());
        }
        if (type.equals(float.class) || type.equals(Float.class)) {
            ReflectionUtils.setField(field, o, value);
        }
    }
}
