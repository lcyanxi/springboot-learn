package com.lcyanxi.basics.juc;

import org.apache.dubbo.common.threadlocal.InternalThread;
import org.apache.dubbo.common.threadlocal.InternalThreadLocal;

/**
 * @author lichang
 * @date 2020/12/25
 */
public class InternalThreadLocalDemo {

    private static InternalThreadLocal<Integer> internalThreadLocal_0 = new InternalThreadLocal<>();

    public static void main(String[] args) {
        new InternalThread(() -> {
            for (int i = 0; i < 5; i++) {
                internalThreadLocal_0.set(i);
                Integer value = internalThreadLocal_0.get();
                System.out.println(Thread.currentThread().getName() + ":" + value);
            }
        }, "internalThread_have_set").start();

        new InternalThread(() -> {
            for (int i = 0; i < 5; i++) {
                Integer value = internalThreadLocal_0.get();
                System.out.println(Thread.currentThread().getName() + ":" + value);
            }
        }, "internalThread_no_set").start();
    }
}
