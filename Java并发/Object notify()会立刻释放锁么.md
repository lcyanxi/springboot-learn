Synchronized的重要不言而喻, 而作为配合Synchronized使用的另外两个关键字也显得格外重要.

今天, 来聊聊配合Object基类的
1. **wait()**
2. **notify()**

这两个方法的实现,为多线程协作提供了保证。

##### wait() & notify()
Object 类中的 wait&notify 这两个方法，其实包括他们的重载方法一共有 5 个，而 Object 类中一共才 12 个方法，可见这 2 个方法的重要性。

我们先看看 JDK 中的定义：

```
public final native void notify();
```
**wait方法**
> wait是要释放对象锁，进入等待池。
既然是释放对象锁，那么肯定是先要获得锁。
所以wait必须要写在synchronized代码块中，否则会报异常。

**notify方法**
> 也需要写在synchronized代码块中,
调用对象的这两个方法也需要先获得该对象的锁.
notify,notifyAll, 唤醒等待该对象同步锁的线程,并放入该对象的锁池中.
对象的锁池中线程可以去竞争得到对象锁,然后开始执行.
如果是通过notify来唤起的线程,
那进入wait的线程会被随机唤醒;

> 如果是通过notifyAll唤起的线程,
默认情况是最后进入的会先被唤起来,即LIFO的策略;

比较重要的是:

**notify()或者notifyAll()调用时并不会真正立即释放对象锁, 必须等到synchronized方法或者语法块执行完才真正释放锁**.


##### 案例分析

```
public class ObjectNotifyDemo {
    final static Object lock = new Object();

    public static void main(String[] args) {
        new Thread(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + "等待 获得 锁");
            synchronized (lock) {
                try {
                    System.out.println(threadName + "获得 锁");
                    TimeUnit.SECONDS.sleep(5);
                    System.out.println(threadName + "开始 执行 wait() ");
                    lock.wait();
                    System.out.println(threadName + "结束 执行 wait()");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"Thread A").start();

        new Thread(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + "等待 获得 锁");
            synchronized (lock) {
                try {
                    System.out.println(threadName + "获得 锁");
                    TimeUnit.SECONDS.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.notify();
                System.out.println(threadName + "执行 notify()");
                while (true){
                    try {
                        System.out.println("============");
                        TimeUnit.SECONDS.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        },"Thread B").start();
    }
}
```
执行结果：

```
Thread A等待 获得 锁
Thread A获得 锁
Thread B等待 获得 锁
Thread A开始 执行 wait() 
Thread B获得 锁
Thread B执行 notify()
============
============
```

使用时切记：**必须由同一个lock对象调用wait、notify方法**
1. 当线程A执行wait方法时，该线程会被挂起；
2. 当线程B执行notify方法时，会唤醒一个被挂起的线程A；

**lock对象、线程A和线程B三者是一种什么关系**？
根据上面的案例，可以想象一个场景：
- lock对象维护了一个等待队列list；
- 线程A中执行lock的wait方法，把线程A保存到list中；
- 线程B中执行lock的notify方法，从等待队列中取出线程A继续执行；

**问题一: 为何wait&notify必须要加synchronized锁?**

- synchronized代码块通过javap生成的字节码中包含monitorenter 和 monitorexit 指令
- 执行monitorenter指令可以获取对象的monitor
- 而lock.wait()方法通过调用native方法wait(0)实现，表示线程执行 lock.wait() 方法时，必须持有该lock对象的monitor.

**notify执行之后立马唤醒线程吗?**

其实hotspot里真正的实现是: 退出同步块的时候才会去真正唤醒对应的线程; 不过这个也是个默认策略，也可以改的，在notify之后立马唤醒相关线程。

**问题四: notifyAll是怎么实现全唤起所有线程?**

或许大家立马就能想到一个for循环就搞定了，不过在JVM里没实现这么简单，而是借助了monitorexit.

上面提到了当某个线程从wait状态恢复出来的时候，要先获取锁，然后再退出同步块;

所以notifyAll的实现是调用notify的线程在退出其同步块的时候唤醒起最后一个进入wait状态的线程;

然后这个线程退出同步块的时候继续唤醒其倒数第二个进入wait状态的线程，依次类推.

同样这这是一个策略的问题，JVM里提供了挨个直接唤醒线程的参数，不过很少使用, 这里就不提了。

**问题五: wait的线程是否会影响性能?**

wait/nofity 是通过JVM里的 park/unpark 机制来实现的，在Linux下这种机制又是通过pthread_cond_wait/pthread_cond_signal 来实现的;

因此当线程进入到wait状态的时候其实是会放弃cpu的，也就是说这类线程是不会占用cpu资源。

**notify()是随机唤醒线程么?**

原来hotspot对notofy()的实现并不是我们以为的随机唤醒, 而是“先进先出”的顺序唤醒!
