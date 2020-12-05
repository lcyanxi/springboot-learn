package com.lcyanxi.designPatterns.dynamicProxy.handler;

import com.lcyanxi.service.ICountService;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author lichang
 * @date 2020/12/5
 */
public class JdkHandler implements InvocationHandler {

    final Object delegate;

    JdkHandler(Object delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] objects) throws Throwable {
        return method.invoke(delegate, objects);
    }

    public static ICountService createJdkDynamicProxy(final ICountService delegate) {
        ICountService jdkProxy = (ICountService) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
                new Class[] { ICountService.class }, new JdkHandler(delegate));
        return jdkProxy;
    }
}
