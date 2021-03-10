package com.lcyanxi.jvm.gcMonitor;

/**
 * 演示线程：TIMED_WAITING 通过jstack查看
 * @author lichang
 * @date 2021/2/7
 */
public class JstackTreadSleepDemo {

    public static void main(String[] args) {
        System.out.println("hello - 1");
        try {
            Thread.sleep(1000 * 60 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("hello - 2");
    }
}
