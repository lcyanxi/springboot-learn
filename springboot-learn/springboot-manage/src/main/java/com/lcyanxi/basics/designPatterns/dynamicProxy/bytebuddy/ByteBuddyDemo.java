package com.lcyanxi.basics.designPatterns.dynamicProxy.bytebuddy;

/**
 * @author lichang
 * @date 2021/2/26
 */
public class ByteBuddyDemo {
    private void fun1() throws Exception {
        System.out.println("this is fun 1.");
        Thread.sleep(500);
    }

    private void fun2() throws Exception {
        System.out.println("this is fun 2.");
        Thread.sleep(500);
    }

    public static void main(String[] args) throws Exception {
        ByteBuddyDemo test = new ByteBuddyDemo();
        test.fun1();
        test.fun2();
    }
}
