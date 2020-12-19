> 当面试官问：“请讲一讲Spring中的循环依赖”的时候，我们到底该怎么回答？

**主要分下面几点**
- 什么是循环依赖？
- 什么情况下循环依赖可以被处理？
- Spring是如何解决的循环依赖？

同时本文希望纠正几个目前业界内经常出现的几个关于循环依赖的错误的说法

- 只有在setter方式注入的情况下，循环依赖才能解决（错）
- 三级缓存的目的是为了提高效率（错）

### 什么是循环依赖？

```
@Component
public class A {
    // A中注入了B
	@Autowired
	private B b;
}

@Component
public class B {
    // B中也注入了A
	@Autowired
	private A a;
}
```
当然，这是最常见的一种循环依赖，比较特殊的还有

```
// 自己依赖自己
@Component
public class A {
    // A中注入了A
	@Autowired
	private A a;
}
```
虽然体现形式不一样，但是实际上都是同一个问题----->循环依赖

### 什么情况下循环依赖可以被处理？
在回答这个问题之前首先要明确一点，Spring解决循环依赖是有前置条件的
- 出现循环依赖的Bean必须要是单例
- 依赖注入的方式不能全是构造器注入的方式（很多博客上说，只能解决setter方法的循环依赖，这是错误的）

其中第一点应该很好理解，第二点：不能全是构造器注入是什么意思呢？我们还是用代码说话

```
@Component
public class A {
//	@Autowired
//	private B b;
	public A(B b) {}
}

@Component
public class B {
//	@Autowired
//	private A a;
	public B(A a){}
}
```
在上面的例子中，A中注入B的方式是通过构造器，B中注入A的方式也是通过构造器，这个时候循环依赖是无法被解决，如果你的项目中有两个这样相互依赖的Bean，在启动时就会报出以下错误：

```
Error creating bean with name 'a': Requested bean is currently in creation: Is there an unresolvable circular reference?
```
为了测试循环依赖的解决情况跟注入方式的关系，我们做如下四种情况的测试

依赖情况 | 依赖注入方式 | 循环依赖是否被解决
---|--- |---
AB相互依赖 | 均采用setter方法注入 |  是
AB相互依赖 | 均采用构造器注入  |  否
AB相互依赖 | A中注入B的方式为setter方法，B中注入A的方式为构造器  |  是
AB相互依赖 | B中注入A的方式为setter方法，A中注入B的方式为构造器  | 否

从上面的测试结果我们可以看到，**不是只有在setter方法注入的情况下循环依赖才能被解决，即使存在构造器注入的场景下，循环依赖依然被可以被正常处理掉**。

那么到底是为什么呢？Spring到底是怎么处理的循环依赖呢？不要急，我们接着往下看

### Spring是如何解决的循环依赖？
关于循环依赖的解决方式应该要分两种情况来讨论
- 简单的循环依赖（没有AOP）
- 结合了AOP的循环依赖

##### 简单的循环依赖（没有AOP）
我们先来分析一个最简单的例子，就是上面提到的那个demo

```
@Component
public class A {
    // A中注入了B
	@Autowired
	private B b;
}

@Component
public class B {
    // B中也注入了A
	@Autowired
	private A a;
}
```
**我们先看看Spring bean的加载流程**

![image](http://assets.processon.com/chart_image/5fdbeff01e085304fb7dd5d0.png)

假设按照上面代码Class A 和 B，按照从A->B的顺序来实例化，Spring创建bean的过程主要可以分为三个阶段：
- 实例化，对应方法：createBeanInstance方法
- 属性注入，对应方法： populateBean方法
- 初始化，对应方法： initializeBean

所以执行顺序是先在这个类中的 AbstractBeanFactory 按调用链执行如下三个方法：

```
1、getBean("a")
2、doGetBean("a")
3、getSingleton("a") 
```
在调用getSingleton(a)方法，这个方法又会调用getSingleton(beanName,true)，所以才进入到下面这个方法：

```
//DefaultSingletonBeanRegistry
public Object getSingleton(String beanName) {
    return getSingleton(beanName, true);
}
```
##### getSingleton(beanName, true)

这是个重点方法，该方法实际上就是到缓存中尝试去获取Bean，整个缓存分为三级
- **singletonObjects**：一级缓存，存储的是所有创建好了的单例Bean
- **earlySingletonObjects**：完成实例化，但是还未进行属性注入及初始化的对象
- **singletonFactories**：提前暴露的一个单例工厂，二级缓存中存储的就是从这个工厂中获取到的对象

```
//DefaultSingletonBeanRegistry#getSingleton(java.lang.String, boolean)
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
    //一级缓存中获取-->完整bean
    Object singletonObject = this.singletonObjects.get(beanName);
    if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
        synchronized (this.singletonObjects) {
            //二级缓存中-->获取未属性注入的bean
            singletonObject = this.earlySingletonObjects.get(beanName);
            if (singletonObject == null && allowEarlyReference) {
                //三级缓存中-->获取工厂
                ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                if (singletonFactory != null) {
                    singletonObject = singletonFactory.getObject();
                    //三级缓存中的工厂getObject的对象-->放入二级缓存中
                    this.earlySingletonObjects.put(beanName, singletonObject);
                    this.singletonFactories.remove(beanName);
                }
            }
        }
    }
    return singletonObject;
}
```
因为是第一次创建，因此上面的三级缓存都未命中，此时会进入getSingleton的另外一个重载方法getSingleton(beanName, singletonFactory)。这里我们知道 singletonFactory 是需要等待createBean(beanName, mbd, args) 方法的返回，然后作为第二个输入参数给到下面 getSingleton 方法。

![image](https://upload-images.jianshu.io/upload_images/8926909-f12f9ac4e56dbcd6.png?imageMogr2/auto-orient/strip|imageView2/2/w/792/format/webp)

createBean 方法的返回将作为 getSingleton 的输入，然后会进入到下面这段代码中：

```
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {    
     // .......
    synchronized (this.singletonObjects) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            //省略...
            try {
                //入参的lambda会提供一个singletonFactory
               //调用createBean方法创建一个Bean后返回
                singletonObject = singletonFactory.getObject();
                newSingleton = true;
            }
            //省略...
            if (newSingleton) {
                //添加到一级缓存singletonObjects中
                addSingleton(beanName, singletonObject);
            }
        }
        return singletonObject;
    }
}
```
上面的代码主要实现了：将已经完全创建好了的单例Bean放入一级缓存中。在前面一步createBean()方法的创建实例过程中还有一个doCreateBean方法

**首先是创建实例对象：**

```
if (instanceWrapper == null) {
    // 创建对象
	instanceWrapper = createBeanInstance(beanName, mbd, args);
}
```
**然后将创建的对象添加到三级缓存，再进行属性注入**

![image](https://upload-images.jianshu.io/upload_images/8926909-1ef5872dc2c33110.png?imageMogr2/auto-orient/strip|imageView2/2/w/889/format/webp)

这个也就是在Bean实例化后，属性注入之前Spring将Bean包装成一个工厂添加进了三级缓存中，addSingletonFactory 对应源码如下：

```
// 这里传入的参数也是一个lambda表达式，() -> getEarlyBeanReference(beanName, mbd, bean)
protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
    synchronized (this.singletonObjects) {
        if (!this.singletonObjects.containsKey(beanName)) {
            // 添加到三级缓存中
            this.singletonFactories.put(beanName, singletonFactory);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }
}
```
那么getEarlyBeanReference方法又做了什么呢？进入源码看下：

```
protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
    Object exposedObject = bean;
    if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
        for (BeanPostProcessor bp : getBeanPostProcessors()) {
            if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
                SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor) bp;
                exposedObject = ibp.getEarlyBeanReference(exposedObject, beanName);
            }
        }
    }
    return exposedObject;
}
```
##### 非AOP的二级缓存

这个地方的BeanPostProcessor后置处理器，只在处理AOP的实例对象时才会发挥作用，如果不考虑AOP，代码就是：

```
protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
    Object exposedObject = bean;
    return exposedObject;
}
```

可见，对于非Aop实例对象，**这个工厂直接将实例化阶段创建的对象返回了**！


![image](http://assets.processon.com/chart_image/5fdd66877d9c0855ea75567e.png)

**现在整体来梳理一下**：
- 继续走A对象创建的流程，通过this.singletonFactories.put(beanName,singletonFactory)这个方法只是添加了一个工厂，通过这个工厂（ObjectFactory）的getObject方法可以得到一个对象。
- 当A完成了实例化并添加进了三级缓存后，就要开始为A进行属性注入了，在注入时发现A依赖了B，那么这个时候Spring又会去getBean(b)，然后反射调用setter方法完成属性注入。
- 因为B需要注入A，所以在创建B的时候，又会去调用getBean(a)，这个时候就又回到之前的流程了，但是不同的是，之前的getBean是为了创建Bean，而此时再调用getBean不是为了创建了，而是要从缓存中获取，
- 因为之前A在实例化后已经将其放入了三级缓存singletonFactories中，此时getBean(a)的二级缓存会通过调用三级缓存的facotry，通过工厂的getObject方法将对象放入到二级缓存中并返回。

**结合了AOP的循环依赖**

如果在开启AOP的情况下，那么就是调用 getEarlyBeanReference 方法对应的源码如下：

```
public Object getEarlyBeanReference(Object bean, String beanName) {
    Object cacheKey = getCacheKey(bean.getClass(), beanName);
    this.earlyProxyReferences.put(cacheKey, bean);
    // 如果需要代理，返回一个代理对象，不需要代理，直接返回当前传入的这个bean对象
    return wrapIfNecessary(bean, beanName, cacheKey);
}
```
对A进行了AOP代理的话，那么此时getEarlyBeanReference将返回一个代理后的对象，而不是实例化阶段创建的对象，这样就意味着B中注入的A将是一个代理对象而不是A的实例化阶段创建后的对象。

##### 循环依赖的总结
1. **为啥要用三级缓存，是否可以用二级缓存**

在普通的循环依赖的情况下，三级缓存没有任何作用。三级缓存实际上跟Spring中的AOP相关。AOP场景下的getEarlyBeanReference会拿到一个代理的对象，但是不确定有没有依赖，需不需要用到这个依赖对象，所以先给一个工厂放到三级缓存里。

2. **三级缓存工厂的作用？**

这个工厂的目的在于**延迟对实例化阶段生成的对象的代理**，只有真正发生循环依赖的时候，才去提前生成代理对象，否则只会创建一个工厂并将其放入到三级缓存中，但是不会去通过这个工厂去真正创建对象。

我们思考一种简单的情况，就以单独创建A为例，假设AB之间现在没有依赖关系，但是A被代理了，这个时候当A完成实例化后还是会进入下面这段代码：

```
// A是单例的，mbd.isSingleton()条件满足
// allowCircularReferences：这个变量代表是否允许循环依赖，默认是开启的，条件也满足
// isSingletonCurrentlyInCreation：正在在创建A，也满足
// 所以earlySingletonExposure=true
boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
                                  isSingletonCurrentlyInCreation(beanName));
// 还是会进入到这段代码中
if (earlySingletonExposure) {
	// 还是会通过三级缓存提前暴露一个工厂对象
    addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
}
```
**即使没有循环依赖**，也会将其添加到三级缓存中，而且是不得不添加到三级缓存中，因为到目前为止Spring也不能确定这个Bean有没有跟别的Bean出现循环依赖。

3. **假设我们在这里直接使用二级缓存的话，那么意味着所有的Bean在这一步都要完成AOP代理。这样做有必要吗**？

不仅没有必要，而且违背了Spring在结合AOP跟Bean的生命周期的设计！Spring结合AOP跟Bean的生命周期本身就是通过AnnotationAwareAspectJAutoProxyCreator这个后置处理器来完成的，**在这个后置处理的postProcessAfterInitialization方法中对初始化后的Bean完成AOP代理**。如果出现了循环依赖，那没有办法，只有给Bean先创建代理，但是没有出现循环依赖的情况下，**设计之初就是让Bean在生命周期的最后一步完成代理而不是在实例化后就立马完成代理**。


4. **三级缓存真的提高了效率了吗？**
- **没有进行AOP的Bean间的循环依赖**：这种情况下三级缓存根本没用！所以不会存在什么提高了效率的说法
- **进行了AOP的Bean间的循环依赖**：

就以我们上的A、B为例，其中A被AOP代理，我们先分析下使用了三级缓存的情况下，A、B的创建流程

![image](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9naXRlZS5jb20vd3hfY2MzNDdiZTY5Ni9ibG9nSW1hZ2UvcmF3L21hc3Rlci9pbWFnZS0yMDIwMDcwNjE3MTUxNDMyNy5wbmc?x-oss-process=image/format,png)

假设不使用三级缓存，直接在二级缓存中

![image](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9naXRlZS5jb20vd3hfY2MzNDdiZTY5Ni9ibG9nSW1hZ2UvcmF3L21hc3Rlci9pbWFnZS0yMDIwMDcwNjE3MjUyMzI1OC5wbmc?x-oss-process=image/format,png)

上面两个流程的唯一区别在于为A对象创建代理的时机不同，在使用了三级缓存的情况下为A创建代理的时机是在B中需要注入A的时候，而不使用三级缓存的话在A实例化后就需要马上为A创建代理然后放入到二级缓存中去。对于整个A、B的创建过程而言，消耗的时间是一样的

**综上，不管是哪种情况，三级缓存提高了效率这种说法都是错误的！**

**参考博客**：

[面试必杀技，讲一讲Spring中的循环依赖](https://blog.csdn.net/qq_41907991/article/details/107164508)

[几个直击灵魂的Spring拷问](https://www.jianshu.com/p/e68df1bfbaf6)

