### 数据库事务的“抓手”
数据库的事务功能已经由数据库自身实现，它留给用户的就是三个指令：**开启事务**、**提交事务**和**回滚事务**。

开启事务一般是**start transaction或begin transaction**。提交事务通常都是**commit**。回滚事务通常都是**rollback**。

数据库通常都有**自动开启事务和自动提交事务**的开关，打开后，事务就会自动的开启和提交或回滚。这样用户就无感知了。

### JDBC的事务“抓手”

JDBC实现了访问数据库的协议，可以认为是对一些指令的封装，当然也包括这些事务指令。它们都被做到了**java.sql.Connection**这个接口里。

它代表到数据库的一个连接，当这个连接建立好后，默认事务就已经打开了，而且默认情况下执行完一个SQL语句后事务也是自动提交的。

可以通过下面这个API进行检测：
```
boolean getAutoCommit();
```
通常情况下，我们都不希望事务是一句一提交，而是要执行若干个SQL语句后一次性提交，此时我们就需要改变这种自动提交的行为。

可以通过下面这个API进行设置：

```
void setAutoCommit(boolean autoCommit);
```
当我们禁用掉自动提交之后，一定要记得自己手动提交事务，否则结果可想而知。当然，需要回滚的时候也要记得手动回滚。

可以通过下面这个API提交事务：

```
void commit();
```
可以通过下面这个API回滚事务：
```
void rollback();
```
这样我们就可以在Java代码级别来控制事务的行为了。同时我们也应该认识到，在**Java代码级别事务是和Connecton的实例对象**绑在一起的。

换句话说，只有在同一个Connection对象上执行的SQL语句才会在同一个事务里。在不同的Connection对象上执行的SQL语句永远不会在同一个事务里。

更精确地说，后者构成的是分布式事务，前者通常称为本地事务。当然，分布式事务有属于自己的解决方案，只是不能再使用本地事务了。

> 备注：以上这些其实都是基本常识，只是现在的ORM框架太牛了，导致很多年轻的码农都没机会再接触这些了。（确实哈^_^）

### Spring的事务“抓手”

Spring通过使用@**Transactional**注解实现了
**声明式事务**，并且可以通过设置**Propagation属性来影响事务的传播特性**。

事务的传播特性其实就是指，Service层的若干方法在互相调用交织在一起的时候，究竟哪些方法的代码是在同一个事务里执行，哪些方法的代码不是。

通过前面的分析可知，**在同一个事务里执行的方法代码背后必须使用的是同一个Connection对象**，当事务切换时，必须要切换背后的Connection对象为对应的另一个。

因此，当执行流程进入/退出不同的方法时，Spring根据方法上注解的传播特性，在背后对应的进行Connection对象的切换，也包括新建Connection对象，提交或回滚事务等。

我们知道，在写Service层方法或Mapper层方法时，根本接触不到Connection对象，所以它更不可能明目张胆的以参数的方式传来传去，只能在背地里暗箱操作。

由于这些互相交织的方法代码最终都是在同一个线程里运行的，所以**借助线程的ThreadLocal来实现背后的操作是最适合的**。

只需在方法调用的入口/出口来新建/切换Connection对象，并提交/回滚Connection对象上的事务即可。

##### Spring事务的传播特性原理
**Spring事务是通过代理来实现的，通常是通过CGLIB操作字节码来生成子类，因为要动态加入开启/提交事务的这些代码。**

下面通过一个例子来说明，有四个方法及其对应的传播特性：

方法一，传播特性为REQUIRED：
```
void method1();
```

方法二，传播特性为REQUIRED：
```
void method2();
```
方法三，传播特性为REQUIRES_NEW：
```
void method3();
```
方法四，传播特性为REQUIRED：

```
void method4();
```

假设它们之间的调用关系是，在方法一里依次调用方法二三四：

```
void method1() {
    method2();
    method3();
    method4();
}
```
可以使用下面这个图来表示，图01：

