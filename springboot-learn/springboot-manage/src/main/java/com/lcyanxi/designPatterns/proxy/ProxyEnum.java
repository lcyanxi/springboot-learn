package com.lcyanxi.designPatterns.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyObject;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import sun.reflect.misc.ReflectUtil;

/**
 * 代理技术枚举类
 * @author lcyaxni
 * @date 2020/12/4
 */
public enum ProxyEnum {

    JDK_PROXY(new ProxyFactory() {
        public <T> T newProxyInstance(Class<T> inferfaceClass, Object handler) {
            return inferfaceClass.cast(Proxy.newProxyInstance(
                    this.getClass().getClassLoader(),
                    new Class[] {inferfaceClass},
                    (InvocationHandler)handler));
        }
    }),

    BYTE_BUDDY_PROXY(new ProxyFactory() {
        public <T> T newProxyInstance(Class<T> inferfaceClass, Object handler) {
            Class<? extends T> cls = new ByteBuddy()
                    .subclass(inferfaceClass)
                    .method(ElementMatchers.isDeclaredBy(inferfaceClass))
                    .intercept(MethodDelegation.to(handler, "handler"))
                    .make()
                    .load(inferfaceClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();
            try {
                return (T) ReflectUtil.newInstance(cls);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }),


    CGLIB_PROXY(new ProxyFactory() {
        public <T> T newProxyInstance(Class<T> inferfaceClass, Object handler) {
            Enhancer enhancer = new Enhancer();
            enhancer.setCallback((MethodInterceptor)handler);
            enhancer.setInterfaces(new Class[] {inferfaceClass});
            return (T) enhancer.create();
        }
    }),


    JAVASSIST_DYNAMIC_PROXY(new ProxyFactory() {
        public <T> T newProxyInstance(Class<T> inferfaceClass, Object handler) {
            javassist.util.proxy.ProxyFactory proxyFactory = new javassist.util.proxy.ProxyFactory();
            proxyFactory.setInterfaces(new Class[] { inferfaceClass });
            Class<?> proxyClass = proxyFactory.createClass();
            T javassistProxy = null;
            try {
                javassistProxy = (T) ReflectUtil.newInstance(proxyClass);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            ((ProxyObject) javassistProxy).setHandler((MethodHandler)handler);
            return javassistProxy;
        }
    });

    private ProxyFactory factory;
    ProxyEnum(ProxyFactory factory){
        this.factory =factory;
    }

    public <T> T newProxyInstance(Class<T> interfaceType, Object handler) {
        return factory.newProxyInstance(interfaceType, handler);
    }
    interface ProxyFactory{
        <T> T newProxyInstance(Class<T> inferfaceClass, Object handler);
    }
}
