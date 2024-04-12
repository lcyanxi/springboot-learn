## ### # 读写锁的使用场景

**HashMap / ConcurrentHashMap 配置缓存**
HashMap 是一种基于哈希表的集合类，它提供了快速的插入、查找和删除操作。

HashMap 是很多程序员接触的第一种缓存 , 因为现实业务场景里，我们可能需要给缓存添加缓存统计、过期失效、淘汰策略等功能，HashMap 的功能就显得孱弱 ，所以 HashMap 在业务系统中使用得并不算多。

但 HashMap 在中间件中却是香饽饽，我们消息中间件 RocketMQ 为例。
![image](https://note.youdao.com/yws/res/19814/WEBRESOURCE481d14f2b98d5712c729bdfe62ab2467)

上图是 RocketMQ 的集群模式 ，Broker 分为 Master 与 Slave，一个 Master 可以对应多个 Slave，但是一个 Slave 只能对应一个 Master。

每个 Broker 与 Name Server 集群中的所有节点建立**长连接**，定时每隔 30 秒注册 主题的路由信息到所有 Name Server。

消息发送者、消息消费者，在同一时间只会连接  Name Server 集群中的一台服务器，并且会每隔 30s 会定时更新 Topic 的路由信息。

我们可以理解 Name Server 集群的作用就是注册中心，注册中心会保存路由信息（主题的读写队列数、操作权限等），路由信息就是保存在 HashMap 中 。

![image](https://developer.qcloudimg.com/http-save/yehe-2596032/b17d0cfb034583df4fe18827b8033ecf.jpg)

路由信息通过几个 HashMap 来保存，当 Broker 向 Nameserver 发送心跳包（路由信息），Nameserver 需要对 HashMap 进行数据更新，但我们都知道 HashMap 并不是线程安全的，高并发场景下，**容易出现 CPU 100% 问题**，所以更新 HashMap 时需要加锁，RocketMQ 使用了 JDK 的读写锁 **ReentrantReadWriteLock** 。

下面我们看下路由信息如何更新和读取：

#### 1、写操作：更新路由信息，操作写锁

![image](https://developer.qcloudimg.com/http-save/yehe-2596032/2e113bd1fb8baa89b9d719e5c9bdb8fb.jpg)

#### 2、读操作：查询主题信息，操作读锁

![image](https://developer.qcloudimg.com/http-save/yehe-2596032/bfc8f7adf989775a69a41625a731a679.jpg)

同时，我们需要注意 Name Server 维护路由信息还需要定时任务的支撑。

- 每个 Broker 定时每隔 30 秒注册 主题的路由信息到所有 Name Server
- Name Server 定时任务每隔 10 秒清除已宕机的 Broker

我们做一个小小的总结，Name Server 维护路由的模式是：**HashMap + 读写锁 + 定时任务更新**。

- HashMap 作为存储容器
- 读写锁控制锁的颗粒度
- 定时任务定时更新缓存

写到这里，我们不禁想到 ConcurrentHashMap  。

ConcurrentHashMap 可以保证线程安全，JDK1.7 之前使用**分段锁机制**实现，JDK1.8 则使用**数组+链表+红黑树**数据结构和**CAS原子操作**实现。

Broker 使用不同的 ConcurrentHashMap 分别用来存储消费组、消费进度、消息过滤信息等。

> 那么 Name Server 服务为什么不使用 ConcurrentHashMap 作为存储容器呢 ？

最核心的原因在于：**路由信息由多个 HashMap 组成**，通过每次写操作可能要操作多个对象 ，为了**保证其一致性**，所以才需要加读写锁。