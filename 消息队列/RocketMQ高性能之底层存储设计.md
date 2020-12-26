##### 说在前面
RocketMQ在底层存储设计上借鉴了Kafka，但是也有它独到的设计，本文主要关注深刻影响着RocketMQ性能的底层文件存储设计，中间会穿插一些Kafka的内容以作为对比。

##### （1） Case
要讲存储设计，我们先复习下RMQ中的文件及其作用。

Commit Log，一个文件集合，每个文件1G大小，存储满后存下一个，为了讨论方便可以把它当成一个文件，所有消息内容全部持久化到这个文件中；Consume Queue：一个Topic可以有多个，每一个文件代表一个逻辑队列，这里存放消息在Commit Log的偏移值以及大小和Tag属性。

假如集群有一个Broker，Topic为binlog的队列（Consume Queue）数量为4，如下图所示，按顺序发送这5条消息。
![image](https://upload-images.jianshu.io/upload_images/716353-f6231c8edded139a?imageMogr2/auto-orient/strip|imageView2/2/format/webp)

**先关注下Commit Log和Consume Queue**。

![image](https://upload-images.jianshu.io/upload_images/716353-f6e71ac2bff40019?imageMogr2/auto-orient/strip|imageView2/2/format/webp)

RMQ的消息整体是有序的，所以这5条消息按顺序将内容持久化在Commit Log中。Consume Queue则是用于将消息均衡地按序排列在不同的逻辑队列，集群模式下多个消费者就可以并行消费Consume Queue的消息。现在我们对RMQ的各个文件有个大概的印象了。

##### Page Cache
通常文件随机读写非常慢，但对文件进行顺序读写，速度几乎是接近于内存的随机读写，为什么会这么快，原因就是OS对文件IO有优化。OS发现系统的物理内存有大量剩余时，为了提高IO的性能，将一部分的内存用作Page Cache。

OS在读磁盘时会按照文件顺序预先将内容读到Cache中，以便下次读时能命中Cache，写磁盘时直接写到Cache中就写返回，由pdflush以某种策略将Cache的数据Flush回磁盘。

文件顺序IO时，读和写的区域都是被OS智能Cache过的热点区域，不会产生大量缺页（Page Fault）中断而再次读取磁盘，文件的IO几乎等同于内存的IO。
#####  刷盘
**刷盘分成：同步刷盘和异步刷盘**
![image](https://mmbiz.qpic.cn/mmbiz_png/fYn1DficteCHzvPZRogQ2bSZxiaXPG1Zictwn3nVBdYO7VsicXxUxibXYFynhJqj2yR5V3MaCFFumU0hlgoGw5q7rTg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

- **同步刷盘**：在消息真正落盘后，才返回成功给Producer，只要磁盘阵列RAID5完好，消息就不会丢。一般只用于金融场景，这种方式不是本文讨论的重点，因为没有充分利用Page Cache的特点。
- **异步刷盘**：读写文件充分利用了Page Cache，即写入Page Cache就返回成功给Producer。之后的内容全部以异步刷盘方式来讨论。
##### RMQ发送消息
例子和原理回顾完，从消息发送和消息接收来看RMQ中被mmap后的Commit Log和Consume Queue的IO情况。

发送时，Producer不直接与Consume Queue打交道。上文提到过，RMQ所有的消息都会存放在Commit Log中，为了使消息存储不发生混乱，多线程对Commit Log写会上锁。
![image](https://mmbiz.qpic.cn/mmbiz_png/fYn1DficteCHCsKIFkib3KdD5lKq4uZvliaDAAxF2KvHEWuLdU2AFGyv5VcicCs0rrFgfyuibpzgx2OUYybibiaBbGA2Q/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

消息持久被锁串行化后，对Commit Log就是**顺序写**，也就是常说的Append操作。配合上Page Cache，RMQ在写Commit Log时效率会非常高。正因为写Commit Log很快，RMQ也大胆提供了**自旋锁**以提高性能。

Commit Log持久后，Broker会将消息逐个异步Dispatch到对应的Consume Queue文件中。

![image](https://mmbiz.qpic.cn/mmbiz_png/fYn1DficteCHCsKIFkib3KdD5lKq4uZvliaPjbxXiaWIBKqyDzzH86PZIE2bAdcqzKUgHKO84kZPGZmft6MTnFO6MQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

每一个Consume Queue代表一个逻辑队列，是由ReputMessageService在单个Thread Loop中Append，如上图所示，每一个Consume Queue显然也是**从左往右高效顺序写**。

#####  RMQ消费消息
消费时，Consumer不直接与Commit Log打交道，而是从Consume Queue中去拉取数据

![image](https://mmbiz.qpic.cn/mmbiz_png/fYn1DficteCHCsKIFkib3KdD5lKq4uZvliaZnicuoMibanMvFHvChcOkq0jkia9qiaPibzH8bUPQuICLWkt6AlJriakYsJg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

如上图所示，拉取的顺序从旧到新，每一个Consume Queue都是顺序读。

光拉取Consume Queue是没有消息真实内容的，但是里面有对Commit Log的偏移值引用，所以再次映射到Commit Log获取真实消息数据。

![image](https://mmbiz.qpic.cn/mmbiz_png/fYn1DficteCHCsKIFkib3KdD5lKq4uZvliaakxJbl4AOuhbqNW60dho8k8fdcmqs7FgYqSicGAHhBPTHBaXWamWWEg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

问题出现了，从上图可以看到，**Commit Log会进行随机读**。

![image](https://mmbiz.qpic.cn/mmbiz_png/fYn1DficteCHCsKIFkib3KdD5lKq4uZvliafv9JI9Pss2VTbGDiaGiadgpoBcoNLSKCRXrDvbgUvWDlF51mFgMHibPgA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

虽然是随机读，但整体还是从旧到新有序读，只要随机的那块区域还在Page Cache的热点范围内，还是可以充分利用Page Cache。
![image](https://mmbiz.qpic.cn/mmbiz_png/fYn1DficteCHzvPZRogQ2bSZxiaXPG1ZictBBULUL6xzrexJbGkk1CuWRiaU7b8IJ6cLjibfKVdbvT5ia584JlXbNaKQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

在一台真实的MQ上查看网络和磁盘，即使消息端一直从MQ读取消息，也几乎看不到RMQ进程从磁盘read数据，数据直接从Page Cache经由Socket发送给了Consumer。

##### 对比Kafka
文章开头就说到，RMQ是借鉴了Kafka的想法，同时也打破了Kafka在底层存储的设计。

![image](https://mmbiz.qpic.cn/mmbiz_png/fYn1DficteCHzvPZRogQ2bSZxiaXPG1Zict0bfIqR3IC3UbL30ekomnnK2cl6Z9FVD3hvLAUOgjbVicx3t08hkrhKA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

Kafka中关于消息的存储只有一种文件，叫做Partition（同一个Partition的多个Segment按一个Partition讨论），它履行了RMQ中Commit Log和Consume Queue公共的职责，即它在逻辑上进行拆分存，以提高消费并行度，又在内部存储了真实的消息内容。

![image](https://mmbiz.qpic.cn/mmbiz_png/fYn1DficteCHE9wgtoXouZlZlHkNbxyWOuc2BRp4oJyjYFwu85UF6XqkTG4TefFcbbtf9rLhCRF6l1fGEmMca8A/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

这样看上去非常完美，不管对于Producer还是Consumer，单个Partition文件在正常的发送和消费逻辑中都是顺序IO，充分利用Page Cache带来的巨大性能提升，但是，万一Topic很多，每个Topic又分了N个Partition，这时对于OS来说，这么多文件的顺序读写在并发时变成了整体随机读写。

![image](https://mmbiz.qpic.cn/mmbiz_png/fYn1DficteCHE9wgtoXouZlZlHkNbxyWO4X2IwQy02LfAibaINWWm5IaOnEPYpmldpHaNt5oK9BoibfzkicRYPIegw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

突然想起了「打地鼠」这款游戏。对于每一个洞，我打的地鼠总是有顺序的，但是，万一有10000个洞，只有你一个人去打，无数只地鼠有先有后的出入于每个洞，这时终究还是随机去打，同学们脑补下这场景

#####  总结
不管是RMQ还是Kafka，基本的原理都是类似的

![image](https://mmbiz.qpic.cn/mmbiz_png/fYn1DficteCHzvPZRogQ2bSZxiaXPG1Zictsx4cJRQqWtNawOtTFEFSlGB3sQUIN79hwcNcrMkcPIUVH3sR8AMkMQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

总结下来就是，发送消息时，消息要写进Page Cache而不是直接写磁盘，依赖异步线程刷盘；接收消息时，消息从Page Cache直接获取而不是缺页从磁盘读取，并且Cache本身就由内核管理，不需要从程序到内核的数据Copy，直接通过Socket传输。