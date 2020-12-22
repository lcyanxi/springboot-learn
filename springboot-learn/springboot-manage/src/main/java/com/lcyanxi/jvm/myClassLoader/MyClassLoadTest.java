package com.lcyanxi.jvm.myClassLoader;

/**
 * @author lichang
 * @date 2020/12/22
 */
public class MyClassLoadTest {
    public static void main(String[] args) {
        new Thread(new MsgHandle()).start();
    }
}
