![image](http://assets.processon.com/chart_image/60004f8c6376897ae0d22043.png)
#### Meta Annotation(元注解)
元注解的作用就是负责注解其他注解。Java5定义了4个标准的Meta Annotation类型，它们被用来提供对其它 Annotation类型作说明。

##### @Target
**@Target说明了Annotation所修饰的对象范围**：Annotation可被用于 packages、types（类、接口、枚举、Annotation类型）、类型成员（方法、构造方法、成员变量、枚举值）、方法参数和本地变量（如循环变量、catch参数）。在Annotation类型的声明中使用了@Target可更加明晰其修饰的目标。

@Target作用：**用于描述注解的使用范围，即被描述的注解可以用在什么地方**

**@Target取值(ElementType)**
- CONSTRUCTOR：用于描述构造器
- FIELD：用于描述域
- LOCAL_VARIABLE：用于描述局部变量
- METHOD：用于描述方法
- PACKAGE：用于描述包
- PARAMETER：用于描述参数
- TYPE：用于描述类、接口(包括注解类型) 或enum声明

##### @Retention
**@Retention定义了该Annotation的生命周期**：某些Annotation仅出现在源代码中，而被编译器丢弃；而另一些却被编译在class文件中；编译在class文件中的Annotation可能会被虚拟机忽略，而另一些在class被装载时将被读取（请注意并不影响class的执行，因为Annotation与class在使用上是被分离的）。@Retention有唯一的value作为成员。

@Retention作用：**表示需要在什么级别保存该注释信息，用于描述注解的生命周期**（即：被描述的注解在什么范围内有效）

@Retention取值来自java.lang.annotation.RetentionPolicy的枚举类型值
- SOURCE:在源文件中有效（即源文件保留）
- CLASS:在class文件中有效（即class保留）
- RUNTIME:在运行时有效（即运行时保留）

##### @Documented
@Documented用于描述其它类型的annotation应该被作为被标注的程序成员的公共API，因此可以被例如javadoc此类的工具文档化。@Documented是一个标记注解，没有成员。

##### @Inherited
@Inherited 是一个标记注解。如果一个使用了@Inherited修饰的annotation类型被用于一个class，则这个Annotation将被用于该class的子类。

##### 自定义Java注解
场景是xxl-job有时会追尾,导致数据重复插入，用自定义注解使用在方法上面，运用AOP的实现在方法执行前去获取锁，执行完毕之后获取锁，此处省略AOP的代码。
```
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConcurrentLeaseLock {
    /**
     * 锁的key
     * @return
     */
    String lockKey();
}
```


Annotation的语法比较简单，除了@符号的使用外，他基本与Java固有的语法一致，JavaSE中内置三个标准Annotation，定义在java.lang中：
- @Override
- @Deprecated
- @SuppressWarnnings：**通知编译器关闭对此方法的警告**