**ReentrantLock是什么？**
  
  作用：保证多线情况下同步执行。ReentrantLock是可重入锁，什么是可重入锁呢？可重入锁就是当前持有该锁的线程能够多次获取该锁，无需等待。这要从 ReentrantLock 的一个内部类 Sync 的父类说起，Sync 的父类是 AbstractQueuedSynchronizer（后面简称AQS）
  
  **什么是AQS？**
  
  AQS 是 JDK1.5 提供的一个基于 FIFO 等待队列实现的一个用于实现同步器的基础框架，这个基础框架的重要性可以这么说，JCU 包里面几乎所有的有关锁、多线程并发以及线程同步器等重要组件的实现都是基于 AQS 这个框架。AQS 的核心思想是基于 volatile int state 这样的一个属性同时配合 Unsafe 工具对其原子性的操作来实现对当前锁的状态进行修改。当 state 的值为 0 的时候，标识改 Lock 不被任何线程所占有
  
  **ReentrantLock 锁的架构**
  ![image](https://note.youdao.com/yws/res/8055/WEBRESOURCE7a538cf1187cee76dace78a68f2ebc96)
  
  上图除了 AQS 之外，我把 AQS 的父类 AbstractOwnableSynchronizer（后面简称AOS）也画了进来，可以稍微提一下，AOS 主要提供一个 exclusiveOwnerThread 属性，用于关联当前持有该所的线程。另外、Sync 的两个实现类分别是 NonfairSync 和 FairSync，由名字大概可以猜到，一个是用于实现公平锁、一个是用于实现非公平锁。那么 Sync 为什么要被设计成内部类呢？我们可以看看 AQS 主要提供了哪些 protect 的方法用于修改 state 的状态，我们发现 Sync 被设计成为安全的外部不可访问的内部类。ReentrantLock 中所有涉及对 AQS 的访问都要经过 Sync，其实，Sync 被设计成为内部类主要是为了安全性考虑，这也是作者在 AQS 的 comments 上强调的一点。
  
  **AQS 的等待队列**
  
  作为 AQS 的核心实现的一部分，举个例子来描述一下这个队列长什么样子，我们假设目前有三个线程 Thread1、Thread2、Thread3 同时去竞争锁，如果结果是 Thread1 获取了锁，Thread2 和 Thread3 进入了等待队列，那么他们的样子如下：
  ![image](http://p1-tt.byteimg.com/large/pgc-image/fe85201a7f2e4c32a3061ee1213ab31c?from=pc)
  AQS 的等待队列基于一个双向链表实现的，HEAD 节点不关联线程，后面两个节点分别关联 Thread2 和 Thread3，他们将会按照先后顺序被串联在这个队列上。这个时候如果后面再有线程进来的话将会被当做队列的 TAIL
  
**同步队列为什么是一个双向链表,单向链表不行吗？**

假如队列是单向的如：Head -> N1 -> N2 -> Tail。出队的时候获取N1很简单，Head.next就行了，入队就麻烦了，要遍历整个链表到N2，然后N2.next = N3;N3.next =Tail。入队的复杂度就是O(n),而且Tail也失去他的意义。相反双向链表出队和入队都是O(1)时间复杂度。说白了空间换时间。
  
-   入队列

我们来看看，当这三个线程同时去竞争锁的时候发生了什么？代码

```
    public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
```
解读：三个线程同时进来，他们会首先会通过 CAS 去修改 state 的状态，如果修改成功，那么竞争成功，因此这个时候三个线程只有一个 CAS 成功，其他两个线程失败，也就是 tryAcquire 返回 false。接下来，addWaiter 会把将当前线程关联的 EXCLUSIVE 类型的节点入队列：

```
    private Node addWaiter(Node mode) {
        Node node = new Node(Thread.currentThread(), mode);
        // Try the fast path of enq; backup to full enq on failure
        Node pred = tail;
        if (pred != null) {
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        enq(node);
        return node;
    }
```
解读：如果队尾节点不为 null，则说明队列中已经有线程在等待了，那么直接入队尾。对于我们举的例子，这边的逻辑应该是走 enq，也就是开始队尾是 null，其实这个时候整个队列都是 null 的。代码：

```
private Node enq(final Node node) {
    for (;;) {
        Node t = tail;
        if (t == null) { // Must initialize
            if (compareAndSetHead(new Node()))
                tail = head;
        } else {
            node.prev = t;
            if (compareAndSetTail(t, node)) {
                t.next = node;
                return t;
            }
        }
    }
}
```

解读：如果 Thread2 和 Thread3 同时进入了 enq，同时 t==null，则进行 CAS 操作对队列进行初始化，这个时候只有一个线程能够成功，然后他们继续进入循环，第二次都进入了 else 代码块，这个时候又要进行 CAS 操作，将自己放在队尾，因此这个时候又是只有一个线程成功，我们假设是 Thread2 成功，哈哈，Thread2 开心的返回了，Thread3 失落的再进行下一次的循环，最终入队列成功，返回自己。

- 并发问题

 基于上面两段代码，他们是如何实现不进行加锁，当有多个线程，或者说很多很多的线程同时执行的时候，怎么能保证最终他们都能够乖乖的入队列而不会出现并发问题的呢？这也是这部分代码的经典之处，多线程竞争，热点、单点在队列尾部，多个线程都通过【CAS+死循环】这个free-lock黄金搭档来对队列进行修改，每次能够保证只有一个成功，如果失败下次重试，如果是N个线程，那么每个线程最多 loop N 次，最终都能够成功。
 
-  挂起等待线程

上面只是 addWaiter 的实现部分，那么节点入队列之后会继续发生什么呢？那就要看看 acquireQueued 是怎么实现的了我们还是以上面的例子来看看，Thread2 和 Thread3 已经被放入队列了，进入 acquireQueued 之后：

```
final boolean acquireQueued(final Node node, int arg) {
    boolean failed = true;
    try {
        boolean interrupted = false;
        for (;;) {
            final Node p = node.predecessor();
            if (p == head && tryAcquire(arg)) {
                setHead(node);
                p.next = null; // help GC
                failed = false;
                return interrupted;
            }
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```


1. 对于 Thread2 来说，它的 prev 指向 HEAD，因此会首先再尝试获取锁一次，如果失败，则会将 HEAD 的 waitStatus 值为 SIGNAL，下次循环的时候再去尝试获取锁，如果还是失败，且这个时候 prev 节点的 waitStatus 已经是 SIGNAL，则这个时候线程会被通过 LockSupport 挂起。
2. 对于 Thread3 来说，它的 prev 指向 Thread2，因此直接看看 Thread2 对应的节点的 waitStatus 是否为 SIGNAL，如果不是则将它设置为 SIGNAL，再给自己一次去看看自己有没有资格获取锁，如果 Thread2 还是挡在前面，且它的 waitStatus 是 SIGNAL，则将自己挂起。

如果 Thread1 死死的握住锁不放，那么 Thread2 和 Thread3 现在的状态就是挂起状态啦，而且 HEAD，以及 Thread 的 waitStatus 都是 SIGNAL，尽管他们在整个过程中曾经数次去尝试获取锁，但是都失败了，失败了不能死循环呀，所以就被挂起了。当前状态如下

![image](http://p3-tt.byteimg.com/large/pgc-image/06d2d66e4e584a789a478ebfe360a3c8?from=pc)

**锁释放-等待线程唤起**

我们来看看当 Thread1 这个时候终于做完了事情，调用了 unlock 准备释放锁，这个时候发生了什么。代码：

```
public final boolean release(int arg) {
    if (tryRelease(arg)) {
        Node h = head;
        if (h != null && h.waitStatus != 0)
            unparkSuccessor(h);
        return true;
    }
    return false;
}
```

解读：首先，Thread1 会修改AQS的state状态，加入之前是 1，则变为 0，注意这个时候对于非公平锁来说是个很好的插入机会，举个例子，如果锁是公平锁，这个时候来了 Thread4，那么这个锁将会被 Thread4 抢去。。。

我们继续走常规路线来分析，当 Thread1 修改完状态了，判断队列是否为 null，以及队头的 waitStatus 是否为 0，如果 waitStatus 为 0，说明队列无等待线程，按照我们的例子来说，队头的 waitStatus 为 SIGNAL=-1，因此这个时候要通知队列的等待线程，可以来拿锁啦，这也是 unparkSuccessor 做的事情，unparkSuccessor 主要做三件事情：

1. 将队头的 waitStatus 设置为 0。
2. 通过从队列尾部向队列头部移动，找到最后一个 waitStatus<=0的那个节点，也就是离队头最近的没有被cancelled的那个节点，队头这个时候指向这个节点。
3. 将这个节点唤醒，其实这个时候 Thread1 已经出队列了。

唤醒线程代码：

```
private void unparkSuccessor(Node node) {
    int ws = node.waitStatus;
    if (ws < 0)
        compareAndSetWaitStatus(node, ws, 0);

    Node s = node.next;
    if (s == null || s.waitStatus > 0) {
        s = null;
        for (Node t = tail; t != null && t != node; t = t.prev)
            if (t.waitStatus <= 0)
                s = t;
    }
    if (s != null)
        LockSupport.unpark(s.thread);
}
```
**羊群效应**

当有多个线程去竞争同一个锁的时候，假设锁被某个线程占用，那么如果有成千上万个线程在等待锁，有一种做法是同时唤醒这成千上万个线程去去竞争锁，这个时候就发生了羊群效应，海量的竞争必然造成资源的剧增和浪费，因此终究只能有一个线程竞争成功，其他线程还是要老老实实的回去等待。AQS 的 FIFO 的等待队列给解决在锁竞争方面的羊群效应问题提供了一个思路：保持一个 FIFO 队列，队列每个节点只关心其前一个节点的状态，线程唤醒也只唤醒队头等待线程。