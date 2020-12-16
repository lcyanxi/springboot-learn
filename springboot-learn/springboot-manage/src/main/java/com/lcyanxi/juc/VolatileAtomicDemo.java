package com.lcyanxi.juc;

/**
 * @author lichang 模拟volatile无法保证原子性
 * @date 2020/9/19
 */
public class VolatileAtomicDemo {
    private static volatile int count = 0;
    public static void main(String[] args) {
        for (int i= 0 ;i < 10; i++){
            Thread thread = new Thread(()->{
                for (int j = 0; j < 1000; j++){
                    count = count +1;
                }
            });
            thread.start();
        }
        System.out.println(count);
    }
}
