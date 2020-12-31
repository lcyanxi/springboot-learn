![image](http://assets.processon.com/chart_image/5f8affb01e085307a09217af.png)

### 简介
在计算机行业有一个定律叫"摩尔定律"，在此定律下，计算机的性能突飞猛进，而且价格也随之越来越便宜， CPU 从单核到了多核，缓存性能也得到了很大提升，尤其是多核 CPU 技术的到来，计算机同一时刻可以处理多个任务。在硬件层面的发展带来的效率极大提升中，软件层面的多线程编程已经成为必然趋势，然而多线程编程就会引入数据安全性问题，有矛必有盾，于是发明了“锁”来解决线程安全问题。在这篇文章中，总结了 Java 中几把经典的 JVM 级别的锁。
###  synchronized
synchronized 关键字是一把经典的锁，也是我们平时用得最多的。在 JDK1.6 之前， syncronized 是一把重量级的锁，不过随着 JDK 的升级，也在对它进行不断的优化，如今它变得不那么重了，甚至在某些场景下，它的性能反而优于轻量级锁。在加了 syncronized 关键字的方法、代码块中，一次只允许一个线程进入特定代码段，从而避免多线程同时修改同一数据。

synchronized 锁有如下几个特点：

##### 有锁升级过程
在 JDK1.5 (含)之前， synchronized 的底层实现是重量级的，所以之前一致称呼它为"重量级锁"，在 JDK1.5 之后，对 synchronized 进行了各种优化，它变得不那么重了，**实现原理就是锁升级的过程**。我们先聊聊 1.5 之后的 synchronized 实现原理是怎样的。说到 synchronized 加锁原理，就不得不先说 Java 对象在内存中的布局， Java 对象内存布局如下:
![image](https://mmbiz.qpic.cn/sz_mmbiz_png/qdzZBE73hWspLa0PfqGuQh4IxWhMzWVFsI1g34ZtdbkCYpoqGcvSXgWWE7SPVjK42FzdfhiaP4LjgqENbibgdOuA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

如上图所示，在创建一个对象后，在 JVM 虚拟机( HotSpot )中，对象在 Java 内存中的存储布局 可分为三块:

##### 对象头区域此处存储的信息包括两部分：
- **对象自身的运行时数据( MarkWord )**：存储 hashCode、GC 分代年龄、锁类型标记、偏向锁线程 ID 、 CAS 锁指向线程 LockRecord 的指针等， synconized 锁的机制与这个部分( markwork )密切相关，用 markword 中最低的三位代表锁的状态，其中一位是偏向锁位，另外两位是普通锁位
- **对象类型指针( Class Pointer )**：对象指向它的类元数据的指针、 JVM 就是通过它来确定是哪个 Class 的实例。

##### 实例数据区域 
 此处存储的是对象真正有效的信息，比如对象中所有字段的内容
 
#####  对齐填充区域
 JVM 的实现HostSpot规定**对象的起始地址必须是 8 字节的整数倍**，换句话来说，现在 64 位的 OS 往外读取数据的时候一次性读取 64bit 整数倍的数据，也就是 8 个字节，所以 HotSpot **为了高效读取对象**，就做了"对齐"，如果一个对象实际占的内存大小不是 8byte 的整数倍时，就"补位"到 8byte 的整数倍。所以对齐填充区域的大小不是固定的。
 
 当线程进入到 synchronized 处尝试获取该锁时， synchronized 锁升级流程如下
 ![image](https://mmbiz.qpic.cn/sz_mmbiz_png/qdzZBE73hWspLa0PfqGuQh4IxWhMzWVFjibqQyGIcXPuUfL4uGN3KxoAk45FpgcflVLg7yRUP5luNjDzQAaD1cg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)
 
 如上图所示， synchronized 锁升级的顺序为：**偏向锁->轻量级锁->重量级**锁，每一步触发锁升级的情况如下：
-  **偏向锁**

在 JDK1.8 中，其实默认是轻量级锁，但如果设定了 -XX:BiasedLockingStartupDelay = 0 ，那在对一个 Object 做 syncronized 的时候，会立即上一把偏向锁。当处于偏向锁状态时， **markwork 会记录当前线程 ID**

- **升级到轻量级锁**
当下一个线程参与到偏向锁竞争时，会先判断 markword 中保存的线程 ID 是否与这个线程 ID 相等，如果不相等，会立即撤销偏向锁，升级为轻量级锁。每个线程在自己的线程栈中生成一个 LockRecord ( LR )，然后每个线程通过 CAS (自旋)的操作将锁对象头中的 markwork 设置为指向自己的 LR 的指针，哪个线程设置成功，就意味着获得锁。关于 synchronized 中此时执行的 CAS 操作是通过 native 的调用 HotSpot 中 bytecodeInterpreter.cpp 文件 C++ 代码实现的，有兴趣的可以继续深挖。

- **升级到重量级锁**

如果锁竞争加剧(如线程自旋次数或者自旋的线程数超过某阈值， JDK1.6 之后，由 JVM 自己控制该规则)，就会升级为重量级锁。此时就会向操作系统申请资源，**线程挂起，进入到操作系统内核态的等待队列中**，等待操作系统调度，然后映射回用户态。在重量级锁中，由于需要做内核态到用户态的转换，而这个过程中需要消耗较多时间，也就是"重"的原因之一。

##### 可重入
synchronized 拥有强制原子性的内部锁机制，是一把可重入锁。因此，在一个线程使用 synchronized 方法时调用该对象另一个 synchronized 方法，即一个线程得到一个对象锁后再次请求该对象锁，是永远可以拿到锁的。在 Java 中线程获得对象锁的操作是以线程为单位的，而不是以调用为单位的。 synchronized **锁的对象头的 markwork 中会记录该锁的线程持有者和计数器**，当一个线程请求成功后， JVM 会记下持有锁的线程，并将计数器计为1。此时其他线程请求该锁，则必须等待。而该持有锁的线程如果再次请求这个锁，就可以再次拿到这个锁，同时计数器会递增。当线程退出一个  synchronized 方法/块时，计数器会递减，如果计数器为 0 则释放该锁锁。

##### 悲观锁(互斥锁、排他锁)

 synchronized是一把悲观锁(独占锁)，当前线程如果获取到锁，会导致其它所有需要锁该的线程等待，一直等待持有锁的线程释放锁才继续进行锁的争抢。
 
### synchronized 与 ReentrantLock对比

##### ReentrantLock
ReentrantLock 从字面可以看出是一把可重入锁，这点和 synchronized 一样，但实现原理也与 syncronized 有很大差别，它是基于经典的 AQS 实现的, **AQS 是基于 volitale 和 CAS 实现的**，其中 AQS 中维护一个 valitale 类型的变量 state 来做一个可重入锁的重入次数，加锁和释放锁也是围绕这个变量来进行的。 ReentrantLock 也提供了一些 synchronized 没有的特点，因此比 synchronized 好用。

![image](https://mmbiz.qpic.cn/sz_mmbiz_png/qdzZBE73hWspLa0PfqGuQh4IxWhMzWVFYkogrf4T3bz5BQHv4dnSr596Dx7zbB4AC84Giawmqt9icTiatuicEyicYOg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

ReentrantLock 有如下特点：

1. **可重入**

 ReentrantLock 和 syncronized 关键字一样，都是可重入锁，不过两者实现原理稍有差别， **RetrantLock 利用 AQS 的的 state 状态来判断资源是否已锁**，同一线程重入加锁， state 的状态 +1 ; 同一线程重入解锁, state 状态 -1 (解锁必须为当前独占线程，否则异常); 当 state 为 0 时解锁成功。

2. **需要手动加锁、解锁**

synchronized关键字是自动进行加锁、解锁的，而 ReentrantLock 需要 lock() 和 unlock() 方法配合 try/finally语句块来完成，来手动加锁、解锁。

3. **支持设置锁的超时时间**

 synchronized关键字无法设置锁的超时时间，如果一个获得锁的线程内部发生死锁，那么其他线程就会一直进入阻塞状态，而 ReentrantLock 提供 tryLock方法，允许设置线程获取锁的超时时间，如果超时，则跳过，不进行任何操作，避免死锁的发生。

4. **支持公平/非公平锁**

synchronized关键字是一种非公平锁，先抢到锁的线程先执行。而 ReentrantLock 的构造方法中允许设置 true/false 来实现公平、非公平锁，如果设置为 true ，则线程获取锁要遵循"先来后到"的规则，每次都会构造一个线程 Node ，然后到双向链表的"尾巴"后面排队，等待前面的 Node 释放锁资源。

5. **可中断锁**

 ReentrantLock 中的 lockInterruptibly() 方法使得线程可以在被阻塞时响应中断，比如一个线程 t1 通过 lockInterruptibly() 方法获取到一个可重入锁，并执行一个长时间的任务，另一个线程通过 interrupt() 方法就可以立刻打断 t1 线程的执行，来获取t1持有的那个可重入锁。而通过 ReentrantLock 的 lock() 方法或者 Synchronized 持有锁的线程是不会响应其他线程的 interrupt() 方法的，直到该方法主动释放锁之后才会响应 interrupt() 方法。