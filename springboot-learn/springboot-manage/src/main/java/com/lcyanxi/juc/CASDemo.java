package com.lcyanxi.juc;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lichang
 * @date 2020/12/20
 */
public class CASDemo {
    AtomicInteger data = new AtomicInteger(0);

    // 多个线程同时对data变量执行操作： data.incrementAndGet()

    public synchronized void increment(){
        data.incrementAndGet();
    }
}
