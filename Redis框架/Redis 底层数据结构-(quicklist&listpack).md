在 Redis 3.0 之前，List 对象的底层数据结构是**双向链表或者压缩列表**。然后在 Redis 3.2 的时候，List 对象的底层改由 quicklist 数据结构实现。

其实 quicklist 就是「双向链表 + 压缩列表」组合，因为一个 quicklist 就是一个链表，而链表中的每个元素又是一个压缩列表。

在前面讲压缩列表的时候，我也提到了压缩列表的不足，虽然压缩列表是通过紧凑型的内存布局节省了内存开销，但是因为它的结构设计，如果保存的**元素数量增加，或者元素变大了，压缩列表会有「连锁更新」的风险，一旦发生，会造成性能下降**。

quicklist 解决办法，通过**控制每个链表节点中的压缩列表的大小或者元素个数，来规避连锁更新的问题**。因为压缩列表元素越少或越小，连锁更新带来的影响就越小，从而提供了更好的访问性能。

# quicklist 结构设计
quicklist 的结构体跟链表的结构体类似，都包含了表头和表尾，区别在于 quicklist 的节点是 quicklistNode。


```
typedef struct quicklist {
    //quicklist的链表头
    quicklistNode *head;      
    //quicklist的链表尾
    quicklistNode *tail; 
    //所有压缩列表中的总元素个数
    unsigned long count;
    //quicklistNodes的个数
    unsigned long len;       
    ...
} quicklist;
```
quicklistNode 的结构定义：

```
typedef struct quicklistNode {
    //前一个quicklistNode
    struct quicklistNode *prev;     
    //下一个quicklistNode
    struct quicklistNode *next;     
    //quicklistNode指向的压缩列表
    unsigned char *zl;              
    //压缩列表的的字节大小
    unsigned int sz;                
    //压缩列表的元素个数
    unsigned int count : 16;        //ziplist中的元素个数 
    ....
} quicklistNode;
```

可以看到，quicklistNode 结构体里包含了前一个节点和下一个节点指针，这样每个 quicklistNode 形成了一个双向链表。但是链表节点的元素不再是单纯保存元素值，而是保存了一个压缩列表，所以 quicklistNode 结构体里有个指向压缩列表的指针 *zl。

![image](https://cdn.xiaolincoding.com//mysql/other/f46cbe347f65ded522f1cc3fd8dba549.png)

在向 quicklist 添加一个元素的时候，不会像普通的链表那样，直接新建一个链表节点。而是会检查插入位置的压缩列表是否能容纳该元素，如果能容纳就直接保存到 quicklistNode 结构里的压缩列表，如果不能容纳，才会新建一个新的 quicklistNode 结构。

quicklist 会控制 quicklistNode 结构里的压缩列表的大小或者元素个数，来规避潜在的连锁更新的风险，但是这**并没有完全解决连锁更新的问题**。

# listpack

quicklist 虽然通过控制 quicklistNode 结构里的压缩列表的大小或者元素个数，来减少连锁更新带来的性能影响，但是并没有完全解决连锁更新的问题。

因为 quicklistNode 还是用了压缩列表来保存元素，压缩列表连锁更新的问题，来源于它的结构设计，所以要想彻底解决这个问题，需要设计一个新的数据结构。

于是，Redis 在 5.0 新设计一个数据结构叫 listpack，目的是替代压缩列表，它最大特点是 listpack 中**每个节点不再包含前一个节点的长度了，压缩列表每个节点正因为需要保存前一个节点的长度字段，就会有连锁更新的隐患**。



> 最新 6.2 发行版本中，Redis Hash 对象、ZSet 对象的底层数据结构的压缩列表还未被替换成 listpack，而 Redis 的最新代码（还未发布版本）已经将所有用到压缩列表底层数据结构的 Redis 对象替换成 listpack 数据结构来实现，估计不久将来，Redis 就会发布一个将压缩列表为 listpack 的发行版本。

# listpack 结构设计

listpack 采用了压缩列表的很多优秀的设计，比如还是用一块连续的内存空间来紧凑地保存数据，并且为了节省内存的开销，listpack 节点会采用不同的编码方式保存不同大小的数据。

![image](https://cdn.xiaolincoding.com//mysql/other/4d2dc376b5fd68dae70d9284ae82b73a.png)

listpack 头包含两个属性，分别记录了 listpack 总字节数和元素数量，然后 listpack 末尾也有个结尾标识。图中的 listpack entry 就是 listpack 的节点了。
![image](https://cdn.xiaolincoding.com//mysql/other/c5fb0a602d4caaca37ff0357f05b0abf.png)

主要包含三个方面内容：

- encoding：定义该元素的编码类型，会对不同长度的整数和字符串进行编码；
- data：实际存放的数据；
- len：encoding + data 的总长度；

可以看到，listpack 没有压缩列表中记录前一个节点长度的字段了，listpack 只记录当前节点的长度，当我们向 listpack 加入一个新元素的时候，不会影响其他节点的长度字段的变化，从而避免了压缩列表的连锁更新问题。



# 总结

**ziplist**

优点：
1. 【节省内存】内存紧凑型的数据结构，不存储前后节点的指针，占用一块连续的内存空间；

缺点：

1. 【查找复杂度高】ziplist 元素过多时，访问性能会降低；
2. 【潜在的连锁更新风险】不能保存过大的元素，否则新增或修改数据时，容易导致内存重新分配，甚至可能引发连锁更新的问题。

**quicklist**

优点：
1. 结合了链表和 ziplist 各自的优势；
2. 一个 quicklist 就是一个链表，而链表中的每个元素又是一个 ziplist；
3. quicklist 通过控制每个 quicklistNode 中 ziplist 的大小或是元素个数，就有效减少了在 ziplist 中新增或修改元素后，发生连锁更新的情况，从而提供了更好的访问性能。

缺点：

1. 【潜在的连锁更新风险】有效减少但未完全避免；
2. 【内存开销大】quicklist 使用 quicklistNode 结构指向每个 ziplist；

**listpack**

优点：
1. 【节省内存】用一块连续的内存空间来紧凑地保存数据；
2. 为了【进一步节省内存空间】，listpack 元素会对不同长度的整数和字符串进行不同的编码；
3. 【避免连锁更新】；




> 问题：压缩列表的 entry 为什么要保存 prevlen 呢？listpack 改成 len 之后不会影响功能吗？

- 压缩列表的 entry 保存 prevlen 是为了实现节点从后往前遍历，知道前一个节点的长度，就可以计算前一个节点的偏移量。



