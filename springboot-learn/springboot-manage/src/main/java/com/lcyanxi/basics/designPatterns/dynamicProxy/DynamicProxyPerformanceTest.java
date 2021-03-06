//package com.lcyanxi.basics.designPatterns.dynamicProxy;
//
//import static com.lcyanxi.basics.designPatterns.dynamicProxy.handler.AsmBytecodeHandler.createAsmBytecodeDynamicProxy;
//import com.lcyanxi.basics.designPatterns.dynamicProxy.handler.CglibHandler;
//import com.lcyanxi.basics.designPatterns.dynamicProxy.handler.JavaAssistHandler;
//import com.lcyanxi.basics.designPatterns.dynamicProxy.handler.JdkProxyHandler;
//import com.lcyanxi.service.ICountService;
//import com.lcyanxi.serviceImpl.CountServiceImpl;
//import java.text.DecimalFormat;
//
///**
// * @author lichang
// * @date 2020/12/5
// */
//public class DynamicProxyPerformanceTest {
//
//    public static void main(String[] args) throws Exception {
//        ICountService delegate = new CountServiceImpl();
//
//        long time = System.currentTimeMillis();
//        ICountService jdkProxy = JdkProxyHandler.createProxy(delegate);
//        time = System.currentTimeMillis() - time;
//        System.out.println("Create JDK Proxy: " + time + " ms");
//
//        time = System.currentTimeMillis();
//        ICountService cglibProxy = CglibHandler.createCglibDynamicProxy(delegate);
//        time = System.currentTimeMillis() - time;
//        System.out.println("Create CGLIB Proxy: " + time + " ms");
//
//        time = System.currentTimeMillis();
//        ICountService javassistProxy = JavaAssistHandler.createJavassistDynamicProxy(delegate);
//        time = System.currentTimeMillis() - time;
//        System.out.println("Create Javassist Proxy: " + time + " ms");
//
//        time = System.currentTimeMillis();
//        ICountService javassistBytecodeProxy = JavaAssistHandler.createJavassistBytecodeDynamicProxy(delegate);
//        time = System.currentTimeMillis() - time;
//        System.out.println("Create Javassist Bytecode Proxy: " + time + " ms");
//
//        time = System.currentTimeMillis();
//        ICountService asmBytecodeProxy = createAsmBytecodeDynamicProxy(delegate,ICountService.class);
//        time = System.currentTimeMillis() - time;
//        System.out.println("Create ASM Proxy: " + time + " ms");
//        System.out.println("================");
//
//        for (int i = 0; i < 3; i++) {
//            test(jdkProxy, "Run JDK Proxy: ");
//            test(cglibProxy, "Run CGLIB Proxy: ");
//            test(javassistProxy, "Run Javassist Proxy: ");
//            test(javassistBytecodeProxy, "Run Javassist Bytecode Proxy: ");
//            test(asmBytecodeProxy, "Run ASM Bytecode Proxy: ");
//            System.out.println("----------------");
//        }
//    }
//
//    private static void test(ICountService service, String label) throws Exception {
//        service.count(); // warm up
//        int count = 10000000;
//        long time = System.currentTimeMillis();
//        for (int i = 0; i < count; i++) {
//            service.count();
//        }
//        time = System.currentTimeMillis() - time;
//        System.out.println(label + time + " ms, " + new DecimalFormat().format(count * 1000 / time) + " t/s");
//    }
//
//}
