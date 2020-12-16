package com.lcyanxi.java;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * hashMap 循环依赖模拟
 * @author lichang
 * @date 2020/12/15
 */
public class HashMapDemo {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++){
            System.out.println("=========");
            HashMapThread thread = new HashMapThread();
            thread.start();
        }
    }
}

class HashMapThread extends Thread {
    private static AtomicInteger ai = new AtomicInteger();
    private static Map<Integer,Integer> map = new HashMap<>();

    @Override
    public void run() {
        while (ai.get() < 1000000000){
            System.out.println("********");
            map.put(ai.get(),ai.get());
            ai.incrementAndGet();
        }
    }
}