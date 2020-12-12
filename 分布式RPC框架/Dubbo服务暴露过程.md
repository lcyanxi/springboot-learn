### 服务暴露全流程
服务暴露的过程起始于**Spring IOC容器刷新完成**之时，具体的流程就是根据配置得到 URL，再利用 **Dubbo SPI 机制** [参考Dubbo SPI与JDK SPI实现原理分析](https://note.youdao.com/web/#/file/WEB43c43bdd28350654d4989b250a073bb3/markdown/WEBe01524bf570539b5e44feb641af475f0/) ，根据URL的参数选择对应的实现类，实现扩展。

通过 javassist 动态封装 ref (你写的服务实现类)，统一暴露出 Invoker 使得调用方便，屏蔽底层实现细节，然后封装成 exporter 存储起来，等待消费者的调用，并且会将 URL 注册到注册中心，使得消费者可以获取服务提供者的信息

从代码的流程来看大致可以分为三个步骤:
- 第一步是**检测配置**，如果有些配置空的话会默认创建，并且组装成 URL 。
- 第二步是**暴露服务**，包括暴露到本地的服务和远程的服务。
- 第三步是**注册服务至注册中心**。

![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1Fpz82arDLk6S32wLdibQBnsiblLoPSLLF0HEOSWSJU5JCpIjxMxz9DAHXWAs1K7jf52TOib6M8mU26feA/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

从对象构建转换的角度看可以分为两个步骤。
- 第一步是将服务实现类转成 Invoker。
- 第二部是将 Invoker 通过具体的协议转换成 Exporter。
![image](http://assets.processon.com/chart_image/5f182d4f5653bb7fd24a6868.png)

### 服务暴露源码分析
接下来我们进入源码分析阶段，从上面配置解析的截图标红了的地方可以看到 service 标签其实就是对应 ServiceBean，我们看下它的定义。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1Fpz82arDLk6S32wLdibQBnsibl62cMFrW2D8BMs1AaYGBkia8JUyPkial6icBzUKcyqbcdiczkc555MUFr3Q/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

这里涉及到 Spring相关内容了，可以看到它**实现了 ApplicationListener<ContextRefreshedEvent>**，这样就会在 S**pring IOC容器刷新完成**后调用**onApplicationEvent**方法，而这个方法里面做的就是服务暴露，这就是服务暴露的启动点。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1Fpz82arDLk6S32wLdibQBnsiblNJtPhKicUXaLKJ03BheoEYicA0wgXn1K5kHkqsTibc8N2oHZuVVNpqJbg/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)
可以看到，如果不是延迟暴露、并且还没暴露过、并且支持暴露的话就执行 export 方法，而 export 最终会调用父类的 export 方法，我们来看看。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1Fpz82arDLk6S32wLdibQBnsibloX8C18h4ZibrsPjib9hMfk2yKvXsF9KUvJD17A8Zlo1qFWcFlQMPEBHw/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)
主要就是检查了一下配置，确认需要暴露的话就暴露服务， doExport 这个方法很长，不过都是一些检测配置的过程，虽说不可或缺不过不是我们关注的重点，我们重点关注里面的 doExportUrls 方法。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1Fpz82arDLk6S32wLdibQBnsiblSe6uPbPIIt6Bnj4iboF1VFFtYTfr7mbic88kvULSnUhficnyW1S3Fh18g/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

可以看到 Dubbo 支持多注册中心，并且支持多个协议，一个服务如果有多个协议那么就都需要暴露，比如同时支持 dubbo 协议和 hessian 协议，那么需要将这个服务用两种协议分别向多个注册中心（如果有多个的话）暴露注册。

loadRegistries 方法我就不做分析了，就是根据配置组装成注册中心相关的 URL ，我就给大家看下拼接成的 URL的样子。

```
registry://127.0.0.1:2181/com.alibaba.dubbo.registry.RegistryService?application=demo-provider&dubbo=2.0.2&pid=7960&qos.port=22222&registry=zookeeper&timestamp=1598624821286
```
我们接下来关注的重点在 doExportUrlsFor1Protocol 方法中，这个方法挺长的，我会截取大致的部分来展示核心的步骤。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1Fpz82arDLk6S32wLdibQBnsiblboEOjTSbsrMN5FSe8CH2ia34DCzko234LdKozmuZpWiaCibPaxAxzf5bA/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)
此时构建出来的 URL 长这样，可以看到走得是 dubbo 协议。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1Fpz82arDLk6S32wLdibQBnsiblwib6qxETiawmbB6UxlNADKP78U8RVkLHYkgL0AEkHTfKobgvElNMia8HQ/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)
然后就是要根据 URL 来进行服务暴露了，我们再来看下代码，这段代码我就直接截图了，因为需要断点的解释。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1Fpz82arDLk6S32wLdibQBnsiblltMSLQ5oU21tDNHicUgY90MqCyskTARWcic9NyT8681EQQOLE28dbCRw/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)
### 本地暴露
我们再来看一下 exportLocal 方法，这个方法是本地暴露，走的是 injvm 协议，可以看到它搞了个新的 URL 修改了协议。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1Fpz82arDLk6S32wLdibQBnsiblOCpmkGbRxl8ZX5dJ3FvJbeeiaQ0PVibPM8eRJlTtms6NqeOJvrvQkZzg/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)
我们来看一下这个 URL，可以看到协议已经变成了 injvm。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1Fpz82arDLk6S32wLdibQBnsiblAMtkoZ6C2OU1geJwFPj6RT0Zke8wpibXErbGXQqR1dLoXH33zndXAPw/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

从图中可以看到实际上就是具体实现类层层封装， invoker 其实是由 Javassist 创建的，具体创建过程 proxyFactory.getInvoker 就不做分析了，对 Javassist 有兴趣的同学自行去了解，之后可能会写一篇，至于 dubbo 为什么用 javassist 而不用 jdk 动态代理是因为 javassist 快。

##### 为什么要封装成 invoker？

至于为什么要封装成 invoker 其实就是**想屏蔽调用的细节，统一暴露出一个可执行体**，这样调用者简单的使用它，向它发起 invoke 调用，它有可能是一个本地的实现，也可能是一个远程的实现，也可能一个集群实现。
**为什么要搞个本地暴露呢？**

因为可能存在同一个JVM内部引用自身服务的情况，因此**暴露的本地服务在内部调用的时候可以直接消费同一个 JVM 的服务避免了网络间的通信**。

### 远程暴露
也和本地暴露一样，需要封装成 Invoker ，不过这里相对而言比较复杂一些，我们先来看下  registryURL.addParameterAndEncoded(Constants.EXPORT_KEY, url.toFullString()) 将 URL 拼接成什么样子。

```
registry://127.0.0.1:2181/com.alibaba.dubbo.registry.RegistryService?application=demo-provider&dubbo=2.0.2&export=dubbo://192.168.1.17:20880/com.alibaba.dubbo.demo.DemoService....
```
因为很长，我就不截全了，可以看到走 registry 协议，然后参数里又有 export=dubbo://，这个走 dubbo 协议，所以我们可以得知会先通过 registry 协议找到  RegistryProtocol 进行 export，并且在此方法里面还会根据 export 字段得到值然后执行 DubboProtocol 的 export 方法。

现在我们把目光聚焦到 RegistryProtocol#export 方法上，我们先过一遍整体的流程，然后再进入 doLocalExport 的解析。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1Fpz82arDLk6S32wLdibQBnsiblnYxKNq333alcwsZevKfSNtXWKnDRz6glgUbjBf21icJfMKLC8kLXbUA/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

可以看到这一步主要是将上面的 export=dubbo://... 先转换成 exporter ，然后获取注册中心的相关配置，如果需要注册则向注册中心注册，并且在 ProviderConsumerRegTable 这个表格中记录服务提供者，其实就是往一个 ConcurrentHashMap 中将塞入 invoker，key 就是服务接口全限定名，value 是一个 set，set 里面会存包装过的 invoker 。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1Fpz82arDLk6S32wLdibQBnsiblRlb5hOLHW0krzeBictdYNZyiaMEuUvKEryHIPRHA019KZhBYAu06g1Ow/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)
我们再把目光聚焦到  doLocalExport 方法内部

![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1Fpz82arDLk6S32wLdibQBnsibldYyndfEjR7cHSYVFQn0UpLc48eMTJ9icfDut7yACQqTPfX9upp4a9Wg/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

这个方法没什么难度，主要就是根据URL上 Dubbo 协议暴露出 exporter，接下来就看下 DubboProtocol#export 方法。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1Fpz82arDLk6S32wLdibQBnsibljEBUibbiatNwW4WBwOT7tcSyob38DHKSttxVL6ys0AUVSuibq69edN0qw/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

可以看到这里的关键其实就是打开 Server ，RPC 肯定需要远程调用，这里我们用的是 NettyServer 来监听服务。
![image](https://mmbiz.qpic.cn/mmbiz_jpg/uChmeeX1Fpz82arDLk6S32wLdibQBnsiblc4NugZVXZvn4k7n2IrAnnW5TgWwxNGibmiaKAhy57AwicJMIYuJJts5hg/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

再下面我就不跟了，我总结一下 Dubbo 协议的 export 主要就是根据 URL 构建出 key（例如有分组、接口名端口等等），然后 key 和 invoker 关联，关联之后存储到 DubboProtocol 的 exporterMap 中，然后如果是服务初次暴露则会创建监听服务器，默认是 NettyServer，并且会初始化各种 Handler 比如心跳啊、编解码等等。


[原文地址](https://mp.weixin.qq.com/s/ISiN06QynyE2pPtX3cGQ9w)