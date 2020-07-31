package com.lcyanxi.util;

/**
 * @author lichang
 * @date 2020/6/10
 * ThreadLocal与线程同步机制不同，线程同步机制是多个线程共享同一个变量，
 * 而ThreadLocal是为每一个线程创建一个单独的变量副本，故而每个线程都可以独立地改变自己所拥有的变量副本，而不会影响其他线程所对应的副本。
 */
public class ThreadLocalDemo {

    private static ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 0);

    public Integer getAndIncrement(){
        threadLocal.set(threadLocal.get() + 1);
        return threadLocal.get();
    }

    public static void main(String[] args) {
        ThreadLocalDemo demo = new ThreadLocalDemo();
        for (int i = 0; i < 3; i++){
            ThreadDemo demo1 = new ThreadDemo(demo);
            demo1.start();
        }
    }

    private static class ThreadDemo extends Thread{
        private ThreadLocalDemo demo;
        private ThreadDemo (ThreadLocalDemo demo){
            this.demo = demo;
        }

        @Override
        public void run() {
            for (int i = 0; i < 3; i++){
                System.out.println(Thread.currentThread().getName() + ":" + demo.getAndIncrement());
            }
        }
    }
}
