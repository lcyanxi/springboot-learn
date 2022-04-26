package com.lcyanxi.basics.juc.lock;

import lombok.extern.slf4j.Slf4j;

/**
 * @author : lichang
 * @desc : 用 synchronized 实现 ReentrantLock 功能
 * @since : 2022/04/22/5:02 下午
 */
@Slf4j
public class SynchronizedToReentrantLock {

    private static final long NONE = -1;
    private long owner = NONE;

    public synchronized void lock() {
        long currentThreadId = Thread.currentThread().getId();
        if (owner == currentThreadId) {
            throw new IllegalStateException("lock has been acquired by current thread");
        }
        while (this.owner != NONE) {
            log.info("thread {} is waiting lock", currentThreadId);
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.owner = currentThreadId;
        log.info("lock is acquired by thread {}", currentThreadId);

    }

    public synchronized void unlock() {
        long currentThreadId = Thread.currentThread().getId();
        if (this.owner != currentThreadId) {
            throw new IllegalStateException("Only lock owner can unlock the lock");
        }
        log.info("thread {} is unlocking", owner);
        owner = NONE;
        notify();
    }
}