![image]( https://mmbiz.qpic.cn/mmbiz_png/Kmic4GNAkJNDax7HepiaH6iaoE2SryguvZeLQNxgl7Hp7W9eibzTGs4ZCOc7Jtq684PUOtaEDkialCOdN2XCDc6c1DA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

**那么经过Spring生成代理后，会重写每个方法，并在原来的每个方法前面开启事务，方法后面提交/回滚事务**。

等效的伪代码如下：

```
beginTx();
void method1() {
    beginTx();
    method2();
    commit/rollbackTx();
    beginTx();
    method3();
    commit/rollbackTx();
    beginTx();
    method4();
    commit/rollbackTx();
}
commit/rollbackTx();
```
可以使用下面这个图来表示，图02：

![image](https://mmbiz.qpic.cn/mmbiz_png/Kmic4GNAkJNDax7HepiaH6iaoE2SryguvZe5nPAM0cuXovmftQ2eyf4m2G9Z222M8aWicDENQN6pHUY7iaWRPwyoHyg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

其中红色的向上箭头对应于beginTx()操作，蓝色的向下箭头对应于commit/rollbackTx()操作。

下面就来看看具体的执行过程：

**一、执行方法一的开启事务**

![image](https://mmbiz.qpic.cn/mmbiz_png/Kmic4GNAkJNDax7HepiaH6iaoE2SryguvZegJzl7lQnoxxias18zr1iaO7rqlGicKFBAegKRzRLiaQJwYCH7NnVGHvTVQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

**由于方法一要求有事务，此时线程中没有事务，于是就开启一个新的事务，即创建一个新的Connection对象并绑定到线程的ThreadLocal。**

**二、进入方法一开始执行**

![image](https://mmbiz.qpic.cn/mmbiz_png/Kmic4GNAkJNDax7HepiaH6iaoE2SryguvZeBnIGjiaichEDMaTVkPgibAIrwGfXafIialJOfic0rERDPhaibiahnXBIDIibhQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

**三、执行方法二的开启事务**

![image](https://mmbiz.qpic.cn/mmbiz_png/Kmic4GNAkJNDax7HepiaH6iaoE2SryguvZenkLgYTqHaqZbzkGcU1EKaNnLfKG8ojWCaLoDsGib1NpjY65EPBj9eyQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

**由于方法二要求有事务，此时线程中已经有事务了，因此直接参与/使用这个事务即可。**

**四、进入方法二开始执行**

![image](https://mmbiz.qpic.cn/mmbiz_png/Kmic4GNAkJNDax7HepiaH6iaoE2SryguvZexgbbC05qGqWia8hbjic2kJicPibV88LCA38Z13sE02iaBYhN9mtPmy2Tjuw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

**五、方法二完毕执行提交事务**

![image](https://mmbiz.qpic.cn/mmbiz_png/Kmic4GNAkJNDax7HepiaH6iaoE2SryguvZeZaAuAkuINhGLp6o9ibqicHVamb3Ola6XTUEnQtN2Zp8En4qQ3CuHhKzA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

由于该事务并非方法二新建的，它只是参与而已，所以它没有资格提交事务，因此实际并不提交事务。

**六、方法二结束后又回到方法一里执行**

![image](https://mmbiz.qpic.cn/mmbiz_png/Kmic4GNAkJNDax7HepiaH6iaoE2SryguvZeJiaEaAPrKC8tpqXymehPZxHjpFibmicC41IjwxZVpb6gyvtLBMkXvFRAA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

**七、执行方法三的开启事务**

![image](https://mmbiz.qpic.cn/mmbiz_png/Kmic4GNAkJNDax7HepiaH6iaoE2SryguvZeiaTiaiaNsmUwWL4rIF4HhQuBhkz4a8bsw4ria2UssWaq6VeaQfKp4uMQQg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

由于方法三要求新的事物，所以新建一个事务，并把当前的现有事务挂起，使当前线程使用新事务。

即新建一个Connection对象，把当前现有Connection对象与线程的ThreadLocal解绑，把新的Connection对象绑定到线程的ThreadLocal。

那原来的那个Connection对象呢？当然是存储起来了，存储的形式是依附于新的Connection对象，即和新的对象关联一下。

**八、进入方法三开始执行**

![image](https://mmbiz.qpic.cn/mmbiz_png/Kmic4GNAkJNDax7HepiaH6iaoE2SryguvZeIwSAjErgiciaBgAXqUn55TUuuTnw5HibLbF9rN60LicUVYJwCjFvovTkLA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

可以看到，原来的事务相当于暂存在新的事务里。注意，这种说法只是一个形象的比喻。

**九、方法三完毕执行提交事务**

![image](https://mmbiz.qpic.cn/mmbiz_png/Kmic4GNAkJNDax7HepiaH6iaoE2SryguvZeQCaY4MibpJic5eBHXLFnS6rXiah2rnGkDvKVTqCuicYhKPWJtABn3SxzIw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

由于这个事务是个新建事务，即是方法三创建的而非参与的，所以有权提交，所以事务就真的提交了。

**十、方法三结束后又回到方法一里执行**

![image](https://mmbiz.qpic.cn/mmbiz_png/Kmic4GNAkJNDax7HepiaH6iaoE2SryguvZezhmxMJslfKq3n5gQjtWswuDOia7KCBibHRFKTXRCrtlBuHZvRm9SVdPA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

方法三的事务完成了，但是它里面暂存了方法一的事务，于是把它重新恢复到线程里去。

**十一、执行方法四的开启事务**

![image](https://mmbiz.qpic.cn/mmbiz_png/Kmic4GNAkJNDax7HepiaH6iaoE2SryguvZeLoDHjglOxUdticTahch1rBQ1OX8ATI3GFkIJoGUkUdsaGJnjV3Ap7sw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

由于方法四要求有事务，此时线程中已经有事务了，因此直接参与/使用这个事务即可。

**十二、进入方法四开始执行**

![image](https://mmbiz.qpic.cn/mmbiz_png/Kmic4GNAkJNDax7HepiaH6iaoE2SryguvZeqQicwp0ajlLicxj6p8nCrxCKlXpLoqmgBC8qH9SLT4z1xO8OictBhB5fg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

**十三、方法四完毕执行提交事务**

![image](https://mmbiz.qpic.cn/mmbiz_png/Kmic4GNAkJNDax7HepiaH6iaoE2SryguvZe8CnFpZc6YyTs6ulib5XX4hxf1FOLfia6ftibJDbfibQlpqDIyrCmkcI73Q/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

由于该事务并非方法四新建的，它只是参与而已，所以它没有资格提交事务，因此实际并不提交事务。

**十四、方法四结束后又回到方法一里执行**

![image](https://mmbiz.qpic.cn/mmbiz_png/Kmic4GNAkJNDax7HepiaH6iaoE2SryguvZe4F5FiaLPuKbG9YsdibRTZ7V5M84EATNctQC7yUwDic6mDicj3FWC4ddR0g/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

**十五、方法一完毕执行提交事务**

![image](https://mmbiz.qpic.cn/mmbiz_png/Kmic4GNAkJNDax7HepiaH6iaoE2SryguvZeCZAcbQxRODUp2zA8zliaRF753l7icd2dIMNxmkyZarJ4ovKW2yzw4ibwg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

由于这个事务是个新建事务，即是方法一创建的而非参与的，所以有权提交，所以事务就真的提交了。

**十六、所有事务完成，只剩一个空线程**

![image](https://mmbiz.qpic.cn/mmbiz_png/Kmic4GNAkJNDax7HepiaH6iaoE2SryguvZeTfWJibcnTwsPBxaMwiaRpFH4u14d98aZicAPuuwMYWaVUQNX0sDUfylFQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

方法一的事务完成了，由于它里面没有暂存其它事务，所以没有事务了，因此最后只剩一个空线程。

**说明**：这只是传播特性的实现原理解说，所以比较简单，实际代码实现要考虑很多事情，因此会复杂很多。


### @Transactional失效场景

##### @Transactional注解可以作用于哪些地方？
- **作用于类**：当把@Transactional 注解放在类上时，表示所有该类的public方法都配置相同的事务属性信息。
- **作用于方法**：当类配置了@Transactional，方法也配置了@Transactional，方法的事务会覆盖类的事务配置信息。
- **作用于接口**：不推荐这种使用方法，因为一旦标注在Interface上**并且配置了Spring AOP 使用CGLib动态代理**（**cglib是通过实现类生成子类代理来实现的**），将会导致@Transactional注解失效

##### 1、@Transactional 应用在非 public 修饰的方法上

如果Transactional注解应用在非public 修饰的方法上，Transactional将会失效。

![image](https://user-gold-cdn.xitu.io/2020/3/19/170f0e025a51a1b4?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

之所以会失效是因为在Spring AOP 代理时，如上图所示 TransactionInterceptor （事务拦截器）在目标方法执行前后进行拦截，DynamicAdvisedInterceptor（CglibAopProxy 的内部类）的 intercept 方法或 JdkDynamicAopProxy 的 invoke 方法会间接调用computeTransactionAttribute 方法，**获取Transactional 注解的事务配置信息**。而此方法会检查目标方法的修饰符是否为public，不是public则不会获取@Transactional 的属性配置信息。

```
protected TransactionAttribute computeTransactionAttribute(Method method,Class<?> targetClass) {
    if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
        return null;
    }
}
```
> 注意：protected、private 修饰的方法上使用 @Transactional 注解，虽然事务无效，但不会有任何报错，这是我们很容犯错的一点。

##### 2、同一个类中方法调用，导致@Transactional失效

开发中避免不了会对同一个类里面的方法调用，比如有一个类UserServiceImpl，它的一个方法A，A再调用本类的方法B（不论方法B是用public还是private修饰），但方法A没有声明注解事务，而B方法有。则外部调用方法A之后，方法B的事务是不会起作用的。这也是经常犯错误的一个地方。

```
@Override
@Transactional(propagation = Propagation.REQUIRED)
public int insertException(User record) {
    userMapper.insert(record);
    throw new RuntimeException();
}

@Override
public int insertExceptionWithCall(User record) {
    return insertException(record);
}
```
**insertException方法不会回滚**


**那为啥会出现这种情况**?

其实这还是由于使用**Spring AOP**代理造成的，因为只有当事务方法被当前类以外的代码调用时，才会由Spring生成的代理对象来管理。

##### 3、数据库引擎不支持事务

这种情况出现的概率并不高，事务能否生效数据库引擎是否支持事务是关键。常用的MySQL数据库默认使用**支持事务的innodb引擎**。一旦数据库引擎切换成**不支持事务的myisam**，那事务就从根本上失效了。

##### 4、异常被你的 catch“吃了”导致@Transactional失效


```
@Override
@Transactional(propagation = Propagation.REQUIRED)
public void transactionExceptionRequiredExceptionTry(Integer userId, String userName) {

    User1 user1 = new User1();
    user1.setName(userName);
    user1Service.insert(user1);

    User user = new User();
    user.setPassword("222");
    user.setUserId(userId);
    user.setUserName(userName);
    try {
        userService.insertException(user) ;
    }catch (Exception e){
        log.error("transactionExceptionRequiredExceptionTry userService exception",e);
    }
}

@Override
@Transactional(propagation = Propagation.REQUIRED)
public int insertException(User record) {
    userMapper.insert(record);
    throw new RuntimeException();
}
```
如果userService方法内部抛了异常，而transactionExceptionRequiredExceptionTry方法此时try catch了B方法的异常，user1Service能插入成功吗？ 那这个事务还能正常回滚吗？

答案：不能成功，也不能正常回滚，会抛出异常：

```
Transaction rolled back because it has been marked as rollback-only

```
因为当**userService**中抛出了一个异常以后，**userService**标识当前事务需要rollback。但是**userService**中由于你手动的捕获这个异常并进行处理，**transactionExceptionRequiredExceptionTry**认为当前事务应该正常commit。此时就出现了前后不一致，也就是因为这样，抛出了前面的UnexpectedRollbackException异常。

**spring的事务**是在调用业务方法之前开始的，业务方法执行完毕之后才执行commit or rollback，事务是否执行取决于是否抛出runtime异常。如果抛出runtime exception
并在你的业务方法中没有catch到的话，事务会回滚。

在业务方法中一般不需要catch异常，如果非要catch一定要抛出throw new RuntimeException()，或者注解中指定抛异常类型@Transactional(rollbackFor=Exception.class)，否则会导致事务失效，数据commit造成数据不一致，所以有些时候try catch反倒会画蛇添足。