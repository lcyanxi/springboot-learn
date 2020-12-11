**ThreadLoacal是什么？**

ThreadLocal与线程同步机制不同，线程同步机制是多个线程共享同一个变量，而ThreadLocal是为每一个线程创建一个单独的变量副本，故而每个线程都可以独立地改变自己所拥有的变量副本，而不会影响其他线程所对应的副本

![image](https://note.youdao.com/yws/res/7995/WEBRESOURCEb32b57f75677f2e2a32d7d54bce2b7c1)

**ThreadLocal 实现原理**

每个Thread 维护一个 ThreadLocalMap 映射表，这个映射表的 key 是 ThreadLocal 实例本身，value 是真正需要存储的 Object。

也就是说 ThreadLocal 本身并不存储值，它只是作为一个 key 来让线程从 ThreadLocalMap 获取 value。值得注意的是图中的虚线，表示 ThreadLocalMap 是使用 ThreadLocal 的弱引用作为 Key 的，弱引用的对象在 GC 时会被回收。

**为什么需要数组呢？没有了链表怎么解决Hash冲突呢？**

用数组是因为，我们开发过程中可以一个线程可以有多个TreadLocal来存放不同类型的对象的，但是他们都将放到你当前线程的ThreadLocalMap里，所以肯定要数组来存。

hash冲突后采用**线性探测**（原来值的基础上往后加一个单位，直至不发生哈希冲突）的方法解决冲突

**ThreadLocal为什么会内存泄漏**

ThreadLocalMap使用ThreadLocal的弱引用作为key，如果一个ThreadLocal没有外部强引用来引用它，那么系统 GC 的时候，这个ThreadLocal势必会被回收，这样一来，ThreadLocalMap中就会出现key为null的Entry，就没有办法访问这些key为null的Entry的value，如果当前线程再迟迟不结束的话，这些key为null的Entry的value就会一直存在一条强引用链：Thread Ref -> Thread -> ThreaLocalMap -> Entry -> value永远无法回收，造成内存泄漏。

其实，ThreadLocalMap的设计中已经考虑到这种情况，也加上了一些防护措施：在ThreadLocal的get(),set(),remove()的时候都会清除线程ThreadLocalMap里所有key为null的value。

但是这些被动的预防措施并不能保证不会内存泄漏：

- 使用static的ThreadLocal，延长了ThreadLocal的生命周期，可能导致的内存泄漏（参考ThreadLocal 内存泄露的实例分析）。
- 分配使用了ThreadLocal又不再调用get(),set(),remove()方法，那么就会导致内存泄漏。


**为什么使用弱引用** :官方解释如下

> To help deal with very large and long-lived usages, the hash table entries use WeakReferences for keys.为了应对非常大和长时间的用途，哈希表使用弱引用的 key。

下面我们分两种情况讨论：

- **key 使用强引用**：引用的ThreadLocal的对象被回收了，但是ThreadLocalMap还持有ThreadLocal的强引用，如果没有手动删除，ThreadLocal不会被回收，导致Entry内存泄漏。

- **key 使用弱引用**：引用的ThreadLocal的对象被回收了，由于ThreadLocalMap持有ThreadLocal的弱引用，即使没有手动删除，ThreadLocal也会被回收。value在下一次ThreadLocalMap调用set,get，remove的时候会被清除。

比较两种情况，我们可以发现：由于ThreadLocalMap的生命周期跟Thread一样长，如果都没有手动删除对应key，都会导致内存泄漏，但是使用弱引用可以多一层保障：弱引用ThreadLocal不会内存泄漏，对应的value在下一次ThreadLocalMap调用set,get,remove的时候会被清除。

因此，ThreadLocal内存泄漏的根源是：由于ThreadLocalMap的生命周期跟Thread一样长，如果没有手动删除对应key就会导致内存泄漏，而不是因为弱引用。



**ThreadLocal使用示例**

```
public class ThreadLocalDemo {

    private static ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 0);

    public Integer getAndIncrement(){
        threadLocal.set(threadLocal.get() + 1);
        return threadLocal.get();
    }

    public static void main(String[] args) {
        ThreadLocalDemo demo = new ThreadLocalDemo();
        for (int i = 0; i < 3; i++){
            ThreadDemo demo1 = new ThreadDemo(demo);
            demo1.start();
        }
    }

    private static class ThreadDemo extends Thread{
        private ThreadLocalDemo demo;
        private ThreadDemo (ThreadLocalDemo demo){
            this.demo = demo;
        }

        @Override
        public void run() {
            for (int i = 0; i < 3; i++){
                System.out.println(Thread.currentThread().getName() + ":" + demo.getAndIncrement());
            }
        }
    }
}
```
**运行结果**
```
Thread-0:1
Thread-1:1
Thread-2:1
Thread-1:2
Thread-0:2
Thread-1:3
Thread-2:2
Thread-0:3
Thread-2:3
```
**ThreadLocal源码解析**

ThreadLocal  set流程

```
    public void set(T value) {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
    }
    
```
ThreadLocalMap set

```
private void set(ThreadLocal<?> key, Object value) {

    Entry[] tab = table;
    int len = tab.length;

    // 根据 ThreadLocal 的散列值，查找对应元素在数组中的位置
    int i = key.threadLocalHashCode & (len-1);

    // 采用“线性探测法”，寻找合适位置
    for (Entry e = tab[i]; e != null; e = tab[i = nextIndex(i, len)]) {
        ThreadLocal<?> k = e.get();
        // key 存在，直接覆盖
        if (k == key) {
            e.value = value;
            return;
        }
        // key == null，但是存在值（因为此处的e != null），说明之前的ThreadLocal对象已经被回收了
        if (k == null) {
            // 用新元素替换陈旧的元素
            replaceStaleEntry(key, value, i);
            return;
        }
    }
    // ThreadLocal对应的key实例不存在也没有陈旧元素，new 一个
    tab[i] = new Entry(key, value);
    int sz = ++size;
    // cleanSomeSlots 清楚陈旧的Entry（key == null）
    // 如果没有清理陈旧的 Entry 并且数组中的元素大于了阈值，则进行 rehash
    if (!cleanSomeSlots(i, sz) && sz >= threshold)
        rehash();
}
```
- ThreadLocal  get流程

```
public T get() {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null) {
        ThreadLocalMap.Entry e = map.getEntry(this);
        if (e != null) {
            @SuppressWarnings("unchecked")
            T result = (T)e.value;
            return result;
        }
    }
    return setInitialValue();
}
```