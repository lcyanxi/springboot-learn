-  **TCP 是一个可靠传输的协议，那它是如何保证可靠的呢？**

为了实现可靠性传输，需要考虑很多事情，例如数据的破坏、丢包、重复以及分片顺序混乱等问题。如不能解决这些问题，也就无从谈起可靠传输。

**TCP 是通过序列号、确认应答、重发控制、连接管理以及窗口控制等机制实现可靠性传输的**

TCP 的重传机制、滑动窗口、流量控制、拥塞控制
![image](https://mmbiz.qpic.cn/mmbiz_png/J0g14CUwaZeuicRMlA8rKvl5AVLibhibDhgTfasBzdn2sIB39aFcqL22zhAa7v9d9vR1oZF4mibLUKouDEfKjYoZww/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)
##### 重传机制
TCP 实现可靠传输的方式之一，是通过序列号(SYN)与确认应答(ACK)。

在 TCP 中，当发送端的数据到达接收主机时，接收端主机会返回一个确认应答消息，表示已收到消息。
![image](https://mmbiz.qpic.cn/mmbiz_png/J0g14CUwaZeuicRMlA8rKvl5AVLibhibDhg7rhicME5YSPXZCicwvDpdicChuibpe3AbuavGsDNz5ibPIicibF6wiaJHlKlFQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

但在错综复杂的网络，并不一定能如上图那么顺利能正常的数据传输，万一数据在传输过程中丢失了呢？

所以 TCP 针对数据包丢失的情况，会用**重传机制**解决。接下来说说常见的重传机制：
- 超时重传
- 快速重传
- SACK（选择性确认）
- D-SACK（收到重复包）

###### 超时重传
重传机制的其中一个方式，就是在发送数据时，设定一个定时器，当超过指定的时间后，没有收到对方的 ACK 确认应答报文，就会重发该数据，也就是我们常说的超时重传。

TCP 会在以下两种情况发生超时重传：
- 数据包丢失
- 确认应答丢失
![image](https://mmbiz.qpic.cn/mmbiz_png/J0g14CUwaZeuicRMlA8rKvl5AVLibhibDhgObdlvOJianvD0oj586rMc8wVs4hlzUtgRibWfD0WBpAJhRtHxOPd9ibibQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

**超时时间应该设置为多少呢？**

我们先来了解一下什么是 ==**RTT**==（Round-Trip Time 往返时延），从下图我们就可以知道：
![image](https://mmbiz.qpic.cn/mmbiz_png/J0g14CUwaZeuicRMlA8rKvl5AVLibhibDhg0gQic4pOyb48icGE2bMfSup2icU0ZAb1SWsEyCibHND1x7V3icliaWeuxwQg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

RTT 就是数据从网络一端传送到另一端所需的时间，也就是包的往返时间。

超时重传时间是以 **RTO** （Retransmission Timeout **超时重传时间**）表示。

假设在重传的情况下，超时时间RTO「较长或较短」时，会发生什么事情呢？
![image](https://mmbiz.qpic.cn/mmbiz_png/J0g14CUwaZeuicRMlA8rKvl5AVLibhibDhgyx80wY6Zsy2xW0U5egNib7icspUPsE9PKYq5WLaoJGpkTIoZ1c5gIjCw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

上图中有两种超时时间不同的情况：
- 当超时时间 RTO较大时，重发就慢，丢了老半天才重发，没有效率，性能差；
- 当超时时间 RTO较小时，会导致可能并没有丢就重发，于是重发的就快，会增加网络拥塞，导致更多的超时，更多的超时导致更多的重发。

精确的测量超时时间 RTO 的值是非常重要的，这可让我们的重传机制更高效。

根据上述的两种情况，我们可以得知，**超时重传时间 RTO 的值应该略大于报文往返  RTT 的值**
![image]( https://mmbiz.qpic.cn/mmbiz_png/J0g14CUwaZeuicRMlA8rKvl5AVLibhibDhgMhribarYibble1eTHicyWZutjSxeRMFPE3WZNglbNTplPyTDuC0u6lZbg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

至此，可能大家觉得超时重传时间RTO的值计算，也不是很复杂嘛。

好像就是在发送端发包时记下t0，然后接收端再把这个 ack 回来时再记一个 t1，于是 RTT = t1 – t0。没那么简单，这只是一个采样，不能代表普遍情况。

实际上「报文往返RTT的值」是经常变化的，因为我们的网络也是时常变化的。也就因为「报文往返 RTT 的值」 是经常波动变化的，所以「超时重传时间 RTO 的值」应该是一个动态变化的值。

**我们来看看 Linux 是如何计算 RTO 的呢？**

如果超时重发的数据，再次超时的时候，又需要重传的时候，TCP 的策略是**超时间隔加倍**。

也就是每当遇到一次超时重传的时候，都会将下一次超时时间间隔设为先前值的两倍。两次超时，就说明网络环境差，不宜频繁反复发送。

超时触发重传存在的问题是，超时周期可能相对较长。那是不是可以有更快的方式呢？

于是就可以用「快速重传」机制来解决超时重发的时间等待。

###### 快速重传
TCP 还有另外一种快速重传（Fast Retransmit）机制，它不以时间为驱动，而是以数据驱动重传。

快速重传机制，是如何工作的呢？其实很简单，一图胜千言。
![image](https://mmbiz.qpic.cn/mmbiz_png/J0g14CUwaZeuicRMlA8rKvl5AVLibhibDhggRhxrfiaRIsia0z3T4l7TxVM7J9vjFktFRKHkdva953UY6vFIpsYsOicQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

在上图，发送方发出了 1，2，3，4，5 份数据：
- 第一份 Seq1 先送到了，于是就 Ack 回 2；
- 结果 Seq2 因为某些原因没收到，Seq3 到达了，于是还是 Ack 回 2；
- 后面的 Seq4 和 Seq5 都到了，但还是 Ack 回 2，因为 Seq2 还是没有收到；
- 发送端收到了三个 Ack = 2 的确认，知道了 Seq2 还没有收到，就会在定时器过期之前，重传丢失的 Seq2。
- 最后，接收到收到了 Seq2，此时因为 Seq3，Seq4，Seq5 都收到了，于是 Ack 回 6 。

所以，快速重传的工作方式是当收到三个相同的 ACK 报文时，会在定时器过期之前，重传丢失的报文段

快速重传机制只解决了一个问题，就是超时时间的问题，但是它依然面临着另外一个问题。**就是重传的时候，是重传之前的一个，还是重传所有的问题**。

比如对于上面的例子，是重传 Seq2 呢？还是重传 Seq2、Seq3、Seq4、Seq5 呢？因为发送端并不清楚这连续的三个 Ack 2 是谁传回来的。

根据 TCP 不同的实现，以上两种情况都是有可能的。可见，这是一把双刃剑。

为了解决不知道该重传哪些 TCP 报文，于是就有**SACK** 方法。
###### SACK方法(选择性确认)
还有一种实现重传机制的方式叫：SACK（ Selective Acknowledgment 选择性确认）。

这种方式需要在 TCP 头部「选项」字段里加一个 SACK 的东西，它可以将缓存的地图发送给发送方，这样发送方就可以知道哪些数据收到了，哪些数据没收到，知道了这些信息，就可以只重传丢失的数据。

如下图，发送方收到了三次同样的 ACK 确认报文，于是就会触发快速重发机制，通过 SACK 信息发现只有 200~299 这段数据丢失，则重发时，就只选择了这个 TCP 段进行重复。
![image](https://mmbiz.qpic.cn/mmbiz_png/J0g14CUwaZeuicRMlA8rKvl5AVLibhibDhg3RbEZItSOQY1TadJpUTuibIDziaibALaEmk7JO0Ill7iaeiaGI94Wyia0NXA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

如果要支持 SACK，必须双方都要支持。在 Linux 下，可以通过 net.ipv4.tcp_sack 参数打开这个功能（Linux 2.4 后默认打开）。
###### Duplicate SACK（重复接收）
Duplicate SACK 又称 D-SACK，**其主要使用了 SACK 来告诉「发送方」有哪些数据被重复接收了**。

下面举例两个栗子，来说明 D-SACK 的作用。

栗子一号：ACK 丢包
![image](https://mmbiz.qpic.cn/mmbiz_png/J0g14CUwaZeuicRMlA8rKvl5AVLibhibDhgYe99dD53qcV16GAJGCeaw1fuqhvhBC1mVs4kETqHlP3flTByXia9ILQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

- 「接收方」发给「发送方」的两个 ACK 确认应答都丢失了，所以发送方超时后，重传第一个数据包（3000 ~ 3499）
- 于是「接收方」发现数据是重复收到的，于是回了一个 SACK = 3000~3500，告诉「发送方」 3000~3500 的数据早已被接收了，因为 ACK 都到了 4000 了，已经意味着 4000 之前的所有数据都已收到，所以这个 SACK 就代表着 D-SACK。
- 这样「发送方」就知道了，数据没有丢，是「接收方」的 ACK 确认报文丢了。

栗子二号：网络延时
![image](https://mmbiz.qpic.cn/mmbiz_png/J0g14CUwaZeuicRMlA8rKvl5AVLibhibDhgGSKaMMbmPiaUQmCvR4cz2kQ5OV8SqaPIwdkZ7T19uEs5WxCc6h66x7w/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

- 数据包（1000~1499） 被网络延迟了，导致「发送方」没有收到 Ack 1500 的确认报文。
- 而后面报文到达的三个相同的 ACK 确认报文，就触发了快速重传机制，但是在重传后，被延迟的数据包（1000~1499）又到了「接收方」；
- **所以「接收方」回了一个 SACK=1000~1500，因为 ACK 已经到了 3000，所以这个 SACK 是 D-SACK，表示收到了重复的包**。
- 这样发送方就知道快速重传触发的原因不是发出去的包丢了，也不是因为回应的 ACK 包丢了，而是因为网络延迟了。

可见，D-SACK：
**可以让「发送方」知道，是发出去的包丢了，还是接收方回应的 ACK 包丢了;**

##### 滑动窗口
> **引入窗口概念的原因？**

我们都知道 TCP是每发送一个数据，都要进行一次确认应答。当上一个数据包收到了应答了，再发送下一个。

这个模式就有点像我和你面对面聊天，你一句我一句。但这种方式的缺点是效率比较低的。

如果你说完一句话，我在处理其他事情，没有及时回复你，那你不是要干等着我做完其他事情后，我回复你，你才能说下一句话，很显然这不现实。

![image](https://mmbiz.qpic.cn/mmbiz_png/J0g14CUwaZeuicRMlA8rKvl5AVLibhibDhgMiaZvYzVsKCCVTHJZw1GR6cI49iaw7XVQCNhomJJibSc0icN5mnA7Zvkog/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

所以，这样的传输方式有一个缺点：**数据包的往返时间越长，通信的效率就越低。**

为解决这个问题，TCP引入了窗口这个概念。即使在往返时间较长的情况下，它也不会降低网络通信的效率。

那么有了窗口，就可以指定窗口大小，**窗口大小就是指无需等待确认应答，而可以继续发送数据的最大**值。

窗口的实现实际上是操作系统开辟的一个缓存空间，发送方主机在等到确认应答返回之前，必须在缓冲区中保留已发送的数据。如果按期收到确认应答，此时数据就可以从缓存区清除。

假设窗口大小为 3 个 TCP 段，那么发送方就可以「连续发送」 3 个 TCP 段，并且中途若有 ACK 丢失，可以通过「下一个确认应答进行确认」。如下图：
![image](https://mmbiz.qpic.cn/mmbiz_png/J0g14CUwaZeuicRMlA8rKvl5AVLibhibDhgz9LqxXsGESpzbick5b29No4rkqEubmVxexM2pM5DASr53mk65Qx5icZA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

图中的 ACK 600确认应答报文丢失，也没关系，因为可以通话下一个确认应答进行确认，只要发送方收到了 ACK 700 确认应答，就意味着 700 之前的所有数据「接收方」都收到了。这个模式就叫**累计确认**或者**累计应答**。

> **窗口大小由哪一方决定？**

**这个字段是接收端告诉发送端自己还有多少缓冲区可以接收数据。于是发送端就可以根据这个接收端的处理能力来发送数据，而不会导致接收端处理不过来**。

所以，通常窗口的大小是由接收方的决定的。

发送方发送的数据大小不能超过接收方的窗口大小，否则接收方就无法正常接收到数据。

> **接收窗口和发送窗口的大小是相等的吗？**

并不是完全相等，接收窗口的大小是约等于发送窗口的大小的
##### 流量控制
发送方不能无脑的发数据给接收方，要考虑接收方处理能力。

如果一直无脑的发数据给对方，但对方处理不过来，那么就会导致触发重发机制，从而导致网络流量的无端的浪费。

为了解决这种现象发生，TCP提供一种机制**可以让「发送方」根据「接收方」的实际接收能力控制发送的数据量，这就是所谓的流量控制**

##### 拥塞控制
**为什么要有拥塞控制呀，不是有流量控制了吗？**

前面的流量控制是避免「发送方」的数据填满「接收方」的缓存，但是并不知道网络的中发生了什么。

一般来说，计算机网络都处在一个共享的环境。因此也有可能会因为其他主机之间的通信使得网络拥堵。

**在网络出现拥堵时，如果继续发送大量数据包，可能会导致数据包时延、丢失等，这时TCP就会重传数据，但是一重传就会导致网络的负担更重，于是会导致更大的延迟以及更多的丢包，这个情况就会进入恶性循环被不断地放大….**

所以，TCP 不能忽略网络上发生的事，它被设计成一个无私的协议，当网络发送拥塞时，TCP会自我牺牲，降低发送的数据量。

于是，就有了拥塞控制，控制的**目的就是避免「发送方」的数据填满整个网络。**

为了在「发送方」调节所要发送数据的量，定义了一个叫做「**拥塞窗口**」的概念。

> **什么是拥塞窗口？和发送窗口有什么关系呢？**

**拥塞窗口**cwnd是发送方维护的一个的状态变量，它会根据**网络的拥塞程度动态变化的**。

我们在前面提到过发送窗口 swnd 和接收窗口 rwnd 是约等于的关系，那么由于入了拥塞窗口的概念后，此时发送窗口的值是swnd = min(cwnd, rwnd)，也就是拥塞窗口和接收窗口中的最小值。

拥塞窗口 cwnd 变化的规则：
- 只要网络中没有出现拥塞，cwnd 就会增大；
- 但网络中出现了拥塞，cwnd 就减少；

>**那么怎么知道当前网络是否出现了拥塞呢？**

其实只要「发送方」没有在规定时间内接收到 ACK 应答报文，也就是**发生了超时重传，就会认为网络出现了用拥塞**。

> **拥塞控制有哪些控制算法**？

拥塞控制主要是四个算法：
- 慢启动
- 拥塞避免
- 拥塞发生
- 快速恢复

###### 慢启动
TCP 在刚建立连接完成后，首先是有个慢启动的过程，这个慢启动的意思就是一点一点的提高发送数据包的数量，如果一上来就发大量的数据，这不是给网络添堵吗？

慢启动的算法记住一个规则就行：**当发送方每收到一个 ACK，就拥塞窗口 cwnd 的大小就会加 1**。

这里假定拥塞窗口cwnd和发送窗口swnd相等，下面举个栗子
![image](https://mmbiz.qpic.cn/mmbiz_png/J0g14CUwaZeuicRMlA8rKvl5AVLibhibDhgDicBugvroe9EtiaFU38hk4JuVfDciauVPfecBNp8TPI1zkoqbibePA4dlg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

###### 拥塞避免算法
前面说道，当拥塞窗口 cwnd 「超过」慢启动门限 ssthresh 就会进入拥塞避免算法。

一般来说 ssthresh(**慢启动门限值**)的大小是**65535** 字节。

那么进入拥塞避免算法后，它的规则是：**每当收到一个 ACK 时，cwnd 增加 1/cwnd。**

接上前面的慢启动的栗子，现假定 ssthresh 为 8：
- 当 8 个 ACK 应答确认到来时，每个确认增加 1/8，8 个 ACK确认cwnd一共增加1，于是这一次能够发送 9 个 MSS 大小的数据，变成了**线性增长**。


所以，我们可以发现，**拥塞避免算法就是将原本慢启动算法的指数增长变成了线性增长**，还是增长阶段，但是增长速度缓慢了一些。

就这么一直增长着后，网络就会慢慢进入了拥塞的状况了，于是就会出现丢包现象，这时就需要对丢失的数据包进行重传。

当触发了重传机制，也就进入了「**拥塞发生算法**」。

###### 拥塞发生
当网络出现拥塞，也就是会发生数据包重传，重传机制主要有两种：
- 超时重传
- 快速重传

> **发生超时重传的拥塞发生算法**

当发生了「**超时重传**」，则就会使用拥塞发生算法。

这个时候，sshresh 和 cwnd 的值会发生变化：
- ssthresh 设为 cwnd/2，
- cwnd 重置为 1

> **发生快速重传的拥塞发生算法**

还有更好的方式，前面我们讲过「快速重传算法」。当接收方发现丢了一个中间包的时候，发送三次前一个包的 ACK，于是发送端就会快速地重传，不必等待超时再重传。

TCP 认为这种情况不严重，因为大部分没丢，只丢了一小部分，则 ssthresh 和 cwnd 变化如下：
- cwnd = cwnd/2 ，也就是设置为原来的一半;
- ssthresh = cwnd;
- 进入快速恢复算法

###### 进入快速恢复算法
进入快速恢复算法如下：
- 拥塞窗口 cwnd = ssthresh + 3 （ 3 的意思是确认有 3 个数据包被收到了）
- 重传丢失的数据包
- 如果再收到重复的 ACK，那么 cwnd 增加 1
- 如果收到新数据的 ACK 后，设置 cwnd 为 ssthresh，接着就进入了拥塞避免算法
![image](https://mmbiz.qpic.cn/mmbiz_png/J0g14CUwaZeuicRMlA8rKvl5AVLibhibDhgR3w50EdpWF95ZM6QPpELCF3P1niazia8nBrSQUvX7e7F7LXMiaXR3iayUA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

[参考博客](https://mp.weixin.qq.com/s?__biz=MzAwNDA2OTM1Ng==&mid=2453143215&idx=2&sn=e9e767ebcbd2fce4688ba71db4fbd32d&scene=21#wechat_redirect)