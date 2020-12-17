package com.lcyanxi.designPatterns.reflect;

import com.lcyanxi.model.User;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author lichang
 * @date 2020/12/17
 */
public class ReflectDemo {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<User> clazz = User.class;
        try{
            User instance = clazz.newInstance();
            System.out.println(instance);
        }catch (Exception e){
            System.out.println(e);
        }
        Constructor<User> constructor = clazz.getDeclaredConstructor(new Class[]{String.class,String.class});
        constructor.setAccessible(true);
        Object instance1 = constructor.newInstance(new Object[]{"1","1222"});

        System.out.println(instance1);
    }
}
