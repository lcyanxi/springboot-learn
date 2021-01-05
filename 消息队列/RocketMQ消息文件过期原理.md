所有的消费均是客户端发起Pull请求的，告诉消息的offset位置，broker去查询并返回。但是有一点需要非常明确的是，消息消费后，**消息其实并没有物理地被清除**，这是一个非常特殊的设计。本文来探索此设计的一些细节。

##### 消费完后的消息去哪里了？

消息的存储是一直存在于**CommitLog**中的，由于CommitLog是以文件为单位（而非消息）存在的，而且CommitLog的设计是只允许顺序写，且每个消息大小不定长，所以这决定了消息文件几乎不可能按照消息为单位删除（否则性能会极具下降，逻辑也非常复杂）。

所以消息被消费了，消息所占据的物理空间也不会立刻被回收。但消息既然一直没有删除，那RocketMQ怎么知道应该投递过的消息就不再投递？——答案是**客户端自身维护——客户端拉取完消息之后**，在响应体中，broker会返回下一次应该拉取的位置，PushConsumer通过这一个位置，更新自己下一次的pull请求。这样就保证了正常情况下，消息只会被投递一次。

##### 什么时候清理物理消息文件？
那消息文件到底删不删，什么时候删？

消息存储在CommitLog之后，的确是会被清理的，但是这个清理只会在以下任一条件成立才会批量删除消息文件（CommitLog）：
- 消息文件过期（默认72小时），且到达清理时点（默认是凌晨4点），删除过期文件。
- 消息文件过期（默认72小时），且磁盘空间达到了水位线（默认75%），删除过期文件。
- 磁盘已经达到必须释放的上限（85%水位线）的时候，则开始批量清理文件（无论是否过期），直到空间充足。
> 注：若磁盘空间达到危险水位线（默认90%），出于保护自身的目的，broker会拒绝写入服务。

##### 这样设计带来的好处
消息的物理文件一直存在，消费逻辑只是听客户端的决定而搜索出对应消息进行，这样做，笔者认为，有以下几个好处：
- 一个消息很可能需要被N个消费组（设计上很可能就是系统）消费，但消息只需要存储一份，消费进度单独记录即可。这给强大的消息堆积能力提供了很好的支持——一个消息无需复制N份，就可服务N个消费组。
- 由于消费从哪里消费的决定权一直都是客户端决定，所以只要消息还在，就可以消费到，这使得RocketMQ可以支持其他传统消息中间件不支持的回溯消费。即我可以通过设置消费进度回溯，就可以让我的消费组重新像放快照一样消费历史消息；或者我需要另一个系统也复制历史的数据，只需要另起一个消费组从头消费即可（前提是消息文件还存在）。
- 消息索引服务。只要消息还存在就能被搜索出来。所以可以依靠消息的索引搜索出消息的各种原信息，方便事后排查问题。

> 注：在消息清理的时候，由于消息文件默认是1GB，所以在清理的时候其实是在删除一个大文件操作，这对于IO的压力是非常大的，这时候如果有消息写入，写入的耗时会明显变高。这个现象可以在凌晨4点（默认删时间时点）后的附近观察得到。

RocketMQ官方建议Linux下文件系统改为Ext4，对于文件删除操作，相比Ext3有非常明显的提升。

##### 跳过历史消息的处理
由于消息本身是没有过期的概念，只有文件才有过期的概念。那么对于很多业务场景——一个消息如果太老，是无需要被消费的，是不合适的。

这种需要跳过历史消息的场景，在RocketMQ要怎么实现呢？

**对于一个全新的消费组**，**PushConsumer默认就是跳过以前的消息而从最尾开始消费的**，解析请参看 https://zhuanlan.zhihu.com/p/25265380 相关章节。

但对于**已存在的消费组（老消费者组**），RocketMQ没有内置的实现，但有以下手段可以解决：
1. 自身的消费代码按照日期过滤，太老的消息直接过滤。如：

```
@Override
public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
  for(MessageExt msg: msgs){
    if(System.currentTimeMillis()-msg.getBornTimestamp()>60*1000) {//一分钟之前的认为过期
      continue;//过期消息跳过
    }
    //do consume here
    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
  }
}
```
2. 自身的消费代码代码判断消息的offset和MAX_OFFSET相差很远，认为是积压了很多，直接return CONSUME_SUCCESS过滤。

```
@Override
public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
  long offset = msgs.get(0).getQueueOffset();
  String maxOffset = msgs.get(0).getProperty(MessageConst.PROPERTY_MAX_OFFSET);
  long diff = Long. parseLong(maxOffset) - offset;
  if (diff > 100000) { //消息堆积了10W情况的特殊处理
      return ConsumeConcurrentlyStatus. CONSUME_SUCCESS;
  }
  //do consume here
  return ConsumeConcurrentlyStatus. CONSUME_SUCCESS;
}
```
3. 消费者启动前，先调整该消费组的消费进度，再开始消费。可以人工使用控制台命令resetOffsetByTime把消费进度调整到后面，再启动消费。
4. 原理同3，但使用代码来控制。代码中调用内部的运维接口，具体代码实例祥见ResetOffsetByTimeCommand.java. 