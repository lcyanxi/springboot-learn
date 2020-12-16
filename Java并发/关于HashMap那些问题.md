> 了解数据结构中的HashMap么？能跟我聊聊他的结构和底层原理么？

HashMap是我们非常常用的数据结构，由**数组和链表组合构成的数据结构**。

大概如下，数组里面每个地方都存了Key-Value这样的实例，在Java7叫Entry在Java8中叫Node。

![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpwgWpRnXONPxeCYuBLK2tuNqGyzxjeZfKEIHD35kp2RuL3MpDwM1FqbmdaBBsZyJkwsoRyGp6wCtQ/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

因为他本身所有的位置都为null，在put插入的时候会根据key的hash去计算一个index值。

就比如我put（”帅丙“，520），我插入了为”帅丙“的元素，这个时候我们会通过哈希函数计算出插入的位置，计算出来index是2那结果如下。

```
hash（“帅丙”）= 2
```
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpwgWpRnXONPxeCYuBLK2tuNFz2RyQ3dKia6IGvQWdACOJVh5kSTFARFNcib0icwzusCGjp4cazJ2b7ng/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

> 你提到了还有链表，为啥需要链表，链表又是怎么样子的呢？

组长度是有限的，在有限的长度里面我们使用哈希，哈希本身就存在概率性，就是”帅丙“和”丙帅“我们都去hash有一定的概率会一样，就像上面的情况我再次哈希”丙帅“极端情况也会hash到一个值上，那就形成了链表

![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpwgWpRnXONPxeCYuBLK2tuNJPRvt6VkO4TicXgAI4DYia5tt1vvuBAr3gLagRb4l1ibGyTiaAEia8weDzA/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

每一个节点都会保存自身的hash、key、value、以及下个节点，我看看Entry的源码

```
static class Entry<K,V> implements Map.Entry<K,V> {
    final K key;
    V value;
    Entry<K,V> next;
    int hash;
}
```
> 说到链表我想问一下，你知道新的Entry节点在插入链表的时候，是怎么插入的么？

java7之前是**头插法**，就是说新来的值会取代原有的值，原有的值就顺推到链表中去，就像上面的例子一样，**因为写这个代码的作者认为后来的值被查找的可能性更大一点，提升查找的效率**。

但是，在java8之后，都是所用**尾部插入**了。

> 为啥改为尾部插入呢？

首先我们看下**HashMap的扩容机制**：

数组容量是有限的，数据多次插入的，到达一定的数量就会进行扩容，也就是resize。

> 什么时候resize呢？

有两个因素：
- Capacity：HashMap当前长度。
- LoadFactor：负载因子，默认值0.75f。

怎么理解呢，就比如当前的容量大小为100，当你存进第76个的时候，判断发现需要进行resize了，那就进行扩容，但是HashMap的扩容也不是简单的扩大点容量这么简单的。

> 扩容？它是怎么扩容的呢？

分为两步
- **扩容**：创建一个新的Entry空数组，长度是原数组的2倍。
- **ReHash**：遍历原Entry数组，把所有的Entry重新Hash到新数组。

> 为什么要重新Hash呢，直接复制过去不香么？

**是因为长度扩大以后，Hash的规则也随之改变**。

**Hash的公式---> index = HashCode（Key） & （Length - 1）**

原来长度（Length）是8你位运算出来的值是2 ，新的长度是16你位运算出来的值明显不一样了。

扩容前：

![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpwgWpRnXONPxeCYuBLK2tuNpukI0InaDheeBnFVdVrl1b58ASSNNNrhksW8icoKDUb3nVMFQicISKyA/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

扩容后：

![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpwgWpRnXONPxeCYuBLK2tuNrGNHN4K8YV7T3QSzJjn06p0iawpzRKiccnHn8dv9cuC3avF2onetWfGg/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

> 为啥之前用头插法，java8之后改成尾插了呢？

**因为java7头插入法会造成循环链表**

![image](http://assets.processon.com/chart_image/5f976dc2e401fd06fda45d11.png)

我先举个例子吧，我们现在往一个容量大小为2的put两个值，负载因子是0.75是不是我们在put第二个的时候就会进行resize？

2*0.75 = 1 所以插入第二个就要resize了

现在我们要在容量为2的容器里面用不同线程插入A，B，C，假如我们在resize之前打个短点，那意味着数据都插入了但是还没resize那扩容前可能是这样的。

我们可以看到链表的指向A->B->C

**Tip：A的下一个指针是指向B的**

![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpwgWpRnXONPxeCYuBLK2tuNAkwVc0kP6ZoBcXubNg4BiaMbZlicQf7lFlwWgp79DDTNB9xOrY4oOUlg/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

因为resize的赋值方式，也就是使用了**单链表的头插入方式，同一位置上新元素总会被放在链表的头部位置**，在旧数组中同一条Entry链上的元素，通过重新计算索引位置后，有可能被放到了新数组的不同位置上。

就可能出现下面的情况，大家发现问题没有？

B的下一个指针指向了A

![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpwgWpRnXONPxeCYuBLK2tuNOPM5susic1r2mDsy8whX5ARtX7tTZhHLMaBohpnvMfHVKJWnn89SMAg/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

一旦几个线程都调整完成，就可能出现环形链表

![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpwgWpRnXONPxeCYuBLK2tuNYwElHbLux4e7LXN8afWrRulD2h4l7fNxibM335vvDNhLtPxZiaWsP3EA/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

> 那Java7头插法会形成死循环，这么大的一个bug，Java官方就没想办法修复一下吗？

因为人家已经明确说明了hashmap不是线程安全的，那为啥你一定要在多线程中使用它呢，并且也已经为你提供了线程安全的集合。所有，不是bug，是你用错地方了^_^


> 头插是JDK1.7的那1.8的尾插是怎么样的呢？
![image](http://assets.processon.com/chart_image/5fd8a48ee401fd06ddc70ae0.png)

因为**java8之后链表有红黑树**的部分，大家可以看到代码已经多了很多if else的逻辑判断了，红黑树的引入巧妙的**将原本O(n)的时间复杂度降低到了O(logn)**。

**使用头插会改变链表的上的顺序，但是如果使用尾插，在扩容时会保持链表元素原本的顺序，就不会出现链表成环的问题了。**

就是说原本是A->B，在扩容后那个链表还是A->B

![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpwgWpRnXONPxeCYuBLK2tuNpCrSUW9uiaIicaiaUBTU8D7B28ic3vqIJgFN6cJR3MKYBictriaCgckcl05g/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

Java7在多线程操作HashMap时可能**引起死循环**，**原因是扩容转移后前后链表顺序倒置，在转移过程中修改了原来链表中节点的引用关系。**

Java8在同样的前提下并不会引起死循环，**原因是扩容转移后前后链表顺序不变，保持之前节点的引用关**系。

> 那是不是意味着Java8就可以把HashMap用在多线程中呢？

即使不会出现死循环，但是通过源码看到put/get方法都没有加同步锁，**多线程情况最容易出现的就是：无法保证上一秒put的值，下一秒get的时候还是原值，所以线程安全还是无法保证**

还有就是**数据覆盖问题**


```
final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    // 如果table为null或者长度为0，则进行初始化
    // resize()方法本来是用于扩容，由于初始化没有实际分配空间，这里用该方法进行空间分配
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;
    // (n - 1) & hash：确保索引在数组范围内,相当于hash % n 的值
    if ((p = tab[i = (n - 1) & hash]) == null)
        //tab[i] 为null，直接将新的key-value插入到计算的索引i位置
        tab[i] = newNode(hash, key, value, null);
    else {
      // ......
    }
}
```

put操作的主函数， 在**p = tab[i = (n - 1) & hash]) == null**这一行，如果没有hash碰撞则会直接插入元素。如果线程A和线程B同时进行put操作，刚好这两条不同的数据hash值一样，并且该位置数据为null，所以这线程A、B都会进入这行代码中。假设一种情况，线程A进入后还未进行数据插入时挂起，而线程B正常执行，从而正常插入数据，然后线程A获取CPU时间片，此时线程A不用再进行hash判断了，问题出现：**线程A会把线程B插入的数据给覆盖，发生线程不安**全。

> 那HashMap的默认初始化长度是多少？

**看源码的时候初始化大小是16**

> 你那知道为啥是16么？

**在JDK1.8的 236行有1<<4就是16**，为啥用位运算呢？直接写16不好么？

们在创建HashMap的时候，阿里巴巴规范插件会提醒我们最好赋初值，而且最好是2的幂

![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpwgWpRnXONPxeCYuBLK2tuNJiblzSaCen8Hb9OZiawhGibd2Ps96gJP4ANhMp4LtiaMjEhwibXBmhe71lA/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

这样是为了位运算的方便，位与运算比算数计算的效率高了很多，之所以选择16，是为了服务将Key映射到index的算法。

我前面说了所有的key我们都会拿到他的hash，但是我们怎么尽可能的得到一个均匀分布的hash呢？

是的我们通过Key的HashCode值去做位运算。

我打个比方，key为”帅丙“的十进制为766132那二进制就是 10111011000010110100

![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpwgWpRnXONPxeCYuBLK2tuNdzUzLQ8gCInynLCcOvtcsZcpmzJtQInbBY9uRHMmXnNUwlyvXNzvbw/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

我们再看下index的计算公式：index = HashCode（Key） & （Length- 1）

![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpwgWpRnXONPxeCYuBLK2tuNosv9l4hJib8LjjribddkA6CL48R3LbcmVo0K9GAzMTs0hmAMT2ibnybFQ/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

15的的二进制是1111，那10111011000010110100 &1111 十进制就是4

之所以用位与运算效果与取模一样，性能也提高了不少！

> 那为啥用16不用别的呢？

因为在使用不是2的幂的数字的时候，Length-1的值是所有二进制位全为1，这种情况下，index的结果等同于HashCode后几位的值。

只要输入的HashCode本身分布均匀，Hash算法的结果就是均匀的。

这是为了**实现均匀分布**。

> 为啥我们重写equals方法的时候需要重写hashCode方法呢？你能用HashMap给我举个例子么？

因为在java中，所有的对象都是继承于Object类。Ojbect类中有两个方法equals、hashCode，这两个方法都是用来比较两个对象是否相等的。

在未重写equals方法我们是继承了object的equals方法，**那里的equals是比较两个对象的内存地址**，显然我们new了2个对象内存地址肯定不一样

- 对于值对象，==比较的是两个对象的值
- 对于引用对象，比较的是两个对象的地址

大家是否还记得我说的HashMap是通过key的hashCode去寻找index的，那index一样就形成链表了，也就是说”帅丙“和”丙帅“的index都可能是2，在一个链表上的。

我们去get的时候，他就是根据key去hash然后计算出index，找到了2，那我怎么找到具体的”帅丙“还是”丙帅“呢？

**equals**！是的，所以如果我们对equals方法进行了重写，建议一定要对hashCode方法重写，以保证相同的对象返回相同的hash值，不同的对象返回不同的hash值。

不然一个链表的对象，你哪里知道你要找的是哪个，到时候发现hashCode都一样，这不是完犊子嘛。

> 上面说过他是线程不安全的，那你能跟我聊聊你们是怎么处理HashMap在线程安全的场景么？

们一般都会使用**HashTable**或者**CurrentHashMap**，但是因为前者的并发度的原因基本上没啥使用场景了，所以存在线程不安全的场景我们都使用的是CorruentHashMap。

HashTable我看过他的源码，很简单粗暴，直接在方法上锁，并发度很低，最多同时允许一个线程访问，currentHashMap就好很多了，1.7和1.8有较大的不同，不过并发度都比前者好太多了。

```
public synchronized V put(K key, V value) {}
 
public synchronized V get(Object key) {}
```


> java7除了死循环问题外还有那些线程不安全的地方？

**扩容造成数据丢失分析过程**

初始时：

![image](https://mmbiz.qpic.cn/mmbiz_png/JdLkEI9sZfecWQvk4vhdQnUBzictjvKTV9qbl5IdZHTh075WLV8eZpGiaHBY2u0baTcwTg0Y27N3icKIYQofQRcrA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

线程A和线程B进行put操作，同样线程A挂起：

```
void transfer(Entry[] newTable, boolean rehash) {
    int newCapacity = newTable.length;
    for (Entry<K,V> e : table) {
        while(null != e) {
            Entry<K,V> next = e.next;
            // 只有产生了新的hash表才需要重新计算hash值
            if (rehash) {
                e.hash = null == e.key ? 0 : hash(e.key);
            }
            // i = oldIndex 或 i = oldIndex + oldCapacity
            int i = indexFor(e.hash, newCapacity);
            // 链表前插法
            e.next = newTable[i]; // 线程A在这里挂起
            newTable[i] = e;
            e = next;
        }
    }
}
```
此时线程A的运行结果如下：

![image](https://mmbiz.qpic.cn/mmbiz_png/JdLkEI9sZfecWQvk4vhdQnUBzictjvKTV0mE04nJPQHmSiaJpjVgV961cZOHvNVImchDLqzlC63WCAIiap5HWKZRQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

此时线程B已获得CPU时间片，并完成resize操作：

![image](https://mmbiz.qpic.cn/mmbiz_png/JdLkEI9sZfecWQvk4vhdQnUBzictjvKTV8icvQkHlaA3y2IK4flVgmprvIjuXEcIZaqiawTfpPZAw9R1xwz8ICoEg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

同样注意由于线程B执行完成，newTable和table都为最新值：5.next=null。
此时切换到线程A，在线程A挂起时：e=7，next=5，newTable[3]=null。
执行newtable[i]=e，就将**7放在了table[3]**的位置，此时next=5。接着进行下一次循环：

```
e=5
next=e.next ----> next=null，从主存中取值
e.next=newTable[1] ----> e.next=5，从主存中取值
newTable[1]=e ----> newTable[1]=5
e=next ----> e=null
```

将5放置在table[1]位置，此时e=null循环结束，3元素丢失，并形成环形链表。并在后续操作hashmap时造成死循环。
![image](https://mmbiz.qpic.cn/mmbiz_png/JdLkEI9sZfecWQvk4vhdQnUBzictjvKTVFF64CbJ5GTu4NxhPBpheia6wmdTncUGeQ4YGdNuG4BknRiaCHZw6Hxsg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)


[原文地址](https://mp.weixin.qq.com/s/0Gf2DzuzgEx0i3mHVvhKNQ)v