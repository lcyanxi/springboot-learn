##### InternalThreadLocal

**能干啥用？**
在说 ThreadLocal 和 InternalThreadLocal 之前，还是先讲讲它们是干啥用的吧。

InternalThreadLocal 是 ThreadLocal 的增强版，所以他们的用途都是一样的，一言蔽之就是：**传递信息**。

你想象你有一个场景，调用链路非常的长。当你在其中某个环节中查询到了一个数据后，最后的一个节点需要使用一下。这个时候你怎么办？你是在每个接口的入参中都加上这个参数，传递进去，然后只有最后一个节点用吗？

可以实现，但是不太优雅。

你再想想一个场景，你有一个和业务没有一毛钱关系的参数，**比如traceId,纯粹是为了做日志追踪用**。

你加一个和业务无关的参数一路透传干啥玩意？

通常我们的做法是放在 ThreadLocal 里面，作为一个全局参数，在当前线程中的任何一个地方都可以直接读取。当然，如果你有修改需求也是可以的，视需求而定。

绝大部分的情况下，**ThreadLocal是适用于读多写少的场景中**。

##### Dubbo 使用RpcContext透传上下文参数
RpcContext 这个对象里面维护了两个 InternalThreadLocal，分别是存放 local 和 server 的上下文。也就是我们说的增强版的 ThreadLocal

```
/**
 * use internal thread local to improve performance
 */
private static final InternalThreadLocal<RpcContext> LOCAL = new InternalThreadLocal<RpcContext>() {
    @Override
    protected RpcContext initialValue() {
        return new RpcContext();
    }
};
private static final InternalThreadLocal<RpcContext> SERVER_LOCAL = new InternalThreadLocal<RpcContext>() {
    @Override
    protected RpcContext initialValue() {
        return new RpcContext();
    }
};
```
作为一个 Dubbo应用，它既可能是发起请求的消费者，也可能是接收请求的提供者。

每一次发起或者收到RPC调用的时候，上下文信息都会发生变化。比如说：A 调用 B，B 调用 C。这个时候 B 既是消费者也是提供者。

那么当 A 调用 B，B 还是没调用 C 之前，RpcContext 里面保存的是 A 调用 B 的上下文信息。当 B 开始调用 C 了，说明A到B之前的调用已经完成了，那么之前的上下文信息就应该清除掉。

这时 RpcContext 里面保存的应该是 B 调用 C 的上下文信息。否则会出现上下文污染的情况。而这个上下文信息里面的一部分就是通过InternalThreadLocal存放和传递的，是 ContextFilter 这个拦截器维护的。

ThreadLocal 在 Dubbo 里面的一个应用就是这样。当然，还有很多很多其他的开源框架都使用了 ThreadLocal 。可以说使用频率非常的高。

> InternalThreadLocal 到底比 ThreadLocal 强在什么地方呢？

InternalThreadLocal 是 ThreadLocal 的一个变种，当配合 InternalThread 使用时，具有比普通 Thread **更高的访问性能**。

- InternalThread的内部使用的**InternalThreadLocalMap**是**数组**，通过下标定位，非常的快。如果遇得扩容，直接数组扩大一倍，完事。
- 而 ThreadLocal 的内部使用的是 **hashCode** 去获取值，多了一步计算的过程，而且用 hashCode 必然会遇到 hash 冲突的场景，ThreadLocal 还得去解决 hash 冲突，如果遇到扩容，扩容之后还得 rehash ,这可不得慢吗？

数据结构都不一样了，这其实就是这两个类的本质区别，也是 **InternalThread 的性能在 Dubbo 的这个场景中比 ThreadLocal 好的根本原因**。

而 InternalThread 这个设计思想是从 Netty 的 FastThreadLocal 中学来的。

本文主要聊聊 InternalThread，但是我希望的是大家能学到这个类的思想，而不是用法。

首先，我们先搞个测试类：

```
public class InternalThreadLocalDemo {

    private static InternalThreadLocal<Integer> internalThreadLocal_0 = new InternalThreadLocal<>();

    public static void main(String[] args) {
        new InternalThread(() -> {
            for (int i = 0; i < 5; i++) {
                internalThreadLocal_0.set(i);
                Integer value = internalThreadLocal_0.get();
                System.out.println(Thread.currentThread().getName() + ":" + value);
            }
        }, "internalThread_have_set").start();

        new InternalThread(() -> {
            for (int i = 0; i < 5; i++) {
                Integer value = internalThreadLocal_0.get();
                System.out.println(Thread.currentThread().getName() + ":" + value);
            }
        }, "internalThread_no_set").start();
    }
}
```
结果：

```
internalThread_have_set:0
internalThread_have_set:1
internalThread_no_set:null
internalThread_no_set:null
internalThread_no_set:null
internalThread_no_set:null
internalThread_no_set:null
internalThread_have_set:2
internalThread_have_set:3
internalThread_have_set:4
```
##### 扩容是怎么扩的？

```
private void expandIndexedVariableTableAndSet(int index, Object value) {
    Object[] oldArray = indexedVariables;
    final int oldCapacity = oldArray.length;
    int newCapacity = index;
    newCapacity |= newCapacity >>> 1;
    newCapacity |= newCapacity >>> 2;
    newCapacity |= newCapacity >>> 4;
    newCapacity |= newCapacity >>> 8;
    newCapacity |= newCapacity >>> 16;
    newCapacity++;

    Object[] newArray = Arrays.copyOf(oldArray, newCapacity);
    Arrays.fill(newArray, oldCapacity, newArray.length, UNSET);
    newArray[index] = value;
    indexedVariables = newArray;
}
```
和 HashMap 里面的位运算异曲同工。

在 InternalThreadLocalMap 中扩容就是变成原来大小的 2 倍。从 32 到 64，从 64 到 128 这样。

扩容完成之后把原数组里面的值拷贝到新的数组里面去。然后剩下的部分用UNSET填充。最后把我们传进来的 value 放到指定位置上

##### 数组下标是怎么来的？

**初始化index：**

```
 private static final AtomicInteger NEXT_INDEX = new AtomicInteger();
 
 int index = NEXT_INDEX.getAndIncrement();
 
 // 产生index
public static int nextVariableIndex() {
    int index = NEXT_INDEX.getAndIncrement();
    if (index < 0) {
        NEXT_INDEX.decrementAndGet();
        throw new IllegalStateException("Too many thread-local indexed variables");
    }
    return index;
}
```


**数据填充到数组**

```
public boolean setIndexedVariable(int index, Object value) {
    Object[] lookup = indexedVariables;
    if (index < lookup.length) {
        Object oldValue = lookup[index];
        lookup[index] = value;
        return oldValue == UNSET;
    } else {
        expandIndexedVariableTableAndSet(index, value);
        return true;
    }
}
```

这个 index 本质上是一个 AtomicInteger。index 每次都是加一，对应的是 InternalThreadLocalMap 里的数组下标。



##### 应用场景一： 在provider端要获取每个调用者的应用名称

**consumer端**

```
@Slf4j
@Activate(group = Constants.CONSUMER)
public class LogTraceConsumerFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        //手动设置consumer的应用名进attachment
        String application = invoker.getUrl().getParameter(Constants.APPLICATION);
        if (application != null) {
            RpcContext.getContext().setAttachment(Constants.APPLICATION, application);
        }

        Result result = null;
        String serverIp = null;
        long startTime = System.currentTimeMillis();
        try {
            result = invoker.invoke(invocation);
            serverIp = RpcContext.getContext().getRemoteHost();//这次返回结果是哪个ip
            return result;
        } finally {
            Throwable throwable = (result == null) ? null : result.getException();
            Object resultObj = (result == null) ? null : result.getValue();
            long costTime = System.currentTimeMillis() - startTime;
            log.info("[TRACE] Call [{}], [{}].{}()] param:[{}], return:[{}], exception:[{}], cost:[{} ms]!",
                    serverIp, invoker.getInterface(), invocation.getMethodName(), invocation.getArguments(), resultObj, throwable, costTime);
        }
    }
}
```

**provider端**

```
@Slf4j
@Activate(group = Constants.PROVIDER)
public class LogTraceProviderFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        //上游如果手动设置了consumer的应用名进attachment，则取出来打印
        String clientIp = RpcContext.getContext().getRemoteHost();//这次请求来自哪个ip
        String application = RpcContext.getContext().getAttachment(Constants.APPLICATION);
        String from = clientIp;
        if (!StringUtils.isEmpty(application)) {
            from = application + "(" + clientIp + ")";
        }
        log.warn("[Trace]From [{}], [{}].[{}]() param:[{}]",
                from, invoker.getInterface(), invocation.getMethodName(), invocation.getArguments());
        return invoker.invoke(invocation);
    }
}
```
**Filter 文件中配置启用**

```
logTraceProviderFilter=com.lcyanxi.dubboFilter.LogTraceProviderFilter

logTraceConsumerFilter=com.lcyanxi.dubboFilter.LogTraceConsumerFilter
```
其实traceId的传递跟这个场景一样，都是利用RpcContext隐式传参

[参考博客](https://zhuanlan.zhihu.com/p/266744246)