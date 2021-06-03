package com.lcyanxi.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by lcyanxi on 2021/3/10
 */

public class ByteBuddyDemo {
    public static void main(String[] args) throws Exception {
        ArrayList list = new ArrayList(12);
        list.add(11);
        System.out.println(list.size());
        StringBuffer buffer  = new StringBuffer();
        buffer.append("dd");
        Hashtable hashtable = new Hashtable();
        hashtable.put("2",2);
//        hashtable.put(null,null);
        HashMap hashMap = new HashMap();
        hashMap.put(null,null);

        MyService service = enhanceByAnnotation();
        service.queryDatabase();
        service.provideHttpResponse();
        service.noLog();

    }

    public static class LoggerInterceptor {
        public static void log(@SuperCall Callable<Void> zuper) throws Exception {
            System.out.println("---before---");
            try {
                zuper.call();
            } finally {
                System.out.println("---after---");
            }
        }
    }

    private static MyService enhanceByAnnotation() throws Exception {
        return new ByteBuddy()
                .subclass(MyService.class)
                .method(new ByteBuddyDemo.FilterMethodWithLogAnnotation())
                .intercept(MethodDelegation.to(ByteBuddyDemo.LoggerInterceptor.class))
                .make()
                .load(ByteBuddyDemo.class.getClassLoader())
                .getLoaded()
                .getConstructor()
                .newInstance();
    }

    public static class FilterMethodWithLogAnnotation implements ElementMatcher<MethodDescription> {

        public boolean matches(MethodDescription target) {
        List<String> methodNameWithLog = Stream.of(MyService.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(MyAnnotation.class))
                .map(Method::getName)
                .collect(Collectors.toList());
        return methodNameWithLog.contains(target.getName());

        }
    }
}
