package com.lcyanxi.designPatterns.dynamicProxy.handler;

import com.lcyanxi.service.ICountService;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

/**
 * @author lichang
 * @date 2020/12/5
 */
public class JavaAssistHandler implements MethodHandler {

    final Object delegate;

    JavaAssistHandler(Object delegate) {
        this.delegate = delegate;
    }

    public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
        return m.invoke(delegate, args);
    }

    public static ICountService createJavassistDynamicProxy(final ICountService delegate) throws Exception {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(new Class[] { ICountService.class });
        Class<?> proxyClass = proxyFactory.createClass();
        ICountService javassistProxy = (ICountService) proxyClass.newInstance();
        ((ProxyObject) javassistProxy).setHandler(new JavaAssistHandler(delegate));
        return javassistProxy;
    }

    public static ICountService createJavassistBytecodeDynamicProxy(ICountService delegate) throws Exception {
        ClassPool mPool = new ClassPool(true);
        CtClass mCtc = mPool.makeClass(ICountService.class.getName() + "JavaassistProxy");
        mCtc.addInterface(mPool.get(ICountService.class.getName()));
        mCtc.addConstructor(CtNewConstructor.defaultConstructor(mCtc));
        mCtc.addField(CtField.make("public " + ICountService.class.getName() + " delegate;", mCtc));
        mCtc.addMethod(CtNewMethod.make("public int count() { return delegate.count(); }", mCtc));
        Class<?> pc = mCtc.toClass();
        ICountService bytecodeProxy = (ICountService) pc.newInstance();
        Field filed = bytecodeProxy.getClass().getField("delegate");
        filed.set(bytecodeProxy, delegate);
        return bytecodeProxy;
    }
}


