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