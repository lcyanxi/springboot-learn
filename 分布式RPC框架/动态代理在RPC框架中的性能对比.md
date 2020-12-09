### 动态代理在RPC框架中的性能对比
动态代理工具比较成熟的产品有：
JDK自带的JDK Proxy，ASM，CGLIB(基于ASM包装)，Javassist

**数据为执行三次，每次调用一千万次代理方法的结果，**   [测试代码地址](https://github.com/lcyanxi/springboot-learn/blob/master/springboot-learn/springboot-manage/src/main/java/com/lcyanxi/designPatterns/dynamicProxy/DynamicProxyPerformanceTest.java)

##### 测试结果：

```
Create JDK Proxy: 2 ms
Create CGLIB Proxy: 76 ms
Create Javassist Proxy: 89 ms
Create Javassist Bytecode Proxy: 101 ms
Create ASM Proxy: 4 ms
================
Run JDK Proxy: 97 ms, 14,536,756 t/s
Run CGLIB Proxy: 63 ms, 22,381,990 t/s
Run Javassist Proxy: 161 ms, 8,758,170 t/s
Run Javassist Bytecode Proxy: 43 ms, 32,792,218 t/s
Run ASM Bytecode Proxy: 44 ms, 32,046,941 t/s
----------------
Run JDK Proxy: 80 ms, 17,625,817 t/s
Run CGLIB Proxy: 39 ms, 36,155,523 t/s
Run Javassist Proxy: 148 ms, 9,527,468 t/s
Run Javassist Bytecode Proxy: 31 ms, 45,485,980 t/s
Run ASM Bytecode Proxy: 32 ms, 44,064,544 t/s
----------------
Run JDK Proxy: 50 ms, 28,201,308 t/s
Run CGLIB Proxy: 45 ms, 31,334,786 t/s
Run Javassist Proxy: 168 ms, 8,393,246 t/s
Run Javassist Bytecode Proxy: 38 ms, 37,106,984 t/s
Run ASM Bytecode Proxy: 31 ms, 45,485,980 t/s
----------------
```
##### 测试结论
1. ASM和Javassist字节码生成方式不相上下，都很快，是CGLIB的5倍。
2. CGLIB次之，是JDK自带的两倍。
3. JDK自带的再次之，因JDK1.6对动态代理做了优化，如果用低版本JDK更慢，==要注意的是JDK也是通过字节码生成来实现动态代理的，而不是反射==（**？？没太明白**）。jdk动态代理是实现同一个接口生成一个代理类（com.sun.proxy.$Proxy），然后在ProxyGenerator.generateProxyClass里生成字节码，最后使用类加载器加载生成的类 [jdk动态代理与cglib动态代理实现原理
](https://developer.aliyun.com/article/654228)
4. Javassist提供者动态代理接口最慢，比JDK自带的还慢。
(这也是为什么网上有人说Javassist比JDK还慢的原因，用Javassist最好别用它提供的动态代理接口，而可以考虑用它的字节码生成方式)

##### 差异原因
- 各方案生成的字节码不一样，
像JDK和CGLIB都考虑了很多因素，以及继承或包装了自己的一些类，
所以生成的字节码非常大，而我们很多时候用不上这些，
- 而手工生成的字节码非常小，所以速度快，


#####  最终选型
- 最终决定使用Javassist的字节码生成代理方式，
虽然ASM稍快，但并没有快一个数量级，
- 而Javassist的字节码生成方式比ASM方便，
Javassist只需用字符串拼接出Java源码，便可生成相应字节码，
- 而ASM需要手工写字节码。

##### JDK动态代理和CGLIB字节码生成的区别？
Java动态代理是**利用反射机制**生成一个实现代理接口的匿名类，在调用具体方法前调用InvokeHandler来处理。 而cglib动态代理是**利用asm开源包**，对代理对象类的**class文件**加载进来，通过**修改其字节码生成子类**来处理。
1. 如果目标对象实现了接口，默认情况下会采用JDK的动态代理实现AOP
2. 如果目标对象实现了接口，可以强制使用CGLIB实现AOP
3. 如果目标对象没有实现了接口，必须采用CGLIB库，spring会自动在JDK动态代理和CGLIB之间转换

##### 如何强制使用CGLIB实现AOP？
1. 添加CGLIB库，SPRING_HOME/cglib/*.jar
2. 在spring配置文件中加入<aop:aspectj-autoproxy proxy-target-class="true"/>

##### 实现区别
1. JDK动态代理只能对实现了接口的类生成代理，而不能针对类
2. CGLIB是针对类实现代理，主要是对指定的类生成一个子类，覆盖其中的方法，因为是继承，所以该类或方法最好不要声明成final

JDK代理是不需要以来第三方的库，只要要JDK环境就可以进行代理，它有几个要求
1. 实现InvocationHandler
2. 使用Proxy.newProxyInstance产生代理对象
3. 被代理的对象必须要实现接口

CGLib 必须依赖于CGLib的类库，但是它需要类来实现任何接口代理的是指定的类生成一个子类，覆盖其中的方法，是一种继承但是针对接口编程的环境下推荐使用JDK的代理
