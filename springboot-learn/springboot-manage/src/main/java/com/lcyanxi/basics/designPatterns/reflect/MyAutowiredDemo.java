package com.lcyanxi.basics.designPatterns.reflect;

import com.lcyanxi.annotation.MyAutowired;
import com.lcyanxi.controller.IndexController;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lichang
 * @date 2020/12/16
 */
@Slf4j
public class MyAutowiredDemo {
    public static void main(String[] args) {
        IndexController controller = new IndexController();
        //1.获取相应类对象
        Class<? extends IndexController> clazz = controller.getClass();
        //2.通过反射获取所有的属性
        Stream.of(clazz.getDeclaredFields()).forEach(field -> {
            //3.获取标注了@Autowired注解的属性
            MyAutowired annotation = field.getAnnotation(MyAutowired.class);
            if (Objects.isNull(annotation)){
                return;
            }
            //4.修改属性的权限值
            field.setAccessible(true);
            //获取出相应的类型值
            Class<?> type = field.getType();

            try {
                //5.new出相应类型的对象
                //Object newInstance = type.newInstance(); // 无法获取私有构造器
                Constructor<?> constructor = type.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object instance = constructor.newInstance();
                System.out.println(instance);
                //6.把值进行设置
                field.set(controller,instance);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        log.info("myAutowiredDemo get userService:[{}]",controller.getCountServiceIntense());
    }
}
