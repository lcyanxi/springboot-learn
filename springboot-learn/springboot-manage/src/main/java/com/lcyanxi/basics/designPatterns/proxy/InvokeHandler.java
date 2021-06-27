package com.lcyanxi.basics.designPatterns.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import javassist.util.proxy.MethodHandler;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 客户端代理类的处理
 * @author lichang
 * @date 2020/12/5
 */
@Slf4j
public class InvokeHandler implements InvocationHandler, MethodHandler, MethodInterceptor {

    private Object doInvoke(Object proxy, Method method, Object[] args) {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvokeHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }
        //TODO RPC调用
//        log.info("InvokeHandler doInvoke for rpc start..........");
        return null;
    }

    /**
     * jdk invoke
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return doInvoke(proxy, method, args);
    }

    /**
     * byteBuddy invoke
     */
    @RuntimeType
    public Object byteBuddyInvoke(@This Object proxy, @Origin Method method, @AllArguments @RuntimeType Object[] args) throws Throwable {
        return doInvoke(proxy, method, args);
    }

    /**
     * javassist invoke
     *
     */
    public Object invoke(Object proxy, Method method, Method proceed, Object[] args) throws Throwable {
        return doInvoke(proxy, method, args);
    }

    /**
     * cglib invoke
     */
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        return doInvoke(proxy, method, args);
    }
}
