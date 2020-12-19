![image](http://assets.processon.com/chart_image/5fdbeff01e085304fb7dd5d0.png)
##### Bean创建的三个阶段
Spring在创建一个Bean时是分为三个步骤的
1. **实例化**:可以理解为new一个对象
2. **属性注入**:可以理解为调用setter方法完成属性注入
3. **初始化**:你可以按照Spring的规则配置一些初始化的方法（例如，@PostConstruct注解）

##### 生命周期的概念
Bean的生命周期指的就是在上面三个步骤中**后置处理器BeanPostprocessor穿插执行的过程**

##### 后置处理器的分析
按照实现接口进行分类
- 直接实现了BeanPostProcessor接口

最简单的后置处理器，也就是说直接实现了BeanPostProcessor接口，这种后置处理器**只能在初始化前后执行**
```
public interface BeanPostProcessor {
 // 初始化前执行的方法
 @Nullable
 default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
  return bean;
 }    
 // 初始化后执行的方法
 default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
  return bean;
 }
}
```
- 直接实现了InstantiationAwareBeanPostProcessor接口

在第一种后置处理的基础上进行了一层扩展，可以在Bean的**实例化阶段前后执行**

```
// 继承了BeanPostProcessor，额外提供了两个方法用于在实例化前后的阶段执行
// 因为实例化后紧接着就要进行属性注入，所以这个接口中还提供了一个属性注入的方法
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {
// 实例化前执行
 default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
  return null;
 }
// 实例化后置
 default boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
  return true;
 }
// 属性注入
default PropertyValues postProcessPropertyValues(
    PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
    return pvs;
 }
}
```
- Spring内部专用的后置处理器

可能有的小伙伴认为，第三种后置处理器肯定就是用来在属性注入前后执行了的吧。我只能说，大兄弟，太天真了，看看下面这张图

![image](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy90cEVJTGxFbHNrTGczWGZBa3V4VlB2elU4U3NGRnByZmx5ZTlVQU9VelpqM2pHMFQweklRZ29lT0FMakRpYzhBcGs1RWJwdmxOVW1HbkJmY0tTcDdHa0EvNjQw?x-oss-process=image/format,png)

这种情况下再为属性注入阶段专门提供两个方法是不是有点多余呢？**实际上第三种后置处理器是Spring为了自己使用而专门设计的**

我们再来看看，属性注入后紧接着已经是初始化的阶段，在初始化阶段开始前应该要调用BeanPostProcessor的预初始化方法（postProcessBeforeInitialization），然后调用自定义的初始化方法，最后调用postProcessAfterInitialization，这是没有问题。

**但是为什么要在初始前还要调用Aware接口的方法**，如果你看了源码的话可能会说，源码就是这么写的，别人就是这么设计的，但是为什么要这么设计呢？**我们看源码到一定阶段后不应该仅仅停留在是什么的阶段，而应该多思考为什么，这样能帮助你更好的了解这个框架**

> 那么为什么Aware接口非要在初始化前执行呢？

这样做的目的是因为，**初始化可能会依赖Aware接口提供的状态**，例如下面这个例子

```
@Component
public class A implements InitializingBean, ApplicationContextAware {
    ApplicationContext applicationContext;
    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化方法需要用到ApplicationContextAware提供的ApplicationContext
        System.out.println(applicationContext);
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
```

这种情况下Aware接口当然要在初始化前执行啦！

另外，在讨论Bean的初始化的时候经常会碰到下面这个问题，**@PostConstruct,afterPropertiesSet跟XML中配置的init-method方法的执行顺序**。

请注意，@PostConstruct实际上是在postProcessBeforeInitialization方法中处理的，严格来说它不属于初始化阶段调用的方法，所以这个方法是最先调用的

其次我们思考下是调用afterPropertiesSet方法的开销大还是执行配置文件中指定名称的初始化方法开销大呢？我们不妨用伪代码演示下

```
// afterPropertiesSet，强转后直接调用
((InitializingBean) bean).afterPropertiesSet()
    
// 反射调用init-method方法
// 第一步：找到这个方法
Method method = class.getMethod(methodName)
// 第二步：反射调用这个方法
```
相比而言肯定是第一种的效率高于第二种，一个只是做了一次方法调用，而另外一个要调用两次反射。

因此，**afterPropertiesSet的优先级高于XML配置的方式**

所以，这三个方法的执行顺序为：
- @PostConstruct注解标注的方法
- 实现了InitializingBean接口后复写的afterPropertiesSet方法
- XML中自定义的初始化方法

在完成初始化，没什么好说的了，**最后调用一下postProcessAfterInitialization，整个Bean的生命周期到此结束**

[原文地址](https://blog.csdn.net/qq_41907991/article/details/107329101)
