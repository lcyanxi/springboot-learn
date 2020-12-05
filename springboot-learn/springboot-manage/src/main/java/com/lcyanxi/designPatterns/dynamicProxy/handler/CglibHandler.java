package com.lcyanxi.designPatterns.dynamicProxy.handler;

import com.lcyanxi.designPatterns.dynamicProxy.DynamicProxyPerformanceTest;
import com.lcyanxi.service.ICountService;
import java.lang.reflect.Method;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * @author lichang
 * @date 2020/12/5
 */
public class CglibHandler implements MethodInterceptor {
    final Object delegate;

    CglibHandler(Object delegate) {
        this.delegate = delegate;
    }
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return methodProxy.invoke(delegate, objects);
    }

    public static ICountService createCglibDynamicProxy(final ICountService delegate) throws Exception {
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(new CglibHandler(delegate));
        enhancer.setInterfaces(new Class[] { ICountService.class });
        ICountService cglibProxy = (ICountService) enhancer.create();
        return cglibProxy;
    }
}
