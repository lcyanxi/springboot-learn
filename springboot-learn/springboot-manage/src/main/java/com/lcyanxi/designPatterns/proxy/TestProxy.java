package com.lcyanxi.designPatterns.proxy;

import com.lcyanxi.service.TestProxyService;

/**
 * @author lichang
 * @date 2020/12/5
 */
public class TestProxy {

    public static void main(String[] args) {
        //Create JDK Proxy
        InvokeHandler handler = new InvokeHandler();
        long time = System.currentTimeMillis();
        TestProxyService test1 =ProxyEnum.JDK_PROXY.newProxyInstance(TestProxyService.class,handler);
        time = System.currentTimeMillis() - time;
        System.out.println("Create JDK Proxy: " + time + " ms");

        //Create byteBuddy Proxy
        time = System.currentTimeMillis();
        TestProxyService test2 =ProxyEnum.BYTE_BUDDY_PROXY.newProxyInstance(TestProxyService.class,handler);
        time = System.currentTimeMillis() - time;
        System.out.println("Create byteBuddy Proxy: " + time + " ms");

        // Create CGLIB Proxy
        time = System.currentTimeMillis();
        TestProxyService test3 =ProxyEnum.CGLIB_PROXY.newProxyInstance(TestProxyService.class,handler);
        time = System.currentTimeMillis() - time;
        System.out.println("Create CGLIB Proxy: " + time + " ms");

        // Create JAVASSIST Proxy
        time = System.currentTimeMillis();
        TestProxyService test5 =ProxyEnum.JAVASSIST_DYNAMIC_PROXY.newProxyInstance(TestProxyService.class,handler);
        time = System.currentTimeMillis() - time;
        System.out.println("Create JAVASSIST Proxy: " + time + " ms");

        String s ="proxy";
        System.out.println("----------------");
        for (int i = 0; i <10; i++) {
            test(test1, "Run JDK Proxy: ",s);
            test(test2, "Run byteBuddy Proxy: ",s);
            test(test3, "Run CGLIB Proxy: ",s);
            test(test5, "Run JAVASSIST Proxy: ",s);
            System.out.println("----------------");
        }

    }
    private static void test(TestProxyService service, String label,String s) {
        service.testProxy(s); // warm up
        int count = 100000000;
        long time = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            service.testProxy(s);
        }
        time = System.currentTimeMillis() - time;
        System.out.println(label + time + " ms, ");
    }
}
