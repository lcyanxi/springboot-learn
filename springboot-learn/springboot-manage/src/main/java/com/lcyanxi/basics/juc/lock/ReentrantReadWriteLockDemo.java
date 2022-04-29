package com.lcyanxi.basics.juc.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author : lichang
 * @desc : ReentrantReadWriteLock 读写锁
 *       适合读多写少场景
 * @since : 2022/04/26/6:47 下午
 */
public class ReentrantReadWriteLockDemo {
    public static void main(String[] args) {
        ReentrantReadWriteLock rrwLock = new ReentrantReadWriteLock();
        ReadThread rt1 = new ReadThread("rt1", rrwLock);
        ReadThread rt2 = new ReadThread("rt2", rrwLock);
        WriteThread wt1 = new WriteThread("wt1", rrwLock);
        ReadThread rt3 = new ReadThread("rt3", rrwLock);
        ReadThread rt4 = new ReadThread("rt4", rrwLock);
        rt1.start();
        rt2.start();
        wt1.start();
        rt3.start();
        rt4.start();
    }
}


class ReadThread extends Thread {
    private final ReentrantReadWriteLock rrwLock;

    public ReadThread(String name, ReentrantReadWriteLock rrwLock) {
        super(name);
        this.rrwLock = rrwLock;
    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + " trying to lock");
        try {
            rrwLock.readLock().lock();
            System.out.println(Thread.currentThread().getName() + " lock successfully");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rrwLock.readLock().unlock();
            System.out.println(Thread.currentThread().getName() + " unlock successfully");
        }
    }
}


class WriteThread extends Thread {
    private final ReentrantReadWriteLock rrwLock;

    public WriteThread(String name, ReentrantReadWriteLock rrwLock) {
        super(name);
        this.rrwLock = rrwLock;
    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + " trying to lock");
        try {
            rrwLock.writeLock().lock();
            Thread.sleep(3000);
            System.out.println(Thread.currentThread().getName() + " lock successfully");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rrwLock.writeLock().unlock();
            System.out.println(Thread.currentThread().getName() + " unlock successfully");
        }
    }
}
