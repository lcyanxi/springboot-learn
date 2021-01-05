RocketMQ broker服务端以组为单位提供服务的，拥有着一样的brokerName则认为是一个组。其中brokerId=0的就是master，大于0的则为slave。

##### 消息同步策略
**master和slave都可以提供读服务，但是只有master允许做写入操作**，slave仅从master同步数据并不断上报自己的同步进度（slave自己的物理max offset）。

在Broker配置中，如果是有三个可选的**brokerRole：ASYNC_MASTER、SYNC_MASTER、SLAVE**。也就是说Master其实是分两种，决定其不同消息同步方式。

- **ASYNC_MASTER**：是异步复制的方式，生存者写入消息到Master后无需等待消息复制到slave即可返回，消息的复制由旁路线程进行异步复制。

- **SYNC_MASTER**：是同步复制的方式，表现出来的是类似同步双写的策略。即Master写入完消息之后，需要等待Slave的复制成功。注，这里只需要有一个Slave复制成功并成功应答即算成功，所以在这种模式下，如果有3个Slave，当生产者获得SEND_OK的应答时，代表消息已经达到Maser和一个Slave（注：这里并不代表已经持久化到磁盘，而只能证明肯定到了PageCache，是否能刷到磁盘取决于刷盘策略是同步刷盘还是异步刷盘），而还有两个Slave实际上是无法保证的，并且这里也不支持配置，即不支持如“”同步半数以上”之类的设置。

如果选择了SYNC_MASTER的方式，那么消息发送的状态除了SEND_OK外，还会多出以下的状态：
- **FLUSH_SLAVE_TIMEOUT**：同步到slave等待超时，即一直等Slave上报同步的进度，但过了超时时间都没有成功没有同步完。
- **SLAVE_NOT_AVAILABLE**：当前没有可用的Slave。

> 注：如果slave落后master实在太多，那个slave也会认为是暂时不可用的slave，直到它同步到接近的范围为止，这个不可用的阈值由broker配置haSlaveFallbehindMax（默认是1024 * 1024 * 256）决定。

相关源码如下：

```
public boolean isSlaveOK(final long masterPutWhere) {
    boolean result = this.connectionCount.get() > 0;
    result = result
            && ((masterPutWhere - this.push2SlaveMaxOffset.get()) < this.defaultMessageStore
            .getMessageStoreConfig().getHaSlaveFallbehindMax());
    return result;
}
```
**殊途同归**

虽然看似两个完全不一样的同步策略，但实际上从实现上，**在消息同步的实现上两者没有任何区别**。无论是SYNC_MASTER还是ASYNC_MASTER，实际上同步数据都是异步的由Slave不断拉取Master的最新数据进行的，之所以看起来SYNC_MASTER能做到同步复制，**实际上是在写入的最后一步做了一个等待操作执行了方法handleHA。而这个方法只对SYNC_MASTER起作用**。


```
public void handleHA(AppendMessageResult result, PutMessageResult putMessageResult, MessageExt messageExt) {
    if (BrokerRole.SYNC_MASTER == this.defaultMessageStore.getMessageStoreConfig().getBrokerRole()) {
        HAService service = this.defaultMessageStore.getHaService();
        if (messageExt.isWaitStoreMsgOK()) {
            // Determine whether to wait
            if (service.isSlaveOK(result.getWroteOffset() + result.getWroteBytes())) {
                GroupCommitRequest request = new GroupCommitRequest(result.getWroteOffset() + result.getWroteBytes());
                service.putRequest(request);
                service.getWaitNotifyObject().wakeupAll();
                boolean flushOK =
                    request.waitForFlush(this.defaultMessageStore.getMessageStoreConfig().getSyncFlushTimeout());
                if (!flushOK) {
                    log.error("do sync transfer other node, wait return, but failed, topic: " + messageExt.getTopic() + " tags: "
                        + messageExt.getTags() + " client address: " + messageExt.getBornHostNameString());
                    putMessageResult.setPutMessageStatus(PutMessageStatus.FLUSH_SLAVE_TIMEOUT);
                }
            }
            // Slave problem
            else {
                // Tell the producer, slave not available
                putMessageResult.setPutMessageStatus(PutMessageStatus.SLAVE_NOT_AVAILABLE);
            }
        }
    }
}
```
##### 同步基于offset而不是消息本身

虽然生产者在发送的时候是一条一条消息的发送，但是同步和消息发送/存储是两个完全不同的服务，无论是否有消息发送，消息的同步过程一直没有停止。并且消息的同步也不是针对一个个消息的，而是只记录一个同步的最大offset。

举个例子，假设当前master的最大offset是 offsetM，slave本身落后master100个消息，假设是OffsetM- X，（X的值取决于各个消息体的存储大小总和），当你发送第101个消息（大小是Y）的时候，master offset去到OffsetM+Y。如果master是SYNC_MASTER，实际上等待的时间会很长，因为他不是等待这个正在发送的消息的同步，而是在等待过去的所有消息的同步，直到同步到OffsetM+Y，才认为写入发送成功返回SEND_OK。如果等待了超时都没有到这个offset，那么就会得到FLUSH_SLAVE_TIMEOUT 这个返回值。但后台的同步工作会一直进行下去，不会停止。



这也就是说，在SYNC_MASTER的情况下，在所有的slave都落后master进度很多的时候（但又不至于被认为不可用），**会让发送TPS下降，而RT（ResponseTime）上升**。

##### 刷盘策略
除了同步策略，刷盘策略也是影响消息可靠性的重要一环。**RocketMQ支持SYNC_FLUSH和ASYNC_FLUSH两种策略**。前者是同步刷盘，后者是异步的刷盘。如果同步刷盘，则消息发送需要等到真正刷到磁盘才会返回。

```
public void handleDiskFlush(AppendMessageResult result, PutMessageResult putMessageResult, MessageExt messageExt) {
    // Synchronization flush 同步刷盘
    if (FlushDiskType.SYNC_FLUSH == this.defaultMessageStore.getMessageStoreConfig().getFlushDiskType()) {
        // 启动一个线程
        final GroupCommitService service = (GroupCommitService) this.flushCommitLogService;
        // 判断同步刷盘是否需要等待返回结果
        if (messageExt.isWaitStoreMsgOK()) {
            GroupCommitRequest request = new GroupCommitRequest(result.getWroteOffset() + result.getWroteBytes());
            service.putRequest(request);
            // 线程wait  超时时间5s 类似于 Future 模式
            boolean flushOK = request.waitForFlush(this.defaultMessageStore.getMessageStoreConfig().getSyncFlushTimeout());
            if (!flushOK) {
                log.error("do groupcommit, wait for flush failed, topic: " + messageExt.getTopic() + " tags: " + messageExt.getTags()
                    + " client address: " + messageExt.getBornHostString());
                putMessageResult.setPutMessageStatus(PutMessageStatus.FLUSH_DISK_TIMEOUT);
            }
        } else {
            // 退化成异步刷盘
            service.wakeup();
        }
    }
    // Asynchronous flush 异步刷盘
    else {
        // 是否开启堆外内存缓存池  默认为false
        if (!this.defaultMessageStore.getMessageStoreConfig().isTransientStorePoolEnable()) {
            flushCommitLogService.wakeup();
        } else {
            commitLogService.wakeup();
        }
    }
}
```

实际上真正意义的高可靠应该是得到一个SEND_OK请求的时候，消息至少被持久化了到了两个结点。但如果这样，则势必要求消息的存储要同步的刷盘，由于刷盘的时间耗时长，势必会影响性能。而采取异步刷盘策略，则可以依赖批量刷的特性，有更好的性能。

实际上，**对于消息丢失容忍度很低的应用，官方建议采用SYNC_MASTER + ASYNC_FLUSH的方式**，这样只有master和slave在刷盘前同时挂掉，且都没有刷到磁盘，消息才会丢失。这样是一个兼顾性能和可靠性的较好平衡。而如果对消息丢失容忍度较高的，则建议采用ASYNC_MASTER+ASYNC_FLUSH的方式。


