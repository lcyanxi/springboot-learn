 _站在巨人的肩膀上，仰望'星空'，脚踏实地_
 
# 大厂常见算法： 

[大厂常见算法](大厂常见算法/leetcode算法.md)

# 设计模式：
- [策略模式](设计模式/策略模式.md)
- [观察者模式](设计模式/观察者模式.md) 【Spring Event && Guava Event】

# Java基础：
- [BIO/NIO/AIO网络模型](Java基础/IO网络模型.md)
- [零拷贝原理](消息队列/零拷贝原理.md)【为什么rocket采取mmap而不是sendfile方式呢？】
- [堆外内存原理](Java基础/堆外内存原理.md)
- [Java自定义注解-Annotation](Java基础/Java自定义注解-Annotation.md)【元注解都有那些？有什么作用】


# Java并发：
- [Object notify()会立刻释放锁么?](Java并发/Object%20notify()会立刻释放锁么.md)【wait会放弃cpu资源吗】
- [Synchronized锁机制](Java并发/Synchronized锁机制.md)【对象在内存中的存储布局,一个对象最小占多少字节？】
- [反射实现原理](Java并发/反射获取class对象的方式.md)【1.单例模式真的就安全吗？2.为什么jdbc要用class.forName加载类?】
- [Java8如何优化CAS性能](Java并发/Java8如何优化CAS性能.md) 
- [深入分析ThreadLocal原理](Java并发/深入分析ThreadLocal原理.md)【如何避免ThradLocal带来的内存泄露问题？】
- [Volatile原理](Java并发/Volatile原理.md)【什么是Java内存模型(JMM)?】
- [LockSupport阻塞和唤醒线程](Java并发/LockSupport阻塞和唤醒线程.md)【waitStatus的作用是什么？】
- [ReentrantLock原理分析](Java并发/ReentrantLock原理分析.md)【AQS同步队列为什么要使用双链表，单链表不行吗？】
- [Condition原理](Java并发/Condition原理.md)【如何手动实现一个生产者/消费者模型】
- [CountDownLatch原理分析](Java并发/CountDownLatch原理分析.md)
- [CyclicBarrier原理](Java并发/CyclicBarrier原理.md)
- [Semaphore原理](Java并发/Semaphore原理.md)【如果要拿到每个线程的返回值会有什么问题？completionService考虑一下】
- [Java线程池实现原理](Java并发/Java线程池实现原理.md) 【ExecutorService的submit返回的Future有什么性能上的问题吗？】
- [CompletionService解决获取返回值阻塞问题](Java并发/CompletionService解决获取返回值阻塞问题.md)
  【如何手动实现一个线程池？】
- [关于HashMap那些问题](Java并发/关于HashMap那些问题.md)【HashMap7除了死循环问题外还有那些线程不安全的地方？HashMap8呢？】
  


# Java虚拟机：
- [类加载器机制](Java虚拟机/类加载器.md)【1.int[]的类加载器是谁？ 2.SPI机制是如何打破双亲委派模型的？】
- [自定义类加载器实现热加载](Java虚拟机/自定义类加载器实现热加载.md)
- [JVM内存模型-方法区](Java虚拟机/JVM内存模型-方法区.md)【方法区的演进细节】
- [JVM内存模型-堆区](Java虚拟机/JVM内存模型-堆区.md)【堆中一定都是线程共享吗,什么是TLAB？】
- [JVM内存模型-虚拟机栈](Java虚拟机/JVM内存模型-虚拟机栈.md)【方法内的局部变量是线程安全的吗？】
- [JVM内存模型-程序计数器](Java虚拟机/JVM内存模型-程序计数器.md)【程序计数器作用是什么？】
- [JVM内存模型-本地方法栈](Java虚拟机/JVM内存模型-本地方法栈.md)
- [StringTable](Java虚拟机/StringTable.md)【JDK9对String数据结构做了什么调整？为什么？】
- [垃圾回收算法](Java虚拟机/垃圾回收算法.md)
- [强、软、弱、虚引用使用场景](Java虚拟机/强、软、弱、虚引用使用场景.md)
- [7种垃圾回收器横向纵向对比](Java虚拟机/7种垃圾回收器横向纵向对比.md)

# 计算机网络： 
- [输入网址后，期间发生了什么？](计算机网络/输入网址后，期间发生了什么.md)
- [TCP 重传、滑动窗口、流量控制、拥塞控制机制？](计算机网络/TCP%20重传、滑动窗口、流量控制、拥塞控制机制.md)
  【数据传输过程怎么解决丢包问题？】
- [HTTP1.0/1.1/2/3演进过程中都解决了什么问题？](计算机网络/HTTP演进过程中都解决了什么问题.md)
  【分布式环境下session解决方案有哪些？】
- [八幅漫画理解使用JSON Web Token设计单点登录系统](计算机网络/八幅漫画理解使用JSON%20Web%20Token设计单点登录系统.md)

# 数据库原理： 
- [一条查询/更新SQL语句是如何执行的？](数据库原理/一条SQL查询或更新语句是如何执行的.md)
- [讲答疑文章(1)-日志和索引相关问题](数据库原理/讲答疑文章(1)-日志和索引相关问题.md)【两阶段提交是如何保证数据不丢的？】
- [讲事务隔离:为什么你改了我还看不见](数据库原理/讲事务隔离:为什么你改了我还看不见.md)【并发版本控制(mvcc)是如何实现的？】
- [为什么表数据删掉一半,表文件大小不变？](数据库原理/为什么表数据删掉一半,表文件大小不变？.md)【如何解决数据删除后产生的索引空洞问题？】
- [Order by是如何工作的？](数据库原理/orderby是怎么工作的.md)
- [为什么有时候会选错索引?](数据库原理/为什么有时候会选错索引.md)【force index的作用？】
- [讲普通索引和唯一索引,应该怎么选择？](数据库原理/讲普通索引和唯一索引,应该怎么选择.md)【从性能上分析唯一索引和普通索引有什么区别？】
- [Count这么慢,我该怎么办?](数据库原理/count这么慢,我该怎么办.md)【1.count(🌟)真的是扫全表吗？2.count(🌟、1、主键、字段)区别是什么？】
- [怎么减少行锁对性能的影响?](数据库原理/怎么减少行锁对性能的影响.md)【1.什么是死锁检测, 2.怎么解决由热点行更新导致的性能问题呢？】
- [幻读是什么,幻读有什么问题?](数据库原理/幻读是什么,幻读有什么问题.md)【InnoDB又是怎么解决幻读问题的？】
- [Explain执行计划详解](数据库原理/Explain执行计划详解.md)【主要关注type、key、rows、filtered、Extra】
- [Limit Offset优化方案](数据库原理/Limit%20Offset优化方案.md)【基于游标分页】
- [Mysql并行复制原理](数据库原理/Mysql并行复制原理.md)【主从延迟的原因有那些？】





# Spring框架：
- [自定义注解Autowired实现](Spring框架/自定义注解Autowired实现.md) 【IOC实现原理： 工厂 + 反射】
- [Spring Bean生命周期](Spring框架/Spring%20Bean生命周期) 
- [Spring容器初始化完成后将Apollo的配置缓存到本地](Spring框架/Spring容器初始化完成后将Apollo的配置缓存到本地.md) 
- [Spring中的循环依赖](Spring框架/Spring中的循环依赖.md) 【三级缓存为什么要使用工厂而不是直接使用引用？】
- [图解Spring事务的传播机制原理](Spring框架/图解Spring事务的传播机制原理.md)【@transactional注解在什么情况下会失效，为什么？】

# 分布式RPC框架： 
- [RPC实战与核心原理-基础篇](分布式RPC框架/RPC实战与核心原理-基础篇.md)
- [RPC实战与核心原理-进阶篇](分布式RPC框架/RPC实战与核心原理-进阶篇.md)
- [RPC实战与核心原理-高级篇](分布式RPC框架/RPC实战与核心原理-高级篇.md)【分布式环境下如何快速定位问题？】
- [TraceId如何在RPC中传递](分布式RPC框架/TraceId如何在RPC中传递.md)【InternalThreadLocal性能如何提升的？】
- [分布式追踪系统原理-SkyWalking](分布式RPC框架/分布式追踪系统原理-SkyWalking.md)【traceId 如何保证全局唯一?】
- [关于RPC dubbo超时原理](分布式RPC框架/关于RPC%20dubbo超时原理.md)【接口的幂等性如何解决？】
- [动态代理在RPC框架中的性能对比](分布式RPC框架/动态代理在RPC框架中的性能对比.md)
- [Dubbo SPI与JDK SPI 实现原理分析](Dubbo%20SPI与JDK%20SPI%20实现原理分析.md)
  【1.Dubbo SPI仅仅只是解决JDK SPI无法按需加载问题吗？2.如何根据请求的参数来动态加载实现类？】
- [Dubbo最小活跃调用策略是如何实现的？](分布式RPC框架/Dubbo最小活跃调用策略是如何实现的.md)
- [Dubbo服务暴露过程](分布式RPC框架/Dubbo服务暴露过程.md)
- [Dubbo服务调用流程](分布式RPC框架/Dubbo服务调用流程.md)【一个request请求何如跟一个response对应上的？】

# Redis
- [基于Redis的分布式锁到底安全吗](Redis框架/基于Redis的分布式锁到底安全吗.md)【看门狗机制】

# zookeeper： 
- [ZooKeeper分布式锁实现原理](zookeeper/ZooKeeper分布式锁实现原理.md)【零时有序节点 + Watcher机制】



# 分库分表： 
- [基于当当网的shardingJdbc进行分库分表demo](分库分表/基于当当网的shardingJdbc进行分库分表.md)

# 消息队列： 
- [消息队列高手课-基础篇](消息队列/消息队列高手课-基础篇.md)
- [消息队列高手课-进阶篇](消息队列/消息队列高手课-进阶篇.md)
##### RocketMQ
- [RocketMQ高性能之底层存储设计](消息队列/RocketMQ高性能之底层存储设计.md)
- [RocketMQ消息存储原理](消息队列/RocketMQ消息存储原理.md)【零拷贝(mmap) + PageChe + 顺序写来提高吞吐量】
- [零拷贝原理](消息队列/零拷贝原理.md)【为什么rocket采取mmap而不是sendfile方式呢？】
- [构建基于RocketMQ的分布式事务服务](消息队列/构建基于RocketMQ的分布式事务服务.md)
- [RocketMQ消息幂等通用解决方案](消息队列/RocketMQ消息幂等通用解决方案.md)【要么基于mysql要么基于redis】
- [RocketMQ消息ACK机制及消费进度管理](消息队列/RocketMQ消息ACK机制及消费进度管理.md)
- [RocketMQ消息文件过期原理](消息队列/RocketMQ消息文件过期原理.md) 【72小时 + 凌晨4点】
- [RocketMQ消息高可靠](消息队列/RocketMQ消息高可靠.md)【同步策略 + 刷盘策略 + ACK】
- [RocketMQ长轮询机制](消息队列/RocketMQ长轮询机制.md)【pull && push的优缺点】
- [RocketMQ消息重试原理](消息队列/RocketMQ消息重试原理.md)【默认重试次数是16次那为什么延迟消息级别是18个？】
##### kafka
- [Kafka如何实现每秒几十万的高并发写入](消息队列/Kafka如何实现每秒几十万的高并发写入.md)【零拷贝(sendfile) + pageChe + 顺序写】
- [Kafka文件存储机制](消息队列/Kafka文件存储机制.md)【在 partition 中如何通过 offset 查找 message？】









