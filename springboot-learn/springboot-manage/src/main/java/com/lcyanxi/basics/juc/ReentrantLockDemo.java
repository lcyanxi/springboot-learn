package com.lcyanxi.basics.juc;

import com.lcyanxi.basics.juc.lock.SynchronizedToReentrantLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo {
    private static final Lock lock = new ReentrantLock();
    private static final SynchronizedToReentrantLock synchronizedToReentrantLock = new SynchronizedToReentrantLock();

    /**
     * 公平锁
     */
    private static final Lock lock1 = new ReentrantLock(true);

    public static void main(String[] args) {

        // fairSync();

        for (int i = 0; i < 10; i++) {
            new Thread(() -> test(), "线程" + i).start();
        }
        System.out.println("===================");
    }

    public static void test() {
        for (int i = 0; i < 2; i++) {
            try {
                // lock.lock();
                synchronizedToReentrantLock.lock();
                System.out.println(Thread.currentThread().getName() + "获取到锁了");
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(Thread.currentThread().getName() + "释放锁了");
                // lock.unlock();
                synchronizedToReentrantLock.unlock();
            }
        }
    }


    /**
     * 公平锁测试
     */
    public static void fairSync() {
        for (int i = 0; i < 10; i++) {
            new Thread(new ThreadDemo(i)).start();
        }
    }

    static class ThreadDemo implements Runnable {
        Integer id;

        public ThreadDemo(Integer id) {
            this.id = id;
        }

        @Override

        public void run() {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
