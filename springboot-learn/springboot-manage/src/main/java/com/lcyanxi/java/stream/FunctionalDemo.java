package com.lcyanxi.java.stream;

/**
 * @author lichang
 * @date 2021/1/12
 */
public class FunctionalDemo {
    // 定义一个含有函数式接口的方法
    public static void doSomthing(MyFunctionInterface functionalInterface) {
        //调用自定义函数式接口的方法
        functionalInterface.merthod();
    }
    public static void main(String[] args) {
        //调用函数式接口的方法
        doSomthing(()->System.out.println("excuter lambda!"));
    }
}
