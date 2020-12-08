![image](https://mmbiz.qpic.cn/mmbiz_png/aYNOWudT2ciaO3RZRrSdTw24ol4mdWpxThg9HXzrKzHNUrhWzk72icTmVNqAEH8icecpH6FLMh0QUKcKRFldACUBA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

上面这张监控图，对于服务端的研发同学来说再熟悉不过了。在日常的系统维护中，『**服务超时**』应该属于监控报警最多的一类问题。

尤其在微服务架构下，一次请求可能要经过一条很长的链路，跨多个服务调用后才能返回结果。当服务超时发生时，研发同学往往要抽丝剥茧般去分析自身系统的性能以及依赖服务的性能，这也是为什么服务超时相对于服务出错和服务调用量异常更难调查的原因。

这篇文章将通过一个真实的线上事故，系统性地介绍下：在微服务架构下，该如何正确理解并设置RPC接口的超时时间，让大家在开发服务端接口时有更全局的视野。内容将分成以下4个部分：
1. 从一次RPC接口超时引发的线上事故说起
2. 超时的实现原理是什么？
3. 设置超时时间到底是为了解决什么问题？
4. 应该如何合理的设置超时时间？

### 从一次RPC接口超时引发的线上事故说起？
事故发生在电商APP的首页推荐模块，某天中午突然收到用户反馈：APP首页除了banner图和导航区域，下方的推荐模块变成空白页了（推荐模块占到首页2/3的空间，是根据用户兴趣由算法实时推荐的商品list）。

上面的业务场景可以借助下面的调用链来理解
![image](https://mmbiz.qpic.cn/mmbiz_png/aYNOWudT2ciaO3RZRrSdTw24ol4mdWpxT0ibfq3y0DbarM9xjC0NFkS6k7QRQfmmYs9A4fQa6TsbmnibAnBlCJVFQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)
- APP端发起一个HTTP请求到业务网关
- 业务网关RPC调用推荐服务，获取推荐商品list
- 如果第2步调用失败，则服务降级，改成RPC调用商品排序服务，获取热销商品list进行托底
- 如果第3步调用失败，则再次降级，直接获取Redis缓存中的热销商品list

粗看起来，两个依赖服务的降级策略都考虑进去了，理论上就算推荐服务或者商品排序服务全部挂掉，服务端都应该可以返回数据给APP端。但是APP端的推荐模块确实出现空白了，降级策略可能并未生效，下面详细说下定位过程。

##### 1、问题定位过程

- 第1步：APP端通过抓包发现：HTTP请求存在接口超时（超时时间设置的是5秒）。
- 第2步：业务网关通过日志发现：调用推荐服务的RPC接口出现了大面积超时（超时时间设置的是3秒），错误信息如下：
![image](https://mmbiz.qpic.cn/mmbiz_png/aYNOWudT2ciaO3RZRrSdTw24ol4mdWpxT3s2icq5HH2yk25iaMITa6jrMKSL9EpMhPMoZzUCxFpRzth6J8IGM7vibQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)
-  第3步：推荐服务通过日志发现：dubbo的线程池耗尽，错误信息如下：
![image](https://mmbiz.qpic.cn/mmbiz_png/aYNOWudT2ciaP1hviaduiaoiaX5WkeQD7I2D22ib1esphvnPBKHicj674z3wwQFq9xfpsJJIH0Ee0ud5NlLZIo24Dq0g/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

通过以上3步，基本就定位到了问题出现在推荐服务，后来进一步调查得出：是因为推荐服务依赖的redis集群不可用导致了超时，进而导致线程池耗尽。详细原因这里不作展开，跟本文要讨论的主题相关性不大。

##### 2、降级策略未生效的原因分析
下面再接着分析下：当推荐服务调用失败时，为什么业务网关的降级策略没有生效呢？理论上来说，不应该降级去调用商品排序服务进行托底吗？

最终跟踪分析找到了根本原因：APP端调用业务网关的超时时间是5秒，业务网关调用推荐服务的超时时间是3秒，同时还设置了3次超时重试，这样当推荐服务调用失败进行第2次重试时，HTTP请求就已经超时了，因此业务网关的所有降级策略都不会生效。下面是更加直观的示意图：
![image](https://mmbiz.qpic.cn/mmbiz_png/aYNOWudT2ciaO3RZRrSdTw24ol4mdWpxT7StU4nEa9x2KXXpnZ6N0dNQmIQ9oPAh1RvyERwE84mGjIcTicHDmJrQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)
##### 3、解决方案
- 将业务网关调用推荐服务的超时时间改成了800ms（推荐服务的TP99大约为540ms），超时重试次数改成了2次
- 将业务网关调用商品排序服务的超时时间改成了600ms（商品排序服务的TP99大约为400ms），超时重试次数也改成了2次

关于超时时间和重试次数的设置，**需要考虑整个调用链中所有依赖服务的耗时、各个服务是否是核心服务等很多因素**。这里先不作展开，后文会详细介绍具体方法。

【**其实当时在疫情期间我们200百万份公益活动期间也遇到了这样的问题，dubbo大面积超时，而dubbo重试机制导致请求风暴放大3倍，还有一个问题这会引起==数据的丢失==和==数据重复==，(当时情况就是不是在修数据就是在修数据的路上^_^)**】

###  超时的实现原理是什么？
只有了解了RPC框架的超时实现原理，才能更好地去设置它。不论是dubbo、SpringCloud或者大厂自研的微服务框架（比如京东的JSF），超时的实现原理基本类似。下面以dubbo2.8.4版本的源码为例来看下具体实现。

熟悉dubbo的同学都知道，可在两个地方配置超时时间：分别是provider（服务端，服务提供方）和consumer（消费端，服务调用方）。

服务端的超时配置是消费端的缺省配置，也就是说只要服务端设置了超时时间，则所有消费端都无需设置，可通过注册中心传递给消费端，这样：一方面简化了配置，另一方面因为服务端更清楚自己的接口性能，所以交给服务端进行设置也算合理。

dubbo支持非常细粒度的超时设置，包括：方法级别、接口级别和全局。如果各个级别同时配置了，优先级为：消费端方法级 > 服务端方法级 > 消费端接口级 > 服务端接口级 > 消费端全局 > 服务端全局。

通过源码，我们先看下服务端的超时处理逻辑

```
@Activate(group = Constants.PROVIDER)
public class TimeoutFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // 执行真正的逻辑调用，并统计耗时
        long start = System.currentTimeMillis();
        Result result = invoker.invoke(invocation);
        long elapsed = System.currentTimeMillis() - start;
        // 判断是否超时
        if (invoker.getUrl() != null
                && elapsed > invoker.getUrl().getMethodParameter(invocation.getMethodName(),
                "timeout", Integer.MAX_VALUE)) {
            // 打印warn日志
            if (logger.isWarnEnabled()) {
                logger.warn("invoke time out. method: " + invocation.getMethodName()
                        + " arguments: " + Arrays.toString(invocation.getArguments()) + " , url is "
                        + invoker.getUrl() + ", invoke elapsed " + elapsed + " ms.");
            }
        }
        return result;
    }
}
```
可以看到，服务端即使超时，也只是打印了一个warn日志。因此，**服务端的超时设置并不会影响实际的调用过程，就算超时也会执行完整个处理逻辑**。

再来看下消费端的超时处理逻辑
```
public class FailoverClusterInvoker<T> {

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Result doInvoke(Invocation invocation, final List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException {
        ............
        // 循环调用，失败重试
        for (int i = 0; i < len; i++) {
                return result;
            } catch (RpcException e) {
            // 业务异常  终止重试
                if (e.isBiz()) { // biz exception.
                    throw e;
                }
                le = e;
            } catch (Throwable e) {
                le = new RpcException(e.getMessage(), e);
            } finally {
                providers.add(invoker.getUrl().getAddress());
            }
        }
        // 若重试失败，则抛出异常

```
FailoverCluster是集群容错的缺省模式，当调用失败后会切换成调用其他服务器。再看下doInvoke方法，当调用失败时，会先判断是否是业务异常，如果是则终止重试，否则会一直重试直到达到重试次数。


继续跟踪invoker的invoke方法，可以看到在请求发出后通过**Future的get方法**获取结果，源码如下：

```
    public Object get(int timeout) throws RemotingException {
        if (timeout <= 0) {
            timeout = Constants.DEFAULT_TIMEOUT;
        }
        if (!isDone()) {
            long start = System.currentTimeMillis();
            lock.lock();
            try {
                // 循环判断
                while (!isDone()) {
                    // 放弃锁，进入等待状态
                    done.await(timeout, TimeUnit.MILLISECONDS);
                    // 判断是否已经返回结果或者已经超时
                    if (isDone() || System.currentTimeMillis() - start > timeout) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
            if (!isDone()) {
                // 如果未返回结果，则抛出超时异常
                throw new TimeoutException(sent > 0, channel, getTimeoutMessage(false));
            }
        }
        return returnFromResponse();
    }
```

进入方法后开始计时，如果在设定的超时时间内没有获得返回结果，则抛出TimeoutException。因此，**消费端的超时逻辑同时受到超时时间和超时次数两个参数的控制，像网络异常、响应超时等都会一直重试，直到达到重试次数。**

###  设置超时时间是为了解决什么问题？
RPC框架的超时重试机制到底是为了解决什么问题呢？从微服务架构这个宏观角度来说，它是为了确保服务链路的稳定性，提供了一种框架级的容错能力。微观上如何理解呢？可以从下面几个具体case来看：
1. consumer调用provider，如果不设置超时时间，则consumer的响应时间肯定会大于provider的响应时间。

当provider性能变差时，consumer的性能也会受到影响，因为它必须无限期地等待provider的返回。

假如整个调用链路经过了A、B、C、D多个服务，只要D的性能变差，就会自下而上影响到A、B、C，最终造成整个链路超时甚至瘫痪，因此设置超时时间是非常有必要的。

2. 假设consumer是核心的商品服务，provider是非核心的评论服务，当评价服务出现性能问题时，商品服务可以接受不返回评价信息，从而保证能继续对外提供服务。

这样情况下，就必须设置一个超时时间，当评价服务超过这个阈值时，商品服务不用继续等待。

3. provider很有可能是因为某个瞬间的网络抖动或者机器高负载引起的超时，如果超时后直接放弃，某些场景会造成业务损失（比如库存接口超时会导致下单失败）。

因此，对于这种临时性的服务抖动，如果在超时后重试一下是可以挽救的，所以有必要通过重试机制来解决。

**但是引入超时重试机制后，并非一切就完美了。它同样会带来副作用，这些是开发RPC接口必须要考虑，同时也是最容易忽视的问题**：

1. **重复请求（当时我们的insert重复数据很是严重，其中就有它的功劳，最后是把所有save接口的重试次数去掉）**：

有可能provider执行完了，但是因为网络抖动consumer认为超时了，这种情况下重试机制就会导致重复请求，从而带来脏数据问题，因此服务端必须考虑接口的幂等性。

2. **降低consumer的负载能力**：

如果provider并不是临时性的抖动，而是确实存在性能问题，这样重试多次也是没法成功的，反而会使得consumer的平均响应时间变长。

比如正常情况下provider的平均响应时间是1s，consumer将超时时间设置成1.5s，重试次数设置为2次，这样单次请求将耗时3s，consumer的整体负载就会被拉下来，如果consumer是一个高QPS的服务，还有可能引起**连锁反应造成雪崩**。

3. **爆炸式的重试风暴(我们还真遇到过,在系统崩盘周围徘徊 ^_^ )**：

假如一条调用链路经过了4个服务，最底层的服务D出现超时，这样上游服务都将发起重试，假设重试次数都设置的3次，那么B将面临正常情况下3倍的负载量，C是9倍，D是27倍，整个服务集群可能因此雪崩。
![image](https://mmbiz.qpic.cn/mmbiz_png/aYNOWudT2ciaO3RZRrSdTw24ol4mdWpxTCsCgEjbIm8aH3HR1dhBjwPXqS5bNX2k3ykmH5eqVQjjvgiauw8GOC4w/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

### 应该如何合理的设置超时时间？
理解了RPC框架的超时实现原理和可能引入的副作用后，可以按照下面的方法进行超时设置：
- 设置调用方的超时时间之前，先了解清楚依赖服务的TP99响应时间是多少（如果依赖服务性能波动大，也可以看TP95），调用方的超时时间可以在此基础上加50%

- 如果RPC框架支持多粒度的超时设置，则：全局超时时间应该要略大于接口级别最长的耗时时间，每个接口的超时时间应该要略大于方法级别最长的耗时时间，每个方法的超时时间应该要略大于实际的方法执行时间

- 区分是可重试服务还是不可重试服务，如果接口没实现幂等则不允许设置重试次数。
注意：读接口是天然幂等的，写接口则可以使用业务单据ID或者在调用方生成唯一ID传递给服务端，通过此ID进行防重避免引入脏数据

- 如果RPC框架支持服务端的超时设置，同样基于前面3条规则依次进行设置，这样能避免客户端不设置的情况下配置是合理的，减少隐患

- 如果从业务角度来看，服务可用性要求不用那么高（比如偏内部的应用系统），则可以不用设置超时重试次数，直接人工重试即可，这样能减少接口实现的复杂度，反而更利于后期维护

- 重试次数设置越大，服务可用性越高，业务损失也能进一步降低，但是性能隐患也会更大，这个需要综合考虑设置成几次（一般是2次，最多3次）

- 如果调用方是高QPS服务，则必须考虑服务方超时情况下的降级和熔断策略。（比如超过10%的请求出错，则停止重试机制直接熔断，改成调用其他服务、异步MQ机制、或者使用调用方的缓存数据）

**最后，再简单总结下：**

RPC接口的超时设置看似简单，实际上有很大学问。不仅涉及到很多技术层面的问题（比如接口幂等、服务降级和熔断、性能评估和优化），同时还需要从业务角度评估必要性。知其然知其所以然，希望这些知识能让你在开发RPC接口时，有更全局的视野。

[参考博客](https://mp.weixin.qq.com/s/pkWkD1VhMxhZPRrybLcQjA)