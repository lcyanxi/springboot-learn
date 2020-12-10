##### 为什么需要SPI机制，或者说SPI的作用是什么？
**在 RPC 框架里面,怎么支持插件化架构的呢？**

为了功能的可扩展性和可插拔性，功能接口与功能的实现分离,供接口的默认实现,这就是SPI机制的原理。

### Java SPI 
Java SPI 就是这样做的，约定在 Classpath 下的 META-INF/services/ 目录里创建一个**以服务接口命名的文件**，然后文件里面记录的是此jar包提供的**具体实现类的全限定名**

这样当我们引用了某个 jar 包的时候就可以去找这个 jar 包的 META-INF/services/ 目录，再根据接口名找到文件，然后读取文件里面的内容去进行实现类的加载与实例化。

##### Java SPI 示例
```
// 接口
public interface IJDKSpiService {
    void spiRegisterName();
}

//实现类1
@Slf4j
public class JDKSpiServiceImpl implements IJDKSpiService {
    @Override
    public void spiRegisterName() {
        log.info("testSpiServiceImpl is implements method start");
    }
}
//实现类2
@Slf4j
public class JDKSpiOtherServiceImpl implements IJDKSpiService {
    @Override
    public void spiRegisterName() {
        log.info("testSpiOtherServiceImpl is implements method start");
    }
}

public class JDKSpiDemo {
    public static void main(String[] args) {
        ServiceLoader<IJDKSpiService> load = ServiceLoader.load(IJDKSpiService.class);
        for (IJDKSpiService next : load) {
            next.spiRegisterName();
        }
    }
}
```
然后我在 META-INF/services/ 目录下建了个以接口全限定名命名的文件，内容如下

```
com.lcyanxi.serviceImpl.JDKSpiOtherServiceImpl
com.lcyanxi.serviceImpl.JDKSpiServiceImpl
```
##### 运行结果
```
testSpiOtherServiceImpl is implements method start
testSpiServiceImpl is implements method start
```
### Java SPI 源码分析
Dubbo 并没有用 Java 实现的 SPI，而是自定义 SPI，那肯定是 Java SPI有什么不方便的地方或者劣势。

从上面我的示例中可以看到**ServiceLoader.load**()其实就是 Java SPI 入口，我们来看看到底做了什么操作。
![image](https://mmbiz.qpic.cn/mmbiz_png/uChmeeX1Fpw2oMzXxojtaJ9TARZP6z3Rah6FcLYsZhFoEnvrz21txibCaWgBb6XIJ8b3z54TcCiaVbgbUJGFSCNw/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

我用一句话概括一下，简单的说就是先找当前线程绑定的 ClassLoader，如果没有就用 SystemClassLoader，然后清除一下缓存，再创建一个 LazyIterator。

那现在重点就是 LazyIterator了，从上面代码可以看到我们调用了 hasNext() 来做实例循环，通过 next() 得到一个实例。而 LazyIterator 其实就是 Iterator 的实现类。我们来看看它到底干了啥。
![image](https://mmbiz.qpic.cn/mmbiz_png/uChmeeX1Fpw2oMzXxojtaJ9TARZP6z3RIcBsibpk93iaXMbU7q3qfTJPaQsk0tGsibfJIJ9G6siaFvYMag1YB8ElGA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

不管进入 if 分支还是 else 分支，重点都在我框出来的代码，接下来就进入重要时刻了！
![image](https://mmbiz.qpic.cn/mmbiz_png/uChmeeX1Fpw2oMzXxojtaJ9TARZP6z3R5P5X9U8ulB3ObfyeucEgQHwSFyd1lqic4U6pnUJmv6VoNBBYibUTdl4g/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

可以看到这个方法其实就是在约定好的地方找到接口对应的文件，然后加载文件并且解析文件里面的内容。

我们再来看一下 nextService()。
![image](https://mmbiz.qpic.cn/mmbiz_png/uChmeeX1Fpw2oMzXxojtaJ9TARZP6z3Rj3DdAwv5upibs2sFzBXuPz9IvMgicx4YXIP6w03CEzQZYyGBrwyYCAAw/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

**所以就是通过文件里填写的全限定名加载类，并且创建其实例放入缓存之后返回实例**。

整体的 Java SPI 的源码解析已经完毕，是不是很简单？**就是约定一个目录，根据接口名去那个目录找到文件，文件解析得到实现类的全限定名，然后循环加载实现类和创建其实例**。

我再用一张图来带大家过一遍。
![image](https://mmbiz.qpic.cn/mmbiz_png/uChmeeX1Fpw2oMzXxojtaJ9TARZP6z3RBYpGMZCDPcybNvpqIJWzKU7lNQIZtEEKUDiceusoODFBdLyjMibpUVyQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

##### Java SPI 哪里不好
**Java SPI 在查找扩展实现类的时候遍历 SPI 的配置文件并且将实现类全部实例化**，假设一个实现类初始化过程比较消耗资源且耗时，但是你的代码里面又用不上它，这就产生了资源的浪费。

所以说 **Java SPI 无法按需加载实现类**。

### Dubbo SPI
因此 Dubbo 就自己实现了一个 SPI，让我们想一下按需加载的话首先你得给个名字，**通过名字去文件里面找到对应的实现类全限定名然后加载实例化即可**。

Dubbo 就是这样设计的，配置文件里面存放的是键值对，我截一个 Cluster 的配置。
![image](https://mmbiz.qpic.cn/mmbiz_png/uChmeeX1Fpw2oMzXxojtaJ9TARZP6z3RO7HWiciaVFgsqsCsVXnB65TA8hqcOFEbU2DEicLmw7cpZSWym42BCEylQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

并且 Dubbo SPI **除了可以按需加载实现类之外，增加了 IOC 和 AOP 的特性，还有个自适应扩展机制**。

我们先来看一下 Dubbo 对配置文件目录的约定，不同于 Java SPI ，Dubbo 分为了三类目录。
- META-INF/services/ 目录：该目录下的 SPI 配置文件是为了用来兼容 Java SPI 。
- META-INF/dubbo/ 目录：该目录存放用户自定义的 SPI 配置文件。
- META-INF/dubbo/internal/ 目录：该目录存放 Dubbo 内部使用的 SPI 配置文件。

##### Dubbo SPI 简单实例
首先在  META-INF/dubbo 目录下按接口全限定名建立一个文件，内容如下：

```
dubboSpiOtherServiceImpl = com.lcyanxi.serviceImpl.DubboSpiOtherServiceImpl
dubboSpiServiceImpl = com.lcyanxi.serviceImpl.DubboSpiServiceImpl
```
接口和实现类

```
public interface DubboSpiDefault {
    String NAME = "dubboSpiServiceImpl";
}

// 接口
@SPI(DubboSpiDefault.NAME)
public interface IDubboSpiService {
    void dubboSpiRegisterName();
}

// 实现类1
@Slf4j
public class DubboSpiOtherServiceImpl implements IDubboSpiService {
    @Override
    public void dubboSpiRegisterName() {
        log.info("dubboSpiOtherServiceImpl is implements method start");
    }
}

//实现类2
@Slf4j
public class DubboSpiServiceImpl implements IDubboSpiService {
    @Override
    public void dubboSpiRegisterName() {
        log.info("dubboSpiServiceImpl is implements method start");
    }
}
```
测试类
```
@Slf4j
public class DubboSpiDemo {
    public static void main(String[] args) {
        ExtensionLoader<IDubboSpiService> extensionLoader = ExtensionLoader.getExtensionLoader(IDubboSpiService.class);

        log.info("dubboSpi enable support extension");
        // 可以支持的实现
        Set<String> supportedExtensions = extensionLoader.getSupportedExtensions();
        supportedExtensions.forEach(((s -> extensionLoader.getExtension(s).dubboSpiRegisterName())));

        log.info("dubboSpi default extension");
        // 默认的实现
        IDubboSpiService defaultExtension = extensionLoader.getDefaultExtension();
        defaultExtension.dubboSpiRegisterName();

        log.info("dubboSpi find by  extension name");
        // 获取指定实现
        IDubboSpiService otherServiceImpl = extensionLoader.getExtension("dubboSpiOtherServiceImpl");
        otherServiceImpl.dubboSpiRegisterName();

    }
}
```
结果：

```
dubboSpi enable support extension
dubboSpiOtherServiceImpl is implements method start
dubboSpiServiceImpl is implements method start

dubboSpi default extension
dubboSpiServiceImpl is implements method start

dubboSpi find by  extension name
dubboSpiOtherServiceImpl is implements method start
```
##### Dubbo 源码分析

从上面的示例代码我们知道 ExtensionLoader 好像就是重点，它是类似 Java SPI 中 ServiceLoader 的存在

我们可以看到大致流程就是先通过接口类找到一个 ExtensionLoader ，然后再通过 ExtensionLoader.getExtension(name) 得到指定名字的实现类实例

我们就先看下 getExtensionLoader() 做了什么
![image](https://mmbiz.qpic.cn/mmbiz_png/uChmeeX1Fpw2oMzXxojtaJ9TARZP6z3R0Azap6qhdHhU789h7ouUO9NQqb26zFcZ3EEaeOLTuWicsMIrUhFXk2g/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

很简单，做了一些判断然后从缓存里面找是否已经存在这个类型的 ExtensionLoader ，如果没有就新建一个塞入缓存。最后返回接口类对应的 ExtensionLoader 。

我们再来看一下 getExtension() 方法，从现象我们可以知道这个方法就是从类对应的 ExtensionLoader 中通过名字找到实例化完的实现类。

![image](https://mmbiz.qpic.cn/mmbiz_png/uChmeeX1Fpw2oMzXxojtaJ9TARZP6z3RAoVHhD3A5fWAnO3DxyHLKO3fvjqWMINb8kFwZ8g9PP8wUHNWft728Q/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

可以看到重点就是 createExtension()，我们再来看下这个方法干了啥。
![image](https://mmbiz.qpic.cn/mmbiz_png/uChmeeX1Fpw2oMzXxojtaJ9TARZP6z3RZjSkiaJCoPbxqeZNkkuzTlI5yzWjsJf6qxPYJ6HGU6f6U4bXXugyByA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

整体逻辑很清晰，**先找实现类，判断缓存是否有实例，没有就反射建个实例，然后执行 set 方法依赖注入。如果有找到包装类的话，再包一层**。

到这步为止我先画个图，大家理一理，还是很简单的
![image](https://mmbiz.qpic.cn/mmbiz_png/uChmeeX1Fpw2oMzXxojtaJ9TARZP6z3RXO6vQxwvic88MEAqmE1eicnxaplqR2O0XiaHD7jLicNFw6hvSqPapDGYdg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

##### Adaptive 注解 - 自适应扩展
在Dubbo中，很多拓展都是通过SPI机制进行加载的，比如Protocol、Cluster、LoadBalance等。这些扩展并非在框架启动阶段就被加载，而是在扩展方法被调用的时候，根据URL对象参数进行加载。那么，Dubbo就是通过自适应扩展机制来解决这个问题。

我们先来看一个场景，首先我们根据配置来进行 SPI 扩展的加载，但是我**不想在启动的时候让扩展被加载，我想根据请求时候的参数来动态选择对应的扩展**。

怎么做呢？

**Dubbo 通过一个代理机制实现了自适应扩展**，简单的说就是为你想扩展的接口生成一个代理类，可以通过JDK 或者 javassist 编译你生成的代理类代码，然后通过**反射**创建实例。

这个实例里面的实现会根据本来方法的请求参数得知需要的扩展类，然后通过 ExtensionLoader.getExtensionLoader(type.class).getExtension(从参数得来的name)，来获取真正的实例来调用。

![image](https://mmbiz.qpic.cn/mmbiz_png/uChmeeX1Fpw2oMzXxojtaJ9TARZP6z3R9RFZPrqr40btichibZUAZ0H229hmWGULOU1wYsc7ErzGwmThYsDDfRicw/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

这个注解就是自适应扩展相关的注解，可以修饰类和方法上，在修饰类的时候不会生成代理类，因为这个类就是代理类，修饰在方法上的时候会生成代理类。


##### WrapperClass - AOP

包装类是因为一个扩展接口可能有多个扩展实现类，而**这些扩展实现类会有一个相同的或者公共的逻辑**，如果每个实现类都写一遍代码就重复了，并且比较不好维护。

因此就搞了个包装类，Dubbo 里帮你自动包装，只需要某个扩展类的构造函数只有一个参数，并且是扩展接口类型，就会被判定为包装类，然后记录下来，用来包装别的实现类。

![image](https://mmbiz.qpic.cn/mmbiz_png/uChmeeX1Fpw2oMzXxojtaJ9TARZP6z3Rtsiax4JTXDQFmrdUJZ4eicOYfeCHYibX60vxGobhbkTZNo5ibAUUxV0Idw/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

##### injectExtension - IOC
直接看代码，很简单，就是查找 set 方法，根据参数找到依赖对象则注入。
![image](https://mmbiz.qpic.cn/mmbiz_png/uChmeeX1Fpw2oMzXxojtaJ9TARZP6z3R68OibCbjfiavE5zO5W73S4OiaElWh1PsC3wQ8qro4YM4gK79ItxcImNlw/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

##### Activate 注解
这个注解我就简单的说下，拿 Filter 举例，Filter 有很多实现类，在某些场景下需要其中的几个实现类，而某些场景下需要另外几个，而 Activate 注解就是标记这个用的。

它有三个属性，group 表示修饰在哪个端，是 provider 还是 consumer，value 表示在 URL参数中出现才会被激活，order 表示实现类的顺序。

### 总结
![image](https://mmbiz.qpic.cn/mmbiz_png/uChmeeX1Fpw2oMzXxojtaJ9TARZP6z3RgiaKYdlGaHTkMdgoLg7YaBb5uGB9DY3Uyyq0B9GGlh7agyRR67Tvoqg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

- Java SPI 会一次加载和实例化所有的实现类。
- 而 Dubbo SPI 则自己实现了 SPI，可以通过名字实例化指定的实现类，并且实现了 IOC 、AOP 与 自适应扩展 SPI 。

[丙哥的原文地址](https://mp.weixin.qq.com/s/gwWOsdQGEN0t2GJVMQQexw)
