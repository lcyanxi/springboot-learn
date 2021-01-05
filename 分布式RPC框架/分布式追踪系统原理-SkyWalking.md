### 前言
在微服务架构中，一次请求往往涉及到多个模块，多个中间件，多台机器的相互协作才能完成。这一系列调用请求中，有些是串行的，有些是并行的，那么如何确定这个请求背后调用了哪些应用，哪些模块，哪些节点及调用的先后顺序？如何定位每个模块的性能问题？本文将为你揭晓答案。

本文将会从以下几个方面来阐述
- 分布式追踪系统原理及作用
- SkyWalking的原理及架构设计

### 分布式追踪系统的原理及作用
如何衡量一个接口的性能好坏，一般我们至少会关注以下三个指标
- 接口的 RT 你怎么知道?
- 是否有异常响应?
- 主要慢在哪里?

##### 单体架构
在初期，公司刚起步的时候，可能多会采用如下单体架构，对于单体架构我们该用什么方式来计算以上三个指标呢?
![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ3p6ZwV4ZkIyz1W1awsZXEdpJHWxLKtAsj8uw7c3as5Q3q0ccjicjAXaQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

**最容易想到的显然是用 AOP**

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ3YFrHRiabrTtMU29N1xJY0nZS0Au3Iqh0q0S3LQnX9B6HtohuBS95FMA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

使用 AOP 在调用具体的业务逻辑前后分别打印一下时间即可计算出整体的调用时间，使用 AOP 来 catch 住异常也可知道是哪里的调用导致的异常。

##### 微服务架构
在单体架构中由于所有的服务，组件都在一台机器上，所以相对来说这些监控指标比较容易实现，不过随着业务的快速发展，单体架构必然会朝微服务架构发展，如下

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ3741KB1bHtYqPKHciabES7XPK7oiaiaAMa19vgKrC6SBUiazbXibjzzXXlfQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

如果有用户反馈某个页面很慢，我们知道这个页面的请求调用链是 A ----->  C ----->  B ----->  D，此时如何定位可能是哪个模块引起的问题。每个服务 Service A,B,C,D 都有好几台机器。怎么知道某个请求调用了服务的具体哪台机器呢？

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ35O997J26IesOxjJXaeUfjBLwKiaW44hxcMzAO4G7JZmdhic2VTCF1ePQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

可以明显看到，由于无法准确定位每个请求经过的确切路径，在微服务这种架构下有以下几个痛点
- 排查问题难度大，周期长
- 特定场景难复现
- 系统性能瓶颈分析较难

分布式调用链就是为了解决以上几个问题而生，它主要的作用如下
- 自动采取数据  
- 分析数据产生完整调用链：有了请求的完整调用链，问题有很大概率可复现
- 数据可视化：每个组件的性能可视化，能帮助我们很好地定位系统的瓶颈，及时找出问题所在

通过分布式追踪系统能很好地定位如下请求的每条具体请求链路，从而轻易地实现请求链路追踪，每个模块的性能瓶颈定位与分析。

![image]( https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ3ib3vKTxiceC9ppmQ4ZElO5ibRbhcO3HWfjxErKJicXm5anTwGKOkribQEMw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

##### 分布式调用链标准 - OpenTracing
知道了分布式调用链的作用，那我们来看下如何实现分布式调用链的实现及原理， 首先为了解决不同的分布式追踪系统 API 不兼容的问题，诞生了 OpenTracing 规范，OpenTracing 是一个轻量级的标准化层，它位于应用程序/类库和追踪或日志分析程序之间。

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ3icTG0sSCeyc9hzJRcsa2jf1Ta0aZZYmibiaicv79slJYMwjaMIKicuvLMbQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

这样 OpenTracing 通过提供平台无关，厂商无关的 API，使得开发人员能够方便地添加追踪系统的实现。
说到这大家是否想过 Java 中类似的实现？还记得 JDBC 吧，通过提供一套标准的接口让各个厂商去实现，程序员即可面对接口编程，不用关心具体的实现。这里的接口其实就是标准，所以制定一套标准非常重要，可以实现组件的可插拔。

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ3Ccgx6xAdibTXTmibJNsZPRJk5nKNURFXqWIuTdoDJI2Lq3lzGeRtVTrw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

**接下来我们来看OpenTracing的数据模型，主要有以下三个**

- Trace：一个完整请求链路
- Span：一次调用过程(需要有开始时间和结束时间)
- SpanContext：Trace的全局上下文信息,如里面有traceId

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ30waDoWWMseyicibktbic82rOW9ib2lFCJNWMGtnKwS96oJwMibeTU8RyGZA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

如图示，一次下单的完整请求完整就是一个 Trace, 显然对于这个请求来说，必须要有一个全局标识来标识这一个请求，每一次调用就称为一个Span，每一次调用都要带上全局的TraceId,这样才可把全局TraceId与每个调用关联起来，**这个 TraceId 就是通过 SpanContext 传输的，既然要传输显然都要遵循协议来调用**。如图示，我们把传输协议比作车，把SpanContext 比作货，把Span比作路应该会更好理解一些。

理解了这三个概念，接下来我看看分布式追踪系统如何采集统一图中的微服务调用链

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ31wB0YT42UIA8kj5pfahrJRc3p8bAlb83IiawsLGbT2iarbdzvlqDxItw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

我们可以看到底层有一个 Collector 一直在默默无闻地收集数据，那么每一次调用 Collector 会收集哪些信息呢。
- **全局 trace_id**：这是显然的，这样才能把每一个子调用与最初的请求关联起来
- *span_id*: 图中的 0，1，1.1，2,这样就能标识是哪一个调用
- **parent_span_id**：比如 b 调用 d 的  span_id 是 1.1，那么它的 parent_span_id 即为 a 调用 b 的 span_id 即 1，这样才能把两个紧邻的调用关联起来。

有了这些信息，Collector 收集的每次调用的信息如下

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ3RJcr8wfqpAUTEmIiaibqcFZQo49drJ9ktI0icicdlb6dWATEOR32xKh4Ww/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

根据这些图表信息显然可以据此来画出调用链的可视化视图如下

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ3oW932BckmNOh1s2xbdACCIBpNHZ8eoGeeMZgd89U9a9v4Sy2icop7AA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

于是一个完整的分布式追踪系统就实现了。

以上实现看起来确实简单，但有以下几个问题需要我们仔细思考一下
- 怎么自动采集span数据：自动采集，对业务代码无侵入
- 如何跨进程传递 context
- traceId 如何保证全局唯一
- 请求量这么多采集会不会影响性能

接下我来看看 SkyWalking 是如何解决以上四个问题的

### SkyWalking的原理及架构设计
##### 怎么自动采集 span 数据
SkyWalking 采用了**插件化 + javaagent** 的形式来实现了 span 数据的自动采集，这样可以做到对代码的 **无侵入性**，插件化意味着可插拔，扩展性好

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ3QkyAIWWMial1Z8XOS4Eg6sK2J51MndRUrxmHRqtIcetCrxsS01HwbSw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

##### 如何跨进程传递 context
我们知道数据一般分为 header 和 body, 就像 http 有 header 和 body, RocketMQ 也有 MessageHeader，Message Body, body 一般放着业务数据，所以不宜在 body 中传递 context，应该在 header 中传递 context，如图示

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ3DicGdGfW9ia3miaVFo4IINhoicoOibibjJby1z6xLAicVr3QdPJlEAia46vGyg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

dubbo 中的 attachment 就相当于 header ,所以我们把 context 放在 attachment 中，这样就解决了 context 的传递问题。

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ369YusFcjAvHEM9zDuK0I4sTIjCXY4icontz8n0ePAP5PyYWAibAmuq7A/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

> 小提示：这里的传递 context 流程均是在 dubbo plugin 处理的，业务无感知，这个 plugin 是怎么实现的呢，下文会分析

##### traceId 如何保证全局唯一
要保证全局唯一 ，我们可以采用分布式或者本地生成的 ID，使用分布式话需要有一个发号器，每次请求都要先请求一下发号器，会有一次网络调用的开销，所以 SkyWalking 最终采用了本地生成 ID 的方式，它采用了大名鼎鼎的 snowflow 算法，性能很高。

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ3MCQflrg7RRUicDO8ibZx0R6CNQNLRrSOkTsLgFNLslevXH2tyibUBCOzQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

不过 snowflake 算法有一个众所周知的问题：**时间回拨**，这个问题可能会导致生成的 id 重复。那么 SkyWalking 是如何解决时间回拨问题的呢。

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ3rbmvTJAAH22YPwQatg9eicr28N9oSW5LJQH2jKyXYuQuVia0NudBnA7A/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

每生成一个 id，都会记录一下生成 id 的时间（lastTimestamp），如果发现当前时间比上一次生成 id 的时间（lastTimestamp）还小，那说明发生了时间回拨，此时会生成一个随机数来作为 traceId。

这里可能就有同学要较真了，可能会觉得生成的这个随机数也会和已生成的全局id重复，是否再加一层校验会好点。

这里要说一下系统设计上的方案取舍问题了，首先如果针对产生的这个随机数作唯一性校验无疑会多一层调用，会有一定的性能损耗，但其实时间回拨发生的概率很小（发生之后由于机器时间紊乱，业务会受到很大影响，所以机器时间的调整必然要慎之又慎），再加上生成的随机数重合的概率也很小，综合考虑这里确实没有必要再加一层全局惟一性校验。对于技术方案的选型，**一定要避免过度设计，过犹不及**。

##### 请求量这么多，全部采集会不会影响性能?

如果对每个请求调用都采集，那毫无疑问数据量会非常大，但反过来想一下，是否真的有必要对每个请求都采集呢，其实没有必要，我们可以设置采样频率，只采样部分数据，**SkyWalking 默认设置了 3 秒采样 3 次**，其余请求不采样,如图示

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ3dJ7mUh852aCpzFqaGYibDIqGprYxcwICcWODHgP5MfXpx9pODkQhogQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

这样的采样频率其实足够我们分析组件的性能了，按 3 秒采样 3 次这样的频率来采样数据会有啥问题呢。理想情况下，每个服务调用都在同一个时间点（如下图示）这样的话每次都在同一时间点采样确实没问题

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ3v1lldaF5rgvdsxicMGUJUaKNqoI7cwXEiaia6J84GGbXjQsjPQibv5ygow/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

但在生产上，每次服务调用基本不可能都在同一时间点调用，因为期间有网络调用延时等，实际调用情况很可能是下图这样

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ3AqOkcVuvLhHiar57CsaWz7hPGzT3If6wEGlDqdZ2DwNHZoUxicobeRiag/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

这样的话就会导致某些调用在服务 A 上被采样了，在服务 B，C 上不被采样，也就没法分析调用链的性能，那么 SkyWalking 是如何解决的呢。

它是这样解决的：**如果上游有携带 Context 过来（说明上游采样了），则下游强制采集数据。这样可以保证链路完整**。

### SkyWalking 的基础架构
SkyWalking 的基础如下架构，可以说几乎所有的的分布式调用都是由以下几个组件组成的

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ3js98qDzibpZDkrLmoodY3Z7ID96KhkcF4wFM5oscmYctsz25DCdh7eg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

首先当然是节点数据的定时采样，采样后将数据定时上报，将其存储到 ES, MySQL 等持久化层，有了数据自然而然可根据数据做可视化分析。

##### SkyWalking 的性能如何

接下来大家肯定比较关心 SkyWalking 的性能，那我们来看下官方的测评数据

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ31h3ricCDfTibZibSjvzibpRyia83anMKO2iaHWj3MJl9mg1jIZyyjWK9kZJg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

图中蓝色代表未使用 SkyWalking 的表现，橙色代表使用了 SkyWalking 的表现，以上是在 TPS 为 5000 的情况下测出的数据，可以看出，不论是 **CPU，内存，还是响应时间，使用 SkyWalking 带来的性能损耗几乎可以忽略不计**。

接下来我们再来看 SkyWalking 与另一款业界比较知名的分布式追踪工具 Zipkin, Pinpoint 的对比（在采样率为 1 秒 1 个，线程数 500，请求总数为 5000 的情况下做的对比）,可以看到在关键的响应时间上， Zipkin（117ms）,PinPoint（201ms）远逊色于 SkyWalking（22ms）!

![image](https://mmbiz.qpic.cn/mmbiz_png/1J6IbIcPCLawX2IVS5a3JWBj3YXYxPJ3ZMNp8jcToicxxBCAlIV7zI6AeD5aSicgrayM61DmPPsVSpvA84iatBqrg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

从性能损耗这个指标上看，SkyWalking 完胜！

再看下另一个指标：**对代码的侵入性如何**，ZipKin 是需要在应用程序中埋点的，对代码的侵入强，而 SkyWalking 采用 javaagent + 插件化这种修改字节码的方式可以做到对代码无任何侵入，除了性能和对代码的侵入性上 SkyWaking 表现不错外，它还有以下优势几个优势

- 对多语言的支持，组件丰富：目前其支持 Java, .Net Core, PHP, NodeJS, Golang, LUA 语言，组件上也支持dubbo, mysql 等常见组件，大部分能满足我们的需求。
- 扩展性：对于不满足的插件，我们按照 SkyWalking 的规则手动写一个即可，新实现的插件对代码无入侵。

[原文地址](https://mp.weixin.qq.com/s/MTk6WF161Ob8sBdpnboFgg)