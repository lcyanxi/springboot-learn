![image](http://assets.processon.com/chart_image/5ffc21d21e0853437c3c013d.png)

> RocketMQ在消费失败后的是如何来保证消息消费的可靠性？

### 重试队列与死信队列的概念

##### 重试队列

**重试队列**：如果Consumer端因为各种类型异常导致本次消费失败，为防止该消息丢失而需要将其重新回发给Broker端保存，保存这种因为异常无法正常消费而回发给MQ的消息队列称之为重试队列。

RocketMQ会为每个消费组都设置一个Topic名称为%RETRY%+**consumerGroup**的重试队列（这里需要注意的是，**这个Topic的重试队列是针对消费组，而不是针对每个Topic设置的**），用于暂时保存因为各种异常而导致Consumer端无法消费的消息。考虑到异常恢复起来需要一些时间，会为重试队列设置多个重试级别，每个重试级别都有与之对应的重新投递延时，重试次数越多投递延时就越大。RocketMQ对于重试消息的处理是先保存至Topic名称为**SCHEDULETOPICXXXX**的**延迟队列**中，后台定时任务按照对应的时间进行Delay后重新保存至“%RETRY%+consumerGroup”的重试队列中

##### 死信队列
**死信队列**:由于有些原因导致Consumer端长时间的无法正常消费从Broker端Pull过来的业务消息，为了确保消息不会被无故的丢弃，那么超过配置的“最大重试消费次数”后就会移入到这个死信队列中

在RocketMQ中，SubscriptionGroupConfig配置常量默认地设置了两个参数，一个是**retryQueueNums为1**（重试队列数量为1个），另外一个是**retryMaxTimes为16**（最大重试消费的次数为16次）。Broker端通过校验判断，如果超过了最大重试消费次数则会将消息移至这里所说的死信队列。这里，RocketMQ会为每个消费组都设置一个Topic命名为“%DLQ%+**consumerGroup**"的死信队列。一般在实际应用中，移入至死信队列的消息，需要人工干预处理；

##### Consumer端回发消息至Broker端
在业务工程中的Consumer端（Push消费模式下），如果消息能够正常消费需要在注册的消息监听回调方法中返回**CONSUME_SUCCESS**的消费状态，否则因为各类异常消费失败则返回**RECONSUME_LATER**的消费状态。消费状态的枚举类型如下所示：

```
public enum ConsumeConcurrentlyStatus{
    //业务方消费成功
    CONSUME_SUCCESS,
    //业务方消费失败，之后进行重新尝试消费
    RECONSUME_LATER;
}
```
如果业务工程对消息消费失败了，那么则会抛出异常并且返回这里的RECONSUME_LATER状态。这里，在消费消息的服务线程—consumeMessageService中，将封装好的消息消费任务ConsumeRequest提交至线程池—consumeExecutor异步执行。从消息消费任务ConsumeRequest的run()方法中会执行业务工程中注册的消息监听回调方法，并在processConsumeResult方法中根据业务工程返回的状态（CONSUME_SUCCESS或者RECONSUME_LATER）进行判断和做对应的处理（下面讲的都是在消费通信模式为集群模型下的，广播模型下的比较简单就不再分析了）。

**（1）业务方正常消费（CONSUME_SUCCESS**）：正常情况下，设置ackIndex的值为consumeRequest.getMsgs().size() - 1，因此后面的遍历consumeRequest.getMsgs()消息集合条件不成立，不会调用回发消费失败消息至Broker端的方法—sendMessageBack(msg, context)。最后，更新消费的偏移量；

**（2）业务方消费失败（RECONSUME_LATER）**：异常情况下，设置ackIndex的值为-1，这时就会进入到遍历consumeRequest.getMsgs()消息集合的for循环中，执行回发消息的方法—sendMessageBack(msg, context)。这里，首先会根据brokerName得到Broker端的地址信息，然后通过网络通信的Remoting模块发送RPC请求到指定的Broker上，如果上述过程失败，**则创建一条新的消息重新发送给Broker**，此时新消息的Topic为“%RETRY%+ConsumeGroupName”—重试队列的主题。其中，在MQClientAPIImpl实例的consumerSendMessageBack()方法中封装了ConsumerSendMsgBackRequestHeader的请求体，随后完成回发消费失败消息的RPC通信请求（业务请求码为：CONSUMERSENDMSGBACK）。**倘若上面的回发消息流程失败，则会延迟5S后重新在Consumer端进行重新消费。与正常消费的情况一样，在最后更新消费的偏移量；**

##### Broker端对于回发消息处理的主要流程
- Broker端收到这条Consumer端回发过来的消息后，通过业务请求码（CONSUMERSENDMSGBACK）匹配业务处理器—SendMessageProcessor来处理。在完成一系列的前置校验（这里主要是“消费分组是否存在”、“检查Broker是否有写入权限”、“检查重试队列数是否大于0”等）后，尝试获取重试队列的TopicConfig对象（如果是第一次无法获取到，则调用createTopicInSendMessageBackMethod()方法进行创建）。
- 根据回发过来的消息偏移量尝试从commitlog日志文件中查询消息内容，若不存在则返回异常错误。
- 然后，设置重试队列的Topic—“%RETRY%+consumerGroup”至MessageExt的扩展属性“RETRYTOPIC”中，并对根据延迟级别delayLevel和最大重试消费次数maxReconsumeTimes进行判断，如果超过最大重试消费次数（默认16次），则会创建死信队列的TopicConfig对象（用于后面将回发过来的消息移入死信队列）
- 在构建完成需要落盘的MessageExtBrokerInner对象后，调用“commitLog.putMessage(msg)”方法做消息持久化。这里，需要注意的是，在putMessage(msg)的方法里会使用“SCHEDULETOPICXXXX”和对应的延迟级别队列Id分别替换MessageExtBrokerInner对象的Topic和QueueId属性值，并将原来设置的重试队列主题（“%RETRY%+consumerGroup”）的Topic和QueueId属性值做一个备份分别存入扩展属性properties的“REALTOPIC”和“REALQID”属性中。

看到这里也就大致明白了，**回发给Broker端的消费失败的消息并非直接保存至重试队列中，而是会先存至Topic为“SCHEDULETOPICXXXX”的定时延迟队列中。**

> 疑问：上面说了RocketMQ的重试队列的Topic是“%RETRY%+consumerGroup”，为啥这里要保存至Topic是“SCHEDULETOPICXXXX”的这个延迟队列中呢？【减少请求风暴】

- 在源码中搜索下关键字—“SCHEDULETOPICXXXX”，会发现Broker端还存在着一个后台服务线程—ScheduleMessageService（通过消息存储服务—DefaultMessageStore启动），通过查看源码可以知道其中有一个DeliverDelayedMessageTimerTask定时任务线程会根据Topic（“SCHEDULETOPICXXXX”）与QueueId，先查到逻辑消费队列ConsumeQueue，
- 然后根据偏移量，找到ConsumeQueue中的内存映射对象，从commitlog日志中找到消息对象MessageExt，并做一个消息体的转换（messageTimeup()方法，由定时延迟队列消息转化为重试队列的消息），再次做持久化落盘，这时候才会真正的保存至重试队列中。
- 看到这里就可以解释上面的疑问了，定时延迟队列只是为了用于暂存的，然后延迟一段时间再将消息移入至重试队列中。RocketMQ设定不同的延时级别delayLevel，并且与定时延迟队列相对应，具体源码如下：


```
private String messageDelayLevel = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";

// 定时延时消息主题的队列与延迟等级对应关系
public static int delayLevel2QueueId(final int delayLevel) {
    return delayLevel - 1;
}
```
#####  Consumer端消费重试机制
每个Consumer实例在启动的时候就默认订阅了该消费组的重试队列主题

因此，这里也就清楚了，Consumer端会一直订阅该重试队列主题的消息，向Broker端发送如下的拉取消息的PullRequest请求，以尝试重新再次消费重试队列中积压的消息。
