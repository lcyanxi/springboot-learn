### 调用流程-客户端源码分析
![image](http://assets.processon.com/chart_image/5f1fe74e0791291b996c9a37.png)
客户端调用一下代码。

```
String hello = demoService.sayHello("world"); 
```
调用具体的接口会调用生成的代理类，而代理类会生成一个 **RpcInvocation** 对象调用 **MockClusterInvoker#invoke**方法。

此时生成的 RpcInvocation如下图所示，包含方法名、参数类和参数值。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpzRZ8zcIIAfAouFVhHEibwPf6Kcd76JNERIqzpG5d3bMxOA9bQxBbnEFtmVyuiaIcnQFM4SPCZL4BLg/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

然后我们再来看一下MockClusterInvoker#invoke 代码。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpzRZ8zcIIAfAouFVhHEibwPfybeoLLwkibBtxrnaiapj4luaRxOtyRiaGAmeecOBKzros0U2MDDLyr50g/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

可以看到就是判断配置里面有没有配置 mock， mock 的话就不展开分析了，我们来看看 this.invoker.invoke 的实现，实际上会调用 AbstractClusterInvoker#invoker 。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpzRZ8zcIIAfAouFVhHEibwPfJZWj5D3phH6kXnoaIm7kQW4UfasJZOJaq3ENBiaCOqg69zfJ4QIKbTg/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

##### 模板方法
这其实就是很常见的设计模式之一，模板方法。如果你经常看源码的话你知道这个设计模式真的是太常见的。

**模板方法其实就是在抽象类中定好代码的执行骨架，然后将具体的实现延迟到子类中，由子类来自定义个性化实现**，也就是说可以在不改变整体执行步骤的情况下修改步骤里面的实现，减少了重复的代码，也利于扩展，符合开闭原则。

在代码中就是那个**doInvoke**由子类来实现，上面的一些步骤都是每个子类都要走的，所以抽到抽象类中。

##### 路由和负载均衡得到 Invoker
我们再来看那个**list(invocation)**，其实就是通过方法名找Invoker，然后服务的路由过滤一波，也有再造一个 MockInvoker 的。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpzRZ8zcIIAfAouFVhHEibwPfTGaTbHwn6gx0uDzyuDjReHadjSOnv4zB7Aq0iacichJSMM9EicutiaUMyQ/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

然后带着这些 Invoker 再进行一波 loadbalance 的挑选，得到一个 Invoker，我们默认使用的是 FailoverClusterInvoker，也就是失败自动切换的容错方式，其实关于路由、集群、负载均衡是独立的模块，如果展开讲的话还是有很多内容的，所以需要另起一篇讲，这篇文章就把它们先作为黑盒使用。

稍微总结一下就是 **FailoverClusterInvoker 拿到 Directory 返回的 Invoker 列表，并且经过路由之后，它会让 LoadBalance 从 Invoker 列表中选择一个 Invoker**。

最后**FailoverClusterInvoker**会将参数传给选择出的那个 Invoker 实例的 invoke 方法，进行真正的远程调用，我们来简单的看下 FailoverClusterInvoker#doInvoke，为了突出重点我删除了很多方法。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpzRZ8zcIIAfAouFVhHEibwPfJx8wic9NAMicIJBP36Y2QGsDq1FpGUhOncvQqjGicLZ07WPeqfUkJjmZQ/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

发起调用的这个 invoke 又是调用抽象类中的 invoke 然后再调用子类的 doInvoker，抽象类中的方法很简单我就不展示了，影响不大，直接看子类 DubboInvoker 的 doInvoke 方法。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpzRZ8zcIIAfAouFVhHEibwPfKly3icrRGkpNyLZYSEBAqcKWr2IJOBribh2zuy69AwnsdlgHpiaUViahibQ/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)
##### 调用的三种方式
从上面的代码可以看到调用一共分为三种，分别是 oneway、异步、同步。
- **oneway**：还是很常见的，就是当你不关心你的请求是否发送成功的情况下，就用 oneway 的方式发送，这种方式消耗最小，啥都不用记，啥都不用管。
- **异步调用**：其实Dubbo天然就是异步的，可以看到 client发送请求之后会得到一个ResponseFuture，然后把future包装一下塞到上下文中，这样用户就可以从上下文中拿到这个future，然后用户可以做了一波操作之后再调用 future.get 等待结果。
- **同步调用**：这是我们最常用的，也就是 Dubbo 框架帮助我们异步转同步了，从代码可以看到在 Dubbo源码中就调用了future.get，所以给用户的感觉就是我调用了这个接口的方法之后就阻塞住了，必须要等待结果到了之后才能返回，所以就是同步的。

可以看到 Dubbo本质上就是异步的，为什么有同步就是因为框架帮我们转了一下，而**同步和异步的区别其实就是future.get在用户代码被调用还是在框架代码被调用**。

再回到源码中来，currentClient.request源码如下就是组装 request然后构造一个future然后调用 NettyClient 发送请求。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpzRZ8zcIIAfAouFVhHEibwPfdazVqrKvKOPhDNx2vXusfRqM22fmyVybuLcM1siaxo5wrGFicsJHgckA/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

我们再来看一下DefaultFuture的内部，**你有没有想过一个问题，因为是异步，那么这个 future 保存了之后，等响应回来了如何找到对应的 future 呢**？

这里就揭秘了！就是利用一个唯一 ID。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpzRZ8zcIIAfAouFVhHEibwPf2gvdic7udcHgrjmrHF0Xnc7C8fcIhQs8oaeCPiaIdkchRrmWABbL5tFg/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

可以看到 Request 会生成一个全局唯一ID，然后 future 内部会将自己和ID存储到一个ConcurrentHashMap。这个ID发送到服务端之后，服务端也会把这个 ID 返回来，这样通过这个ID再去ConcurrentHashMap 里面就可以找到对应的future，这样整个连接就正确且完整了！

我们再来看看最终接受到响应的代码，应该就很清晰了。

先看下一个响应的 message 的样子：

```
Response [id=14, version=null, status=20, event=false, error=null, result=RpcResult [result=Hello world, response from provider: 192.168.1.17:20881, exception=null]]
```
看到这个 ID了吧，最终会调用**DefaultFuture#received**的方法。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1FpzRZ8zcIIAfAouFVhHEibwPfDMPZ0FKIicD9RxNmVdRg54xUuMcUfmicumTfcYlZpq0C2Mk5qUbvQmWQ/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)