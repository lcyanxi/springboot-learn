**CountDownLatch原理**

CountDownLatch是通过一个计数器来实现的，当我们在new 一个CountDownLatch对象的时候需要带入该计数器值，该值就表示了线程的数量。每当一个线程完成自己的任务后，计数器的值就会减1。当计数器的值变为0时，就表示所有的线程均已经完成了任务，然后就可以恢复等待的线程继续执行了。


![image](https://note.youdao.com/yws/res/8246/WEBRESOURCE70a29d85d5dd82634d8ae4f5f081fd92)


**await()**

CountDownLatch提供await()方法来使当前线程在锁存器倒计数至零之前一直等待，除非线程被中断


```
public void await() throws InterruptedException {
    sync.acquireSharedInterruptibly(1);
}
```
await其内部使用AQS的acquireSharedInterruptibly(int arg)

```
public final void acquireSharedInterruptibly(int arg)
        throws InterruptedException {
    if (Thread.interrupted())
        throw new InterruptedException();
    if (tryAcquireShared(arg) < 0)
        doAcquireSharedInterruptibly(arg);
}
```
在内部类Sync中重写了tryAcquireShared(int arg)方法

```
protected int tryAcquireShared(int acquires) {
    return (getState() == 0) ? 1 : -1;
}
```

getState()获取同步状态，其值等于计数器的值，从这里我们可以看到如果计数器值不等于0，则会调用doAcquireSharedInterruptibly(int arg)，该方法为一个自旋方法会尝试一直去获取同步状态：

```
private void doAcquireSharedInterruptibly(int arg)
    throws InterruptedException {
    final Node node = addWaiter(Node.SHARED);
    boolean failed = true;
    try {
        for (;;) {
            final Node p = node.predecessor();
            if (p == head) {
            // 对于CountDownLatch而言，如果计数器值不等于0，那么r 会一直小于0
                int r = tryAcquireShared(arg);
                if (r >= 0) {
                    setHeadAndPropagate(node, r);
                    p.next = null; // help GC
                    failed = false;
                    return;
                }
            }
            // 等待
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                throw new InterruptedException();
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```



**countDown()**

CountDownLatch提供countDown() 方法递减锁存器的计数，如果计数到达零，则释放所有等待的线程。


```
    public void countDown() {
        sync.releaseShared(1);
    }

```

内部调用AQS的releaseShared(int arg)方法来释放共享锁同步状态：


```
    public final boolean releaseShared(int arg) {
        if (tryReleaseShared(arg)) {
            doReleaseShared();
            return true;
        }
        return false;
    }
```

tryReleaseShared(int arg)方法被CountDownLatch的内部类Sync重写：

```
protected boolean tryReleaseShared(int releases) {
    // Decrement count; signal when transition to zero
    for (;;) {
        int c = getState();
        if (c == 0)
            return false;
        int nextc = c-1;
        if (compareAndSetState(c, nextc))
            return nextc == 0;
    }
}
```



**总结**

CountDownLatch内部通过**共享锁实现**。在创建CountDownLatch实例时，需要传递一个int型的参数：count，该参数为计数器的初始值，也可以理解为该共享锁可以获取的总次数。当某个线程调用await()方法，程序首先判断count的值是否为0，如果不会0的话则会一直等待直到为0为止。当其他线程调用countDown()方法时，则执行释放共享锁状态，使count值 – 1。当在创建CountDownLatch时初始化的count参数，必须要有count线程调用countDown方法才会使计数器count等于0，锁才会释放，前面等待的线程才会继续运行。注意CountDownLatch不能回滚重置。