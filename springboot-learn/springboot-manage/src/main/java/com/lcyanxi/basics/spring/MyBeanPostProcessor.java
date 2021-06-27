package com.lcyanxi.basics.spring;

import com.lcyanxi.service.IUserLessonService;

import java.lang.reflect.Proxy;

/**
 * @author lichang
 * @date 2020/7/13
 */
public class MyBeanPostProcessor implements  BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        System.out.println("bean对象初始化之前。。。。。");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object beanInstance, String beanName) throws Exception {
        // 为当前bean对象注册代理监控对象，负责增强bean对象方法能力
        Class beanClass = beanInstance.getClass();
        if (beanClass == IUserLessonService.class) {
            /*
             *
             * method:doSome args:doSome执行接受实参 proxy:代理监控对对象
             **/
            Object proxy = Proxy.newProxyInstance(beanInstance.getClass().getClassLoader(),
                    beanInstance.getClass().getInterfaces(), (proxy1, method, args) -> {
                        System.out.println("IUserLessonService doSome 被拦截");
                        String result = (String) method.invoke(beanInstance, args);// beanInstance.doSome
                        return result.toUpperCase();
                    });
            return proxy;
        }
        return beanInstance;
    }
}
