**说在前面**

Apache RocketMQ-4.3.0正式Release了事务消息的特性，顺着最近的这个热点。第一篇文章，就来聊一下在软件工程学上的长久的难题——分布式事务（Distributed Transaction）。

这个技术也在各个诸如阿里，腾讯等大厂的内部，被广泛地实现，利用及优化。但是由于理论上就有难点，所以分布式事务就隐晦得成了大厂对于小厂的技术壁垒。相信来看这篇文章的同学，一定都听过很多关于分布式事务的术语，比较==二阶段提交，TCC，最终一致性==等，所以这里也不多普及概念。

**基于RocketMQ的分布式事务**

我们直接上正题，利用RocketMQ设计自己的分布式事务组件。

举个虚拟场景引出问题

用户从农行转账100元去招行 ，农行的系统和招行的系统分别部署在自己的机房，系统之间通过消息进行通信，防止过度耦合。

整个模型可以不恰当得描述为：农行扣了100元后，发送“已经扣款”的消息给招行，招行收到消息，知道农行扣款成功了，然后在招行账户上加100元。

问题是，农行这边，方案1. 先扣100元再发消息，方案2. 先发消息再扣100元

整理下整个事务不一致的场景：

方案1，

农行扣100后成功，但是消息发送失败，招行没有加100

方案2，

消息发送成功，但是农行扣100元失败，招行收到消息加了100

各位同学应该已经发现问题所在了，扣款和发送消息这两个事情，没有办法通过调换顺序实现「同时成功」，或者「同时失败」。如果前者成功，后者失败，就会造成不一致。

RocketMQ，以下简称RMQ，为了实现事务消息引入了一种新的消息类型：TransactionMsg

一个完整的事务消息分成两个部分：

HalfMsg(Prepare) + Commit/RollbackMsg

Producer发送了HalfMsg后，由于HalfMsg不是一个完整的事务消息，Consumer无法立刻就消费到该消息，Producer可以对HalfMsg进行Commit或者Rollback来终结事务（EndTransacaction）。只有当Commit了HalfMsg后，Consumer才能消费到这条消息。RMQ会定期去向Producer询问，是否可以Commit或者Rollback那些由于错误没有被终结的HalfMsg来结束它们的生命周期，以达成事务最终的一致。

依然是刚刚的转账场景，我们用RMQ事务消息来优化下流程：

1. 农行向RMQ同步发送HalfMsg，消息中携带农行即将要扣100元的信息
2. 农行HalfMsg成功发送后，执行数据库本地事务，在自己的系统中扣100元
3. 农行查看本地事务执行情况
4. 本地事务返回成功，农行向RMQ提交（Commit）HalfMsg
5. 招行系统订阅了RMQ，顺利收到农行已经扣款100元的信息
6. 招行系统执行本地事务，在招行的系统中加100元

![image](https://upload-images.jianshu.io/upload_images/716353-c69f28b826c3ef7e?imageMogr2/auto-orient/strip|imageView2/2/w/1080/format/webp)


同样得，我们逐个来分析下这个流程是不是会出现不一致：

农行发送HalfMsg是同步发送（Sync），如果HalfMsg发送不成功，压根就不会执行本地事务

发送HalfMsg成功，但是农行扣款****本地事务失败，也没事，如果本地事务没有成功，立刻就发送Rollback去回滚HalfMsg。就当之前啥事都没有发生过

农行本地事务成功了，但是Commit却失败了，但是由于HalfMsg已经在RMQ中，RMQ就能通过定时程序让农行重新检测本地事务是否成功，重新Commit。Rollback失败了也是同理

招行消费了消息后，加钱本地事务失败了，但是招行收到的消息持久化在MQ，甚至可以持久化在招行数据库，可以进行事务重试

刚刚讨论的案例是非常理想化的，整个分布式事务中，只涉及到了金额的变化，但是，真正的线上系统，作为消息发送方的本地事务可能就非常复杂，可能涉及到了几十张不同的表，那RMQ用定时器来Check HalfMsg，难道去查下涉及该事务的每一张表的数据是否提交成功？显然这种方案非常业务侵入非常大，并且很难组件化。所以需要在本地事务中设计一张Transaction表，将业务表和Transaction绑定在同一个本地事务中，如果农行的扣款本地事务成功时，Transaction中应当已经记录该TransactionId的状态为「已完成」。当最后需要检查时，只需要检查对应的TransactionId的状态是否是「已完成」就好，而不用关心具体的业务数据。

再谈一个小细节，

细心的同学可能发现，刚刚No.3的讨论其实是有点不严谨的，RMQ在调用Commit或者Rollback时，用的是Oneway的方式，熟悉RMQ源码的话，知道这种网络调用是只单向发送Request，不会去获取Response。消息发送性能上是有非常大的提升的，但是如果真的发送失败，Producer是不会知晓的，最后只能通过定时检查HalfMsg才能终结事务。

```
public void endTransactionOneway(
        final String addr,
        final EndTransactionRequestHeader requestHeader,
        final String remark,
        final long timeoutMillis
    ) throws RemotingException, MQBrokerException, InterruptedException {
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.END_TRANSACTION, requestHeader);

        request.setRemark(remark);
        // 使用Oneway发送end transaction类型的
        this.remotingClient.invokeOneway(addr, request, timeoutMillis);
    }
```

**RMQ 事务消息实现细节**
![image](https://note.youdao.com/yws/res/19769/WEBRESOURCEb34ea67437cc0d6d318b4f22dd95aeb1)

1. 事务生产者调用事务消息发送接口，发送消息
   开始预提交阶段，客户端发送预消息并在请求头标记这是一条事务消息。消息体就是我们实际要发送的消息内容
1. broker 接收到消息，发现这是一条事务消息，于是将当前消息备份。所谓“备份”即将当前消息的所有数据写入内部的事务 topic 中而不是我们实际要发送的 topic，该事务 topic 由于消费端并没有订阅，所以这条消息对消费端不可见，然后响应客户端的发送请求
1. 客户端确认发送成功，则执行本地事务，并标记事务执行状态。如果发送失败，就不需要执行本地事务了，直接标记事务执行失败，需要回滚。
1. 基于事务的执行状态，给本次发送事务消息的那个broker发送一条结束事务的请求（请求头里包含是提交还是回滚，亦或者是未知状态）
1. broker 收到事务结束的请求，如果是未知状态就打条日志直接返回了；如果是提交事务，就将备份的那条事务**消息恢复过来，写入到原始的topic里**，此时就对消费端可见了，**然后要在op队列里（另一个内部topic）写入一条消息**，消息体就是当前这条事务消息的队列偏移值。如果是回滚事务，就只用在op队列里写入一条消息即可，就不还原事务消息了，这样对消费端就不可见。关于 op 队列的具体作用，后面源码部分再详说。
1. 说一下事务回查。事务回查就是 broker 扫描到那些没有提交也没回滚的消息，找到客户端，发一个请求，让客户端再次提交一下事务结束状态。

**事务回查**
broker 默认每分钟检查一次，从内部事务 topic 队列和 op 队列里拉取消息，然后比对，当前的事务半消息是否已经处理过了，是否需要回查

其实这里涉及到几个关键问题需要明白：

- 写入到事务 topic 里的事务半消息在事务结束后进行删除，但是 rocketmq 是**追加写**的方式，**所以这里的删除并不是从消息队列里真正的删除一条消息**。
- broker 怎么知道一条事务半消息是否已经提交或者回滚了，正如前面说的，这里引入一个 op 队列，即另一个内部 topic，如果一条消息已经提交或回滚了，就向 op 队列里写入一条消息，消息体就是在事务 topic 队列里的偏移值，如果 op 队列里没有，那就说明这条事务消息的状态还没有提交，还是未知的，可能需要事务回查。
- 我们知道写入到事务 topic 的事务半消息也如普通消息一样，是顺序写顺序读的，如果此时已经写入1、2、3、4、5、6共6条事务消息了，1、2、5的事务状态已经提交或者回滚了，但是 3、4 还是未知的，那总不能再重新回头消费吧。并没有，**如果 broker 发现这条消息是未知状态的，那在处理的时候，把这条消息再追回写入到事务 topic 的队列里，然后找客户端回查**。继续下一条消息处理，等到再处理到刚才重新追加的这条事务消息的时候，再从 op 队列里检查一下，这条事务半消息是否已经处理过了，如果还没有而且也没达到事务回查的最大次数，那就再追回写回去，再继续呗。如果已经达到最大次数，**就丢弃（其实是写到另一个内部topic，也就是说事务消息这里用到了3个内部topic来存储数据）**


**脱离RocketMQ的分布式事务**

不是所有的MQ都能支持事务消息，如何使用一般的MQ来搭建分布式事务组件，甚至抽象成一个事务SOA服务？

其实仔细分析下RMQ的事务消息，我们可以把它拆解成两个部分：

事务管理器 + 消息

所谓的事务管理器，就是对于事务的预备（Prepare）、提交（Commit）和回滚（Rollback）的管理，另外还包含预备事务的定时检查器。

消息，指的就是一般的同步消息，发送后能明确得到发送结果，用于事务系统与业务系统解耦。几乎所有的分布式MQ都是支持这种消息的。

我们来设计下自己的DistributedTransaction SOA，以下简称DT-SOA

![image](https://upload-images.jianshu.io/upload_images/716353-41c51f7f826283c0?imageMogr2/auto-orient/strip|imageView2/2/w/1080/format/webp)



流程还是没有变，但分布式事务不再强依赖RMQ，而是用一般的MQ代替：

1. 系统A发送事务，首先调用DT-SOA的Prepare方法准备开启事务，由于是同步调用，获取SendResult，如果发送成功，拿到全局分布式事务的ID——TID
2. 系统A用获取到的TID执行本地事务，本地事务中包含Transaction状态表，成功后将TID对应的状态置为“已完成”
3. 系统A调用DT-SOA提交事务，DT-SOA用MQ发送同步消息给系统B
4. 系统B监听对应Topic，接收到消息后，执行对应的本地事务
