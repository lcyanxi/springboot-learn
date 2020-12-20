假设多个线程需要对一个变量不停的累加1，比如说下面这段代码： 

```
public class CASDemo {
    private int data = 0 ;
    // 多个线程同时对data变量执行操作：data++
}
```

实际上，上面那段代码是不ok的，因为多个线程直接这样并发的对一个data变量进行修改，是线程不安全性的行为，会导致data值的变化不遵照预期的值来改变。

举个例子，比如说20个线程分别对data执行一次data++操作，我们以为最后data的值会变成20，其实不是。最后可能data的值是18，或者是19，都有可能，因为多线程并发操作下，就是会有这种安全问题，导致数据结果不准确。

至于为什么会不准确？那不在本文讨论的范围里，因为这个一般只要是学过java的同学，肯定都了解过多线程并发问题。
##### 初步的解决方案：synchronized

所以，对于上面的代码，一般我们会改造一下，让他通过加锁的方式变成线程安全的：

```
public class CASDemo {
    private int data = 0 ;
    // 多个线程同时对data变量执行操作：data++

    public synchronized void increment(){
        data ++;
    }
}
```

这个时候，代码就是线程安全的了，因为我们加了synchronized，也就是让每个线程要进入increment()方法之前先得尝试加锁，同一时间只有一个线程能加锁，其他线程需要等待锁。

通过这样处理，就可以保证换个data每次都会累加1，不会出现数据错乱的问题。

我们来看看下面的图，感受一下synchronized加锁下的效果和氛围，相当于N个线程一个一个的排队在更新那个数值。
![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLYc6ibPDUmTOuDglWIkSWiavEDmNboJP0ic19WjSzwia123DPYekPuTZtCxUF4MrO1SSibzvCQm4Sgj6LA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

但是，如此简单的data++操作，都要加一个重磅的synchronized锁来解决多线程并发问题，就有点杀鸡用牛刀，大材小用了。

虽然随着Java版本更新，也对synchronized做了很多优化，但是处理这种简单的累加操作，仍然显得“太重了”。人家synchronized是可以解决更加复杂的并发编程场景和问题的。

而且，在这个场景下，你要是用synchronized，不就相当于让各个线程串行化了么？一个接一个的排队，加锁，处理数据，释放锁，下一个再进来。

##### 更高效的方案：Atomic原子类及其底层原理

对于这种简单的data++类的操作，其实我们完全可以换一种做法，java并发包下面提供了一系列的Atomic原子类，比如说AtomicInteger。

他可以保证多线程并发安全的情况下，高性能的并发更新一个数值。我们来看下面的代码：

```
public class CASDemo {
    AtomicInteger data = new AtomicInteger(0);

    // 多个线程同时对data变量执行操作： data.incrementAndGet()
}
```

大家看上面的代码，是不是很简单！多个线程可以并发的执行AtomicInteger的incrementAndGet()方法，意思就是给我把data的值累加1，接着返回累加后最新的值。

这个代码里，就没有看到加锁和释放锁这一说了吧！

实际上，**Atomic原子类底层用的不是传统意义的锁机制，而是无锁化的CAS机制，通过CAS机制保证多线程修改一个数值的安全性**

**那什么是CAS呢**？他的全称是：Compare and Set，也就是先比较再设置的意思。

![image](http://assets.processon.com/chart_image/5f8b8f4a1e085307a0924849.png)

上述整个过程，就是所谓Atomic原子类的原理，没有基于加锁机制串行化，而是基于CAS机制：先获取一个值，然后发起CAS，比较这个值被人改过没？如果没有，就更改值！这个CAS是原子的，别人不会打断你！

通过这个机制，不需要加锁这么重量级的机制，也可以用轻量级的方式实现多个线程安全的并发的修改某个数值。

##### ABA问题解决方案
首先模拟一下ABA问题的产生

```
public class ABADemo {
    /**
     * 普通的原子引用包装类
     */
    static AtomicReference<Integer> atomicReference = new AtomicReference<>(100);

    public static void main(String[] args) {

        System.out.println("============以下是ABA问题的产生==========");
        new Thread(() -> {
            // 把100 改成 101 然后在改成100，也就是ABA
            atomicReference.compareAndSet(100, 101);
            atomicReference.compareAndSet(101, 100);
        }, "t1").start();

        new Thread(() -> {
            try {
                // 睡眠一秒，保证t1线程，完成了ABA操作
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 把100 改成 101 然后在改成100，也就是ABA
            System.out.println(atomicReference.compareAndSet(100, 2019) + "\t" + atomicReference.get());
        }, "t2").start();
    }
}
```
**结果：** cas成功,显然100的值在线程1中被修改过

```
============以下是ABA问题的产生==========
true	2019
```
解决方案：**AtomicStampedReference**传递两个值，一个是初始值，一个是初始版本号

```
public class ABADemo {
    // 传递两个值，一个是初始值，一个是初始版本号
    static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(100, 1);

    public static void main(String[] args) {
        System.out.println("============以下是ABA问题的解决==========");

        new Thread(() -> {
            String threadName = Thread.currentThread().getName();
            // 获取版本号
            int stamp = atomicStampedReference.getStamp();
            System.out.println(threadName + "\t 第一次版本号" + stamp);

            // 暂停t3一秒钟
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 传入4个值，期望值，更新值，期望版本号，更新版本号
            atomicStampedReference.compareAndSet(100, 101, atomicStampedReference.getStamp(), atomicStampedReference.getStamp()+1);
            System.out.println(threadName + "\t 第二次版本号" + atomicStampedReference.getStamp());
            atomicStampedReference.compareAndSet(101, 100, atomicStampedReference.getStamp(), atomicStampedReference.getStamp()+1);
            System.out.println(threadName + "\t 第三次版本号" + atomicStampedReference.getStamp());

        }, "t3").start();

        new Thread(() -> {
            String threadName = Thread.currentThread().getName();
            // 获取版本号
            int stamp = atomicStampedReference.getStamp();
            System.out.println(threadName + "\t 第一次版本号" + stamp);

            // 暂停t4 3秒钟，保证t3线程也进行一次ABA问题
            try {
                Object o = new Object();
                o.wait(100);
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean result = atomicStampedReference.compareAndSet(100, 2019, stamp, stamp+1);
            System.out.println(threadName + "\t 修改成功否：" + result + "\t 当前最新实际版本号：" + atomicStampedReference.getStamp());
            System.out.println(threadName + "\t 当前实际最新值" + atomicStampedReference.getReference());
        }, "t4").start();
    }
}
```
**运行结果：**

```
============以下是ABA问题的解决==========
t3	 第一次版本号1
t4	 第一次版本号1
t3	 第二次版本号2
t3	 第三次版本号3
t4	 修改成功否：false	 当前最新实际版本号：3
t4	 当前实际最新值100
```
##### Java 8对CAS机制的优化

> 但是这个CAS有没有问题呢？

肯定是有的。比如说大量的线程同时并发修改一个AtomicInteger，可能有很**多线程会不停的自旋，进入一个无限重复的循环中**。

这些线程不停地获取值，然后发起CAS操作，但是发现这个值被别人改过了，于是再次进入下一个循环，获取值，发起CAS操作又失败了，再次进入下一个循环。

**在大量线程高并发更新AtomicInteger的时候，这种问题可能会比较明显，导致大量线程空循环，自旋转，性能和效率都不是特别好**。

于是，Java 8推出了一个新的类，LongAdder，他就是尝试使用**分段CAS以及自动分段迁移的方式**来大幅度提升多线程高并发执行CAS操作的性能！

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLYc6ibPDUmTOuDglWIkSWiavECo39A2Gb2Z1wGZ2oMYQsO3piaqGSmJVzz1BvPKUJ9MJJOmDUcxjnTVQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

在LongAdder的底层实现中，首先有一个base值，刚开始多线程来不停的累加数值，都是对base进行累加的，比如刚开始累加成了base = 5。

接着如果发现并发更新的线程数量过多，就会开始施行分段CAS的机制，**也就是内部会搞一个Cell数组，每个数组是一个数值分段**。

这时，让大量的线程分别去对不同Cell内部的value值进行CAS累加操作，这样就把CAS计算压力分散到了不同的Cell分段数值中了！

这样就可以大幅度的降低多线程并发更新同一个数值时出现的无限循环的问题，大幅度提升了多线程并发更新数值的性能和效率！

而且他内部实现了**自动分段迁移的机制**，也就是如果某个Cell的value执行CAS失败了，那么就会自动去找另外一个Cell分段内的value值进行CAS操作。

这样也**解决了线程空旋转、自旋不停等待执行CAS操作的问题**，让一个线程过来执行CAS时可以尽快的完成这个操作。

最后，如果你要从LongAdder中获取当前累加的总值，就会把base值和所有Cell分段数值加起来返回给你。
