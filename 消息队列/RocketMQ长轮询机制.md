![image](http://assets.processon.com/chart_image/5dc1611fe4b0e433945d0808.png)

### 如何选择消息消费的方式—Pull or Push

对于任何一款消息中间件而言，消费者客户端一般有两种方式从消息中间件获取消息并消费：

##### Push方式

由消息中间件（MQ消息服务器代理）主动地将消息推送给消费者；采用Push方式，可以**尽可能实时地将消息发送给消费者进行消费**。但是，在**消费者的处理消息的能力较弱**的时候(比如，消费者端的业务系统处理一条消息的流程比较复杂，其中的调用链路比较多导致消费时间比较久。概括起来地说就是“慢消费问题”)，而MQ不断地向消费者Push消息，**消费者端的缓冲区可能会溢出，导致异常**；

##### Pull方式
由消费者客户端主动向消息中间件（MQ消息服务器代理）拉取消息；采用Pull方式，**如何设置Pull消息的频率需要重点去考虑**，举个例子来说，可能1分钟内连续来了1000条消息，然后2小时内没有新消息产生（概括起来说就是“**消息延迟与忙等待**”）。如果每次Pull的时间间隔比较久，会增加消息的延迟，即消息到达消费者的时间加长，MQ中消息的堆积量变大；若每次Pull的时间间隔较短，但是在一段时间内MQ中并没有任何消息可以消费，那么会产生很多**无效的Pull请求的RPC开销**，影响MQ整体的网络性能

##### RocketMQ消息消费的长轮询机制
> 思考题：
上面简要说明了Push和Pull两种消息消费方式的概念和各自特点。如果长时间没有消息，而消费者端又不停的发送Pull请求不就会导致RocketMQ中Broker端负载很高吗？那么在RocketMQ中如何解决以做到高效的消息消费呢？

通过研究源码可知，RocketMQ的消费方式都是**基于拉模式拉取消息**的，而在这其中有一种**长轮询机制**（对普通轮询的一种优化），来平衡上面Push/Pull模型的各自缺点。基本设计思路是：
- 消费者如果第一次尝试Pull消息失败（比如：Broker端没有可以消费的消息），并不立即给消费者客户端返回Response的响应，而是先hold住并且挂起请求（将请求保存至pullRequestTable本地缓存变量中），
- 然后Broker端的后台独立线程—PullRequestHoldService会从pullRequestTable本地缓存变量中不断地去取，具体的做法是查询**待拉取消息的偏移量是否小于消费队列最大偏移量**，如果条件成立则说明有新消息达到Broker端
> （这里，在RocketMQ的Broker端会有一个后台独立线程—ReputMessageService不停地构建ConsumeQueue/IndexFile数据，同时取出hold住的请求并进行二次处理）

- 则通过重新调用一次业务处理器—PullMessageProcessor的处理请求方法—processRequest()来重新尝试拉取消息（此处，**每隔5S重试一次，默认长轮询整体的时间设置为30s**）。

RocketMQ消息Pull的长轮询机制的关键在于**Broker端的PullRequestHoldService和ReputMessageService两个后台线程**。对于RocketMQ的长轮询（LongPolling）消费模式后面会专门详细介绍。


### RocketMQ中Pull和Push两种消费模式流程简析
RocketMQ提供了两种消费模式，Push和Pull，**大多数场景使用的是Push模式**，在源码中这两种模式分别对应的是DefaultMQPushConsumer类和DefaultMQPullConsumer类。**Push模式实际上在内部还是使用的Pull方式实现的**，通过Pull不断地轮询Broker获取消息，当不存在新消息时，Broker端会挂起Pull请求，直到有新消息产生才取消挂起，返回新消息。

##### （1）RocketMQ的Pull消费模式流程简析

RocketMQ的Pull模式相对来得简单，从上面的demo代码中可以看出，业务应用代码通过由Topic获取到的MessageQueue直接拉取消息（最后真正执行的是PullAPIWrapper的pullKernelImpl()方法，通过发送拉取消息的RPC请求给Broker端）。其中，消息消费的偏移量需要Consumer端自己去维护。

##### （2）RocketMQ的Push消费模式流程简析

在本文前面已经提到过了，从严格意义上说，RocketMQ并没有实现真正的消息消费的Push模式，而是对Pull模式进行了一定的优化，
- 一方面在Consumer端开启后台独立的线程—PullMessageService不断地从阻塞队列—pullRequestQueue中获取PullRequest请求并通过网络通信模块发送Pull消息的RPC请求给Broker端。
- 另外一方面，后台独立线程—rebalanceService根据Topic中消息队列个数和当前消费组内消费者个数进行负载均衡，将产生的对应PullRequest实例放入阻塞队列—pullRequestQueue中。这里算是比较典型的生产者-消费者模型，实现了准实时的自动消息拉取。然后，再根据业务反馈是否成功消费来推动消费进度。
- 在Broker端，PullMessageProcessor业务处理器收到Pull消息的RPC请求后，通过MessageStore实例从commitLog获取消息。如果第一次尝试Pull消息失败（比如Broker端没有可以消费的消息），则通过长轮询机制先hold住并且挂起该请求，然后通过Broker端的后台线程PullRequestHoldService重新尝试和后台线程ReputMessageService的二次处理。
