package com.lcyanxi.fuxi.algorithm;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 手动实现一组生产者消费模型
 */
public class BoundedConditionQueue {
    private int capacity;
    private LinkedList<Object> queue;
    private ReentrantLock lock;
    Condition notFull;
    Condition fullCondition;

    public BoundedConditionQueue(int capacity){
        this.capacity = capacity;
        lock = new ReentrantLock();
        notFull = lock.newCondition();
        fullCondition = lock.newCondition();
        queue = new LinkedList<>();
    }

    public void producer(Integer data) throws InterruptedException {
        lock.lock();
        try{
            if (capacity == queue.size()){
                notFull.wait();
            }
            queue.add(data);
            fullCondition.signal();
        }finally {
            lock.unlock();
        }
    }

    public Integer consumer() throws InterruptedException {
        lock.lock();
        try{
            if (queue.isEmpty()){
                fullCondition.await();
            }
            Object poll = queue.poll();
            notFull.signal();
            return (Integer) poll;
        }finally {
            lock.unlock();
        }
    }

}
