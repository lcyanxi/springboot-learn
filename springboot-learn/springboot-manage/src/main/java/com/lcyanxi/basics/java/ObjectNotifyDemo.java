package com.lcyanxi.basics.java;

import java.util.concurrent.TimeUnit;

/**
 * notify()会立刻释放锁么
 * @author lichang
 * @date 2020/11/14
 */
public class ObjectNotifyDemo {
    final static Object lock = new Object();

    public static void main(String[] args) {
        new Thread(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + "等待 获得 锁");
            synchronized (lock) {
                try {
                    System.out.println(threadName + "获得 锁");
                    TimeUnit.SECONDS.sleep(5);
                    System.out.println(threadName + "开始 执行 wait() ");
                    lock.wait();
                    System.out.println(threadName + "结束 执行 wait()");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"Thread A").start();

        new Thread(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + "等待 获得 锁");
            synchronized (lock) {
                try {
                    System.out.println(threadName + "获得 锁");
                    TimeUnit.SECONDS.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.notify();
                System.out.println(threadName + "执行 notify()");
                while (true){
                    try {
                        System.out.println("============");
                        TimeUnit.SECONDS.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        },"Thread B").start();
    }
}
