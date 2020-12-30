consumer的每个实例是靠队列分配来决定如何消费消息的。**那么消费进度具体是如何管理的，又是如何保证消息成功消费的**（RocketMQ有保证消息肯定消费成功的特性（失败则重试）？

本文将详细解析消息具体是如何ack的，又是如何保证消费肯定成功的。

由于以上工作所有的机制都实现在PushConsumer中，所以本文的原理均只适用于RocketMQ中的PushConsumer即Java客户端中的DefaultPushConsumer。 若使用了PullConsumer模式，类似的工作如何ack，如何保证消费等均需要使用方自己实现。

> 注：广播消费和集群消费的处理有部分区别，以下均特指集群消费（CLSUTER），广播（BROADCASTING）下部分可能不适用。

##### 保证消费成功
PushConsumer为了保证消息肯定消费成功，**只有使用方明确表示消费成功，RocketMQ才会认为消息消费成功**。中途断电，抛出异常等都不会认为成功——即都会重新投递。

消费的时候，我们需要注入一个消费回调，具体sample代码如下：

```
consumer.registerMessageListener(new MessageListenerConcurrently() {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        System.out.println(Thread.currentThread().getName() + " Receive New Messages: " + msgs);
        doMyJob();//执行真正消费
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
});
```
业务实现消费回调的时候，当且仅当此回调函数返回**CONSUME_SUCCESS**，RocketMQ才会认为这批消息（默认是1条）是消费完成的。（具体如何ACK见后面章节）

如果这时候消息消费失败，例如数据库异常，余额不足扣款失败等一切业务认为消息需要重试的场景，只要返回RECONSUME_LATER，RocketMQ就会认为这批消息消费失败了。

**为了保证消息是肯定被至少消费成功一次**，RocketMQ会把这批消息重发回Broker（topic不是原topic而是这个消费租的RETRY topic），在延迟的某个时间点（默认是10秒，业务可设置）后，再次投递到这个ConsumerGroup。而如果一直这样重复消费都持续失败到一定次数（默认16次），就会投递到DLQ死信队列。应用可以监控死信队列来做人工干预。

**注：**
- 如果业务的回调没有处理好而抛出异常，会认为是消费失败当RECONSUME_LATER处理。
- 当使用顺序消费的回调MessageListenerOrderly时，由于顺序消费是要前者消费成功才能继续消费，所以没有RECONSUME_LATER的这个状态，只有SUSPEND_CURRENT_QUEUE_A_MOMENT来暂停队列的其余消费，直到原消息不断重试成功为止才能继续消费。

##### 启动的时候从哪里消费
当新实例启动的时候，PushConsumer会拿到本消费组broker已经记录好的消费进度（consumer offset），按照这个进度发起自己的第一次Pull请求。

如果这个消费进度在Broker并没有存储起来，证明这个是一个**全新的消费组**，这时候客户端有几个策略可以选择：

```
CONSUME_FROM_LAST_OFFSET //默认策略，从该队列最尾开始消费，即跳过历史消息
CONSUME_FROM_FIRST_OFFSET //从队列最开始开始消费，即历史消息（还储存在broker的）全部消费一遍
CONSUME_FROM_TIMESTAMP//从某个时间点开始消费，和setConsumeTimestamp()配合使用，默认是半个小时以前
```
所以，社区中经常有人问：“**为什么我设了CONSUME_FROM_LAST_OFFSET，历史的消息还是被消费了**”？ 原因就在于**只有全新的消费组才会使用到这些策略**，老的消费组都是按已经存储过的消费进度继续消费。

对于老消费组想跳过历史消息可以采用以下两种方法：
- 代码按照日期判断，太老的消息直接return CONSUME_SUCCESS过滤。
- 代码判断消息的offset和MAX_OFFSET相差很远，认为是积压了很多，直接return CONSUME_SUCCESS过滤。
- 消费者启动前，先调整该消费组的消费进度，再开始消费。可以人工使用命令resetOffsetByTime，或调用内部的运维接口，祥见ResetOffsetByTimeCommand.java
##### 消息ACK机制
RocketMQ是以**consumer group+queue**为单位是管理消费进度的，以一个consumer offset标记这个这个消费组在这条queue上的消费进度。

如果某已存在的消费组出现了新消费实例的时候，依靠这个组的消费进度，就可以判断第一次是从哪里开始拉取的。

每次消息成功后，本地的消费进度会被更新，然后由定时器定时同步到broker，以此持久化消费进度。

但是每次记录消费进度的时候，只会把一批消息中最小的offset值为消费进度值，如下图：
![image](https://pic3.zhimg.com/80/v2-abec38173bc87e3f63d93b4125ca4a1e_720w.png)

这钟方式和传统的一条message单独ack的方式有本质的区别。性能上提升的同时，会带来一个潜在的重复问题——由于消费进度只是记录了一个下标，就可能出现拉取了100条消息如 2101-2200的消息，后面99条都消费结束了，只有2101消费一直没有结束的情况。

在这种情况下，RocketMQ为了保证消息肯定被消费成功，消费进度职能维持在2101，直到2101也消费结束了，本地的消费进度才会一下子更新到2200。

在这种设计下，就有消费大量重复的风险。如2101在还没有消费完成的时候消费实例突然退出（机器断电，或者被kill）。这条queue的消费进度还是维持在2101，当queue重新分配给新的实例的时候，新的实例从broker上拿到的消费进度还是维持在2101，这时候就会又从2101开始消费，2102-2200这批消息实际上已经被消费过还是会投递一次。

对于这个场景，3.2.6之前的RocketMQ无能为力，所以业务必须要保证消息消费的幂等性，这也是RocketMQ官方多次强调的态度。

实际上，从源码的角度上看，RocketMQ可能是考虑过这个问题的，截止到3.2.6的版本的源码中，可以看到为了缓解这个问题的影响面，DefaultMQPushConsumer中有个配置consumeConcurrentlyMaxSpan

```
/**
 * Concurrently max span offset.it has no effect on sequential consumption
 */
private int consumeConcurrentlyMaxSpan = 2000;
```
这个值默认是2000，当RocketMQ发现本地缓存的消息的最大值-最小值差距大于这个值（2000）的时候，会触发**流控**——也就是说如果头尾都卡住了部分消息，达到了这个阈值就不再拉取消息。

但作用实际很有限，像刚刚这个例子，2101的消费是死循环，其他消费非常正常的话，是无能为力的。一旦退出，在不人工干预的情况下，2101后所有消息全部重复。

##### Ack卡进度解决方案

对于这个卡消费进度的问题，最显而易见的解法是**设定一个超时时间**，达到超时时间的那个消费当作消费失败处理。

后来RocketMQ显然也发现了这个问题，而RocketMQ在3.5.8之后也就是采用这样的方案去解决这个问题。
- 在pushConsumer中 有一个consumeTimeout字段（默认15分钟），用于设置最大的消费超时时间。消费前会记录一个消费的开始时间，后面用于比对。
- 消费者启动的时候，会定期扫描所有消费的消息，达到这个timeout的那些消息，就会触发sendBack并ack的操作。这里扫描的间隔也是consumeTimeout（单位分钟）的间隔。

核心源码如下：

```
//ConsumeMessageConcurrentlyService.java
public void start() {
    this.CleanExpireMsgExecutors.scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {cleanExpireMsg();}
    }, this.defaultMQPushConsumer.getConsumeTimeout(), this.defaultMQPushConsumer.getConsumeTimeout(), TimeUnit.MINUTES);
}
//ConsumeMessageConcurrentlyService.java
private void cleanExpireMsg() {
    Iterator<Map.Entry<MessageQueue, ProcessQueue>> it =
            this.defaultMQPushConsumerImpl.getRebalanceImpl().getProcessQueueTable().entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry<MessageQueue, ProcessQueue> next = it.next();
        ProcessQueue pq = next.getValue();
        pq.cleanExpiredMsg(this.defaultMQPushConsumer);
    }
}

//ProcessQueue.java
public void cleanExpiredMsg(DefaultMQPushConsumer pushConsumer) {
    // .......
    int loop = msgTreeMap.size() < 16 ? msgTreeMap.size() : 16;
    for (int i = 0; i < loop; i++) {
        MessageExt msg = null;
        try {
            this.lockTreeMap.readLock().lockInterruptibly();
            try {
                if (!msgTreeMap.isEmpty() && System.currentTimeMillis() - Long.parseLong(MessageAccessor.getConsumeStartTimeStamp(msgTreeMap.firstEntry().getValue())) > pushConsumer.getConsumeTimeout() * 60 * 1000) {
                    msg = msgTreeMap.firstEntry().getValue();
                } 
                // .....

        try {
            pushConsumer.sendMessageBack(msg, 3);
            log.info("send expire msg back. topic={}, msgId={}, storeHost={}, queueId={}, queueOffset={}", msg.getTopic(), msg.getMsgId(), msg.getStoreHost(), msg.getQueueId(), msg.getQueueOffset());
            try {
                this.lockTreeMap.writeLock().lockInterruptibly();
                try {
                    if (!msgTreeMap.isEmpty() && msg.getQueueOffset() == msgTreeMap.firstKey()) {
                        try {
                            msgTreeMap.remove(msgTreeMap.firstKey());
                        } 
                        // ......
        }
    }
}
```

通过源码看这个方案，其实可以看出有几个不太完善的问题：
- 消费timeout的时间非常不精确。由于扫描的间隔是15分钟，所以实际上触发的时候，消息是有可能卡住了接近30分钟（15*2）才被清理。
- 由于定时器一启动就开始调度了，中途这个consumeTimeout再更新也不会生效。

