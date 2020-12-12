### 最小活跃调用策略
最小活跃调用策略：指的是当请求调用来临，有多个实例提供服务的时候，选择其中被调用活跃次数最少的实例来提供服务。通俗一点讲就是，当前有 3 个实例在提供服务，A 当前被 2 个服务调用，B 当前被 3 个服务调用，C 当前被 1 个服务调用，一个新的调用请求过来，会选择调用到 C 实例。

**最小活跃值怎么计算呢？**

在下面 ActiveLimitFilter 代码中可以看到，是通过在调用方法前将值 + 1，调用方法完成后将值 -1。

Dubbo 中实现该策略的代码是：LeastActiveLoadBalance。它分为 3 种情况。
- 当只有一个最小活跃的实例时，则返回这个唯一的实例
- 当有多个最小活跃的实例且每个实例的权重相同时，则通过随机选择其中的一个实例
- 当有多个最小活跃的实例且权重不一时，则通过产生一个随机数，范围为 [0, totalWeight - 1]，返回该随机数所在的实例。可以参考随机策略的权重不一情况
![image](http://assets.processon.com/chart_image/5fd4140963768906e6d7639b.png)

### 最小活跃调用策略的优缺点
优点：能动态的根据当前服务的调用情况，选择最小被调用的实例，调用越慢的机器，会接收到更少的请求

缺点：。。。。。。

###  LeastActiveLoadBalance 源码

```
public class LeastActiveLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "leastactive";

    private final Random random = new Random();

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        // 服务实例总数
        int length = invokers.size(); // Number of invokers
        // 最小活跃值
        int leastActive = -1; // The least active value of all invokers
        // 最小活跃值的实例数量
        int leastCount = 0; // The number of invokers having the same least active value (leastActive)
        // 存储所有最小活跃值的实例
        int[] leastIndexs = new int[length]; // The index of invokers having the same least active value (leastActive)
        // 总权重值（只记录最小活跃值的实例的权重）
        int totalWeight = 0; // The sum of weights
        // 记录第一个最小活跃值的实例的权重
        int firstWeight = 0; // Initial value, used for comparision
        // 所有实例的权重是否相同
        boolean sameWeight = true; // Every invoker has the same weight value?
        // 遍历所有实例
        for (int i = 0; i < length; i++) {
            Invoker<T> invoker = invokers.get(i);
            // 获取该实例的活跃值
            int active = RpcStatus.getStatus(invoker.getUrl(), invocation.getMethodName()).getActive(); // Active number
            // 获取该实例的权重值
            int weight = invoker.getUrl().getMethodParameter(invocation.getMethodName(), Constants.WEIGHT_KEY, Constants.DEFAULT_WEIGHT); // Weight
            if (leastActive == -1 || active < leastActive) { // Restart, when find a invoker having smaller least active value.
                // 记录最小的活跃值
                leastActive = active; // Record the current least active value
                // 重置记录
                leastCount = 1; // Reset leastCount, count again based on current leastCount
                leastIndexs[0] = i; // Reset
                totalWeight = weight; // Reset
                firstWeight = weight; // Record the weight the first invoker
                sameWeight = true; // Reset, every invoker has the same weight value?
            } else if (active == leastActive) { // If current invoker's active value equals with leaseActive, then accumulating.
                // 增加多一个最小活跃值
                // 记录该实例的下标
                leastIndexs[leastCount++] = i; // Record index number of this invoker
                // 统计总权重值
                totalWeight += weight; // Add this invoker's weight to totalWeight.
                // If every invoker has the same weight?
                if (sameWeight && i > 0
                        && weight != firstWeight) {
                    // 同是最小活跃值的实例中权重不一
                    sameWeight = false;
                }
            }
        }

        // 如果只有一个最小活跃值的实例，则返回该实例
        // assert(leastCount > 0)
        if (leastCount == 1) {
            // If we got exactly one invoker having the least active value, return this invoker directly.
            return invokers.get(leastIndexs[0]);
        }

        // 最小活跃值的实例中的权重不一
        if (!sameWeight && totalWeight > 0) {
            // If (not every invoker has the same weight & at least one invoker's weight>0), select randomly based on totalWeight.
            // 依据总权重值产生随机数，返回该随机数所在的实例
            int offsetWeight = random.nextInt(totalWeight);
            // Return a invoker based on the random value.
            // 返回随机的最小活跃实例
            for (int i = 0; i < leastCount; i++) {
                int leastIndex = leastIndexs[i];
                offsetWeight -= getWeight(invokers.get(leastIndex), invocation);
                if (offsetWeight <= 0)
                    return invokers.get(leastIndex);
            }
        }
        // 所有最小活跃值的实例都是相同的权重，则随机选择一个实例
        // If all invokers have the same weight value or totalWeight=0, return evenly.
        return invokers.get(leastIndexs[random.nextInt(leastCount)]);
    }
```
权重计算逻辑

```
    // 共用
    protected int getWeight(Invoker<?> invoker, Invocation invocation) {
        // 从 url 中获取权重 weight 配置值
        int weight = invoker.getUrl().getMethodParameter(invocation.getMethodName(), Constants.WEIGHT_KEY, Constants.DEFAULT_WEIGHT);
        if (weight > 0) {
            // 获取服务提供者启动时间戳
            long timestamp = invoker.getUrl().getParameter(Constants.REMOTE_TIMESTAMP_KEY, 0L);
            if (timestamp > 0L) {
                // 计算服务提供者运行时长
                int uptime = (int) (System.currentTimeMillis() - timestamp);
                // 获取服务预热时间，默认为10分钟
                int warmup = invoker.getUrl().getParameter(Constants.WARMUP_KEY, Constants.DEFAULT_WARMUP);
                // 如果服务运行时间小于预热时间，则重新计算服务权重，即降权
                if (uptime > 0 && uptime < warmup) {
                    // 重新计算服务权重
                    weight = calculateWarmupWeight(uptime, warmup, weight);
                }
            }
        }
        return weight;
    }
```


启动最小活跃策略时需要额外配置 filter = "activelimit"，如下所示
```
<dubbo:service interface="service.AbcService" ref="abcService" loadbalance="leastactive" filter="activelimit"/>
```
所以我们再看一下 ActiveLimitFilter 源码

```
@Activate(group = Constants.CONSUMER, value = Constants.ACTIVES_KEY)
public class ActiveLimitFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        URL url = invoker.getUrl();
        String methodName = invocation.getMethodName();
        // 获取当前被调用服务配置的最大并发数
        int max = invoker.getUrl().getMethodParameter(methodName, Constants.ACTIVES_KEY, 0);
        RpcStatus count = RpcStatus.getStatus(invoker.getUrl(), invocation.getMethodName());
        if (max > 0) {
            long timeout = invoker.getUrl().getMethodParameter(invocation.getMethodName(), Constants.TIMEOUT_KEY, 0);
            long start = System.currentTimeMillis();
            long remain = timeout;
            int active = count.getActive();
            if (active >= max) {
                // 当前并发数达到配置的最大并发数，则等待
                synchronized (count) {
                    while ((active = count.getActive()) >= max) {
                        try {
                            count.wait(remain);
                        } catch (InterruptedException e) {
                        }
                        long elapsed = System.currentTimeMillis() - start;
                        remain = timeout - elapsed;
                        if (remain <= 0) {
                            throw new RpcException();
                        }
                    }
                }
            }
        }
        try {
            long begin = System.currentTimeMillis();
            // 每一次调用前活跃数 +1
            RpcStatus.beginCount(url, methodName);
            try {
                Result result = invoker.invoke(invocation);
                // 调用后活跃数 -1
                RpcStatus.endCount(url, methodName, System.currentTimeMillis() - begin, true);
                return result;
            } catch (RuntimeException t) {
                RpcStatus.endCount(url, methodName, System.currentTimeMillis() - begin, false);
                throw t;
            }
        } finally {
            if (max > 0) {
                // 完成调用则通知其他阻塞着的线程
                synchronized (count) {
                    count.notify();
                }
            }
        }
    }
}
```
