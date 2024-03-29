> 问题：分布式锁，如何实现高并发？

**问题场景：热点库存扣减问题**

秒杀场景，有一个难度的问题：热点库存扣减问题。

- 既要保证不发生超卖
- 又要保证高并发

如何解决这个高难度的问题呢？ 答案就是使用redis 分段锁。

**普通分布式锁的性能问题**

分布式锁一旦加了之后，对同一个商品的下单请求，会导致所有下单操作，都必须对同一个商品key加分布式锁。

假设某个场景，一个商品1分钟6000订单，每秒的 600个下单操作，

假设加锁之后，释放锁之前，查库存 -> 创建订单 -> 扣减库存，每个IO操作10ms，大概30毫秒。

可以再进行一下优化，将 创建订单 + 扣减库存 并发执行，将两个10ms 减少为一个10ms，这既是空间换时间的思想，大概20毫秒。
![image](https://pic2.zhimg.com/80/v2-83938d7f569f34b8039bfe69d02f9f41_720w.webp)

将 创建订单 + 扣减库存 批量执行，减少一次IO，也是大概20毫秒。也就是单个商品而言，只有 50 QPS.
![image](https://pic4.zhimg.com/v2-9d033b065d685b696295e507c3e136cb_r.jpg)
假设一个商品sku的数量是10000,10秒内秒杀完，也就是单个商品而言，需要单商品 100 QPS，如何应对一个商品的 100qps秒杀。

甚至，如果单商品需要 1000qps秒杀呢？

答案是，使用 分段加锁。

**优化之后：使用Redis分段锁提升秒杀的并发性能**

回到前面的场景：

> 假设一个商品1分钟6000订单，每秒的 600个下单操作，
假设加锁之后，释放锁之前，查库存 -> 创建订单 -> 扣减库存，经过优化，每个IO操作100ms，大概200毫秒，一秒钟5个订单。

如何提高性能呢？ 空间换时间

为了达到每秒600个订单，可以将锁分成 600 /5 =120 个段，反过来， 每个段1秒可以操作5次， 120个段，合起来，及时每秒操作600次。

进行抢夺锁的，如果申请到一个具体的段呢？

- 随机路由法
- hash取模法

如果是用随机路由算法，可以将请求随机到一个分段， 如果不行，就轮询下一个分段，具体的流程，大致如下：
![image](https://pic2.zhimg.com/80/v2-d48124721271751932db7a9cc0b79785_720w.webp)
> 这个是一个理论的时间预估，没有扣除 尝试下一个分段的 时间, 另外，实际上的性能， 会比理论上差，从咱们实操案例的测试结果，也可以证明这点。


随机路由法的问题：

不同分端之间，可能库存消耗不均，导致部分用户无法扣减库存，反复进行重试，拖慢系统性能。

如何进一步优化： hash取模法。

**第二次优化之后：使用hash取模法，减少库存消耗不均和无效重试**

由于秒杀场景的分布式锁，实际上是为了防止超卖， 和库存是强相关的。

所以，可以结合库存，把秒杀的分布式锁进行改进。

第一步： 把redis 的分段方式进行演进，额外增加一个总库存分段锁，用于分配存储剩余的总库存。采用多批次少量分配的思路，通过定时任务，从总库存向分段库存中迁移库存。

第二步：使用hash取模法，把用户路由到某一个分段，如果分段里边的库存耗光了，就去访问剩余的总库存。

![image](https://pic3.zhimg.com/80/v2-0ea943b45e9f6a8fbc4a8fc0c098a276_720w.webp)

**库存动态迁移**

为了防止分段多库存耗光，大家都去抢占总库存锁。

采用多批次少量分配的思路，通过定时任务，从总库存向分段库存中迁移库存。

![image](https://pic4.zhimg.com/80/v2-602c3f4ee0d8a6d68688ae9f2049f80b_720w.webp)

至此， hash取模法的分段锁设计方案，已经完美实现。

[Redis分布式锁解决高并发场景](https://zhuanlan.zhihu.com/p/268290754)