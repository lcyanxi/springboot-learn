CyclicBarrier，一个同步辅助类，在API中是这么介绍的：

**它允许一组线程互相等待，直到到达某个公共屏障点** (common barrier point)。在涉及一组固定大小的线程的程序中，这些线程必须不时地互相等待，此时 CyclicBarrier 很有用。因为该 barrier 在释放等待线程后可以重用，所以称它为循环 的 barrier。

通俗点讲就是：让一组线程到达一个屏障时被阻塞，直到最后一个线程到达屏障时，屏障才会开门，所有被屏障拦截的线程才会继续干活。

##### 案例

```
/**
 * 和CountDownLatch相反，等7人到齐开会。也就是做加法，开始是0，加到某个值的时候就执行
 * @author lichang
 * @date 2020/11/11
 */
public class CyclicBarrierDemo {
    public static void main(String[] args) {
        /**
         * 定义一个循环屏障，参数1：需要累加的值，参数2 需要执行的方法
         */
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7, () -> System.out.println("7人到齐了，开会吧"));
        for (int i = 0; i < 7; i++) {
            new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "到了");
                try {
                    // 先到的被阻塞，等全部线程完成后，才能执行方法
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, "Thread-" + i).start();
        }
    }
}
```
结果

```
Thread-0到了
Thread-2到了
Thread-1到了
Thread-4到了
Thread-3到了
Thread-5到了
Thread-6到了
7人到齐了，开会吧
```

**实现分析 CyclicBarrier的结构如下**：
![image](https://mmbiz.qpic.cn/mmbiz_png/R3InYSAIZkEz5s7HLOl4f6MXWMib4icP0gEwwI5g6gqBffcKKHEYfZvnq3brib21HlaNgvfEicVUIVfRK3vqxWFKcQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

通过上图我们可以看到CyclicBarrier的内部是使用重入锁ReentrantLock和Condition。它有两个构造函数：
- CyclicBarrier(int parties)：创建一个新的 CyclicBarrier，它将在给定数量的参与者（线程）处于等待状态时启动，但它不会在启动 barrier 时执行预定义的操作。
- CyclicBarrier(int parties, Runnable barrierAction) ：创建一个新的 CyclicBarrier，它将在给定数量的参与者（线程）处于等待状态时启动，并在启动 barrier 时执行给定的屏障操作，该操作由最后一个进入 barrier 的线程执行。



```
/**
 * @param parties 要拦截线程的数量
 * @param barrierAction 用于在线程到达屏障时，优先执行barrierAction ，用于处理更加复杂的业务场景。
 */
public CyclicBarrier(int parties, Runnable barrierAction) {
    if (parties <= 0) throw new IllegalArgumentException();
    this.parties = parties;
    this.count = parties;
    this.barrierCommand = barrierAction;
}

/**
 * @param parties 要拦截线程的数量
 */
public CyclicBarrier(int parties) {
    this(parties, null);
}

```
在CyclicBarrier中最重要的方法莫过于await()方法，在所有参与者都已经在此 barrier 上调用 await 方法之前，将一直等待。如下：

```
/**
 * 在CyclicBarrier中最重要的方法莫过于await()方法，
 * 在所有参与者都已经在此 barrier 上调用 await 方法之前，将一直等待。
 */
public int await() throws InterruptedException, BrokenBarrierException {
    try {
        return dowait(false, 0L); // 不超时等待
    } catch (TimeoutException toe) {
        throw new Error(toe); // cannot happen
    }
}
```
await()方法内部调用dowait(boolean timed, long nanos)方法：

```
private int dowait(boolean timed, long nanos)
    throws InterruptedException, BrokenBarrierException,
           TimeoutException {
    final ReentrantLock lock = this.lock; //获取锁
    lock.lock();
    try {
        final Generation g = generation; //分代
        //当某个线程试图等待处于断开状态的 barrier 时，或者 barrier 进入断开状态而线程处于等待状态时，抛出该异常
        if (g.broken)
            throw new BrokenBarrierException();
        //如果线程中断，终止CyclicBarrier
        if (Thread.interrupted()) {
            breakBarrier();
            throw new InterruptedException();
        }
        //进来一个线程 count - 1
        int index = --count;
        //count == 0 表示所有线程均已到位，触发Runnable任务
        if (index == 0) {  // tripped
            boolean ranAction = false;
            try {
                final Runnable command = barrierCommand;
                //触发任务
                if (command != null)
                    command.run();
                //唤醒所有等待线程，并更新generation
                ranAction = true;
                nextGeneration();
                return 0;
            } finally {
                if (!ranAction)
                    breakBarrier();
            }
        }

        for (;;) {
            try {
                //如果不是超时等待，则调用Condition.await()方法等待
                if (!timed)
                    trip.await();
                //超时等待，调用Condition.awaitNanos()方法等待
                else if (nanos > 0L)
                    nanos = trip.awaitNanos(nanos);
            } catch ()
            }

            if (g.broken)
                throw new BrokenBarrierException();
            //generation已经更新，返回index
            if (g != generation)
                return index;
            //“超时等待”，并且时间已到,终止CyclicBarrier，并抛出异常
            if (timed && nanos <= 0L) {
                breakBarrier();
                throw new TimeoutException();
            }
        }
    } finally {
        //释放锁
        lock.unlock();
    }
}
```
其实await()的处理逻辑还是比较简单的：如果该线程不是到达的最后一个线程，则他会一直处于等待状态，除非发生以下情况：
1. 最后一个线程到达，即index == 0
2. 超出了指定时间（超时等待）
3. 其他的某个线程中断当前线程
4. 其他的某个线程中断另一个等待的线程
5. 其他的某个线程在等待barrier超时
6. 其他的某个线程在此barrier调用reset()方法。reset()方法用于将屏障重置为初始状态。

在上面的源代码中，我们可能需要注意Generation 对象，在上述代码中我们总是可以看到抛出BrokenBarrierException异常。

**那么什么时候抛出异常呢？**

如果一个线程处于等待状态时，如果其他线程调用reset()，或者调用的barrier原本就是被损坏的，则抛出BrokenBarrierException异常。同时，任何线程在等待时被中断了，则其他所有线程都将抛出BrokenBarrierException异常，并将barrier置于损坏状态。

同时，Generation描述着CyclicBarrier的更显换代。在CyclicBarrier中，同一批线程属于同一代。当有parties个线程到达barrier，generation就会被更新换代。其中broken标识该当前CyclicBarrier是否已经处于中断状态。

```
private static class Generation {
    boolean broken = false;
}
```
默认barrier是没有损坏的。

当barrier损坏了或者有一个线程中断了，则通过breakBarrier()来终止所有的线程：

```
private void breakBarrier() {
    generation.broken = true;
    count = parties;
    trip.signalAll();
}
```
在breakBarrier()中除了将broken设置为true，还会调用signalAll将在CyclicBarrier处于等待状态的线程全部唤醒。

当所有线程都已经到达barrier处（index == 0），则会通过nextGeneration()进行更新换地操作，在这个步骤中，做了三件事：唤醒所有线程，重置count，generation。

```
private void nextGeneration() {
    trip.signalAll();
    count = parties;
    generation = new Generation();
}
```

CyclicBarrier同时也提供了await(long timeout, TimeUnit unit) 方法来做超时控制，内部还是通过调用doawait()实现的。

应用场景 CyclicBarrier试用与多线程结果合并的操作，用于多线程计算数据，最后合并计算结果的应用场景。比如我们需要统计多个Excel中的数据，然后等到一个总结果。我们可以通过多线程处理每一个Excel，执行完成后得到相应的结果，最后通过barrierAction来计算这些线程的计算结果，得到所有Excel的总和。

