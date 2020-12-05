package com.lcyanxi.designPatterns.dynamicProxy.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理实现类，根据传入的实现类反射调用
 * @author lichang
 * @date 2020/12/5
 */
public class JdkProxyHandler implements InvocationHandler {

    final Object delegate;

    JdkProxyHandler(Object delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target) {
        return (T) Proxy.newProxyInstance(fetchClassLoader(target.getClass()), target.getClass().getInterfaces(),
                new JdkProxyHandler(target));
    }

    private static ClassLoader fetchClassLoader(Class<?> clazz) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = clazz.getClassLoader();
        }
        return loader == null ? JdkProxyHandler.class.getClassLoader() : loader;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] objects) throws Throwable {
        return method.invoke(delegate, objects);
    }
}
