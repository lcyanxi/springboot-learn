 _站在巨人的肩膀上，仰望'星空'，脚踏实地_
 
# 大厂常见算法： 

[大厂常见算法](大厂常见算法/leetcode算法.md)

# Java并发：
- [反射实现原理](Java并发/反射获取class对象的方式.md)
  【1.单例模式真的就安全吗？2.为什么jdbc要用class.forName加载类?】
 - [Java8如何优化CAS性能](Java并发/Java8如何优化CAS性能.md) 
- [深入分析ThreadLocal原理](Java并发/深入分析ThreadLocal原理.md)
   【如何避免ThradLocal带来的内存泄露问题？】
- [Volatile原理](Java并发/Volatile原理.md)
  【什么是Java内存模型(JMM)?】
- [ReentrantLock原理分析](Java并发/ReentrantLock原理分析.md)
  【AQS同步队列为什么要使用双链表，单链表不行吗？】
- [Condition原理](Java并发/Condition原理.md)
   【如何手动实现一个生产者/消费者模型】
- [CountDownLatch原理分析](Java并发/CountDownLatch原理分析.md)
- [CyclicBarrier原理](Java并发/CyclicBarrier原理.md)

- [关于HashMap那些问题](Java并发/关于HashMap那些问题.md)
  【HashMap7除了死循环问题外还有那些线程不安全的地方？HashMap8呢？】
  


# Java虚拟机：
- [类加载器机制](Java虚拟机/类加载器.md)
  【1.int[]的类加载器是谁？ 2.SPI机制是如何打破双亲委派模型的？】

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
- [讲答疑文章(1)-日志和索引相关问题](数据库原理/讲答疑文章(1)-日志和索引相关问题.md)
  【两阶段提交是如何保证数据不丢的？】
- [讲事务隔离:为什么你改了我还看不见](数据库原理/讲事务隔离:为什么你改了我还看不见.md)
  【并发版本控制(mvcc)是如何实现的？】
- [为什么表数据删掉一半,表文件大小不变？](数据库原理/为什么表数据删掉一半,表文件大小不变？.md)
  【如何解决数据删除后产生的索引空洞问题？】
- [orderby是如何工作的？](数据库原理/orderby是怎么工作的.md)
- [讲普通索引和唯一索引,应该怎么选择？](数据库原理/讲普通索引和唯一索引,应该怎么选择.md)
  【从性能上分析唯一索引和普通索引有什么区别？】
- [count这么慢,我该怎么办?](数据库原理/count这么慢,我该怎么办.md)

  【1.count(*)真的是扫的全表吗？2.count(*)、count(1)、count(主键)和count(字段)的区别是是什么？】
  
- [幻读是什么,幻读有什么问题?](数据库原理/幻读是什么,幻读有什么问题.md)
  【InnoDB又是怎么解决幻读问题的？】


# Spring框架：
- [Spring Bean生命周期](Spring框架/Spring%20Bean生命周期) 
- [Spring容器初始化完成后将Apollo的配置缓存到本地](Spring框架/Spring容器初始化完成后将Apollo的配置缓存到本地.md) 
- [Spring中的循环依赖](Spring框架/Spring中的循环依赖.md) 
   【三级缓存为什么要使用工厂而不是直接使用引用？】
- [图解Spring事务的传播机制原理](Spring框架/图解Spring事务的传播机制原理.md)
  【@transactional注解在什么情况下会失效，为什么？】

# 分布式RPC框架： 
- [RPC实战与核心原理-基础篇](分布式RPC框架/RPC实战与核心原理-基础篇.md)
- [RPC实战与核心原理-进阶篇](分布式RPC框架/RPC实战与核心原理-进阶篇.md)
- [RPC实战与核心原理-高级篇](分布式RPC框架/RPC实战与核心原理-高级篇.md)
  【分布式环境下如何快速定位问题？】


- [关于RPC dubbo超时原理](分布式RPC框架/关于RPC%20dubbo超时原理.md)
  【接口的幂等性如何解决？】
- [动态代理在RPC框架中的性能对比](分布式RPC框架/动态代理在RPC框架中的性能对比.md)
- [Dubbo SPI与JDK SPI 实现原理分析](Dubbo%20SPI与JDK%20SPI%20实现原理分析.md)

  【1.Dubbo SPI仅仅只是解决JDK SPI无法按需加载问题吗？2.如何根据请求的参数来动态加载实现类？】
    
- [Dubbo最小活跃调用策略是如何实现的？](分布式RPC框架/Dubbo最小活跃调用策略是如何实现的.md)
- [Dubbo服务暴露过程](分布式RPC框架/Dubbo服务暴露过程.md)
- [Dubbo服务调用流程](分布式RPC框架/Dubbo服务调用流程.md)
 【一个request请求何如跟一个response对应上的？】






# 分库分表： 
- [基于当当网的shardingJdbc进行分库分表demo](分库分表/基于当当网的shardingJdbc进行分库分表.md)






