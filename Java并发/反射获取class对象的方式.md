![image](http://assets.processon.com/chart_image/5fdb2e317d9c0855ea710c4b.png)
Java 的反射机制是指在**运行状态中**，对于任意一个类都能够知道这个类所有的属性和方法；并且对于任意一个对象，都能够调用它的任意一个方法；这种**动态获取信息以及动态调用对象方法**的功能成为Java语言的反射机制。

> 那么正射是什么呢？

我们在编写代码时，当需要使用到某一个类的时候，都会先了解这个类是做什么的。然后实例化这个类，接着用实例化好的对象进行操作，这就是正射。

```
Student student = new Student();
student.doHomework("数学");
```
**反射**：一开始并不知道我们要初始化的类对象是什么，自然也无法使用new关键字来创建对象。

```
Class clazz = Class.forName("reflection.Student");
Method method = clazz.getMethod("doHomework", String.class);
Constructor constructor = clazz.getConstructor();
Object object = constructor.newInstance();
method.invoke(object, "语文");
```
**正射与反射对比**

但是，其实现的过程还是有很大的差别的：
- 第一段代码在未运行前就已经知道了要运行的类是Student；
- 第二段代码则是到整个程序运行的时候，从字符串reflection.Student，才知道要操作的类是Student。

**结论**

反射就是在运行时才知道要操作的类是什么，并且可以在运行时获取类的完整构造，并调用对应的方法。

##### Class 对象理解

Java是如何让我们在运行时识别对象和类的信息的？主要有两种方式： 一种是传统的RRTI，它假定我们在编译期已知道了所有类型。 另一种是反射机制，它允许我们在运行时发现和使用类的信息。

**每个类都有一个Class对象，每当编译一个新类就产生一个Class对象**（更恰当地说，是被保存在一个同名的.class文件中）。比如创建一个Student类，那么，JVM就会创建一个Student对应Class类的Class对象，该Class对象保存了Student类相关的类型信息。
![image](https://imgedu.lagou.com/3989979b49b940e3b04073821a6b3659.jpg)

Class类的对象作用是运行时提供或获得某个对象的类型信息（包括方法、构造器和属性值）

**获取反射中的Class对象有三种方法**

第一种，使用 Class.forName 静态方法。

```
Class class1 = Class.forName("reflection.TestReflection");
```
第二种，使用类的.class 方法

```
Class class2 = TestReflection.class;

```
第三种，使用实例对象的 getClass() 方法。

```
TestReflection testReflection = new TestReflection();
Class class3 = testReflection.getClass();
```
**通过反射创建类对象主要有两种方式**
![image](https://imgedu.lagou.com/de1ebd0b41e443d6ac2ea76d4a400b9e.jpg)


```
//方式一
Class class1 = Class.forName("reflection.Student");
Student student = (Student) class1.newInstance();
System.out.println(student);

//方式二
Constructor constructor = class1.getConstructor();
Student student1 = (Student) constructor.newInstance();
System.out.println(student1);
```
> 两种方法的到底有什么区别呢？

- 第一种方法：**Class.newInstance()只能使用默认的构造方法**，换句话说就是使用此方法是不能通过带参的构造函数来生成一个类的实例。==不能访问私有的构造器==
- 第二种方法：**通过Constructor.newInstance()方法可以使用默认的构造方法，也可以使用带参的构造方法来生成一个类的实例**。可以访问私有构造器


```
Class<User> clazz = User.class;
try{
    User instance = clazz.newInstance();
    System.out.println(instance);
}catch (Exception e){
    System.out.println(e);
}
Constructor<User> constructor = clazz.getDeclaredConstructor(new Class[]{String.class,String.class});
constructor.setAccessible(true);
Object instance1 = constructor.newInstance(new Object[]{"1","1222"});

System.out.println(instance1);
```
**结果：**
```
can not access a member of class com.lcyanxi.model.User with modifiers "private"
User{}
```


> 注意：在只有一个默认的构造函数（或不带参数的构造函数）时，使用第一种方法比较方便，如果要使用类中的其他构造方法那只能使用第二种方法了

##### 反射的一些应用以及问题

**JDBC 的数据库的连接**

在JDBC连接数据库中，一般包括加载驱动，获得数据库连接等步骤。而加载驱动，就是引入相关Jar包后，**通过Class.forName()即反射技术**，加载数据库的驱动程序

>  **加载一个类可以使用Class.forName也可以使用ClassLoader，有什么区别吗**

在java中Class.forName()和ClassLoader都可以对类进行加载。**ClassLoader就是遵循双亲委派模型**最终调用启动类加载器的类加载器，实现的功能是“**通过一个类的全限定名来获取描述此类的二进制字节流**”，获取到二进制流后放到JVM中。Class.forName()方法实际上也是调用的CLassLoader来实现的。


**Class.forName(String className)源码：**

```
@CallerSensitive
public static Class<?> forName(String className)
            throws ClassNotFoundException {
    Class<?> caller = Reflection.getCallerClass();
    return forName0(className, true, ClassLoader.getClassLoader(caller), caller);
}
```
最后调用的方法是forName0这个方法，**在这个forName0方法中的第二个参数被默认设置为了true，这个参数代表是否对加载的类进行初始化，设置为true时会类进行初始化**，代表会执行类中的静态代码块，以及对静态变量的赋值等操作。


也可以调用Class.forName(String name, boolean initialize,ClassLoader loader)方法来**手动选择在加载类的时候是否要对类进行初始化**源码如下：

```
@CallerSensitive
public static Class<?> forName(String name, boolean initialize,ClassLoader loader){
    Class<?> caller = null;
    SecurityManager sm = System.getSecurityManager();
    if (sm != null) {
        caller = Reflection.getCallerClass();
        if (sun.misc.VM.isSystemDomainLoader(loader)) {
            ClassLoader ccl = ClassLoader.getClassLoader(caller);
            if (!sun.misc.VM.isSystemDomainLoader(ccl)) {
            sm.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
            }
        }
    }
    return forName0(name, initialize, loader, caller);
}
```
测试：

```
System.out.println("class forName start");
Class<?> forName = Class.forName("com.lcyanxi.model.User");
System.out.println("classLoader  loadClass start）;

Class<?> aClass = ClassLoader.getSystemClassLoader().loadClass("com.lcyanxi.model.User");
```
**结果**

```
class forName start
User static start
classLoader  loadClass start
```
**根据运行结果得出Class.forName加载类时将类进了初始化，而ClassLoader的loadClass并没有对类进行初始化，只是把类加载到了虚拟机中**

> 为什么jdbc会选择Class.forName加载类而不是classLoader呢？

而在我们使用JDBC时通常是使用Class.forName()方法来加载数据库连接驱动。这是**因为在JDBC规范中明确要求Driver(数据库驱动)类必须向DriverManager注册自己**。

以MySQL的驱动为例解释


```
public class Driver extends NonRegisteringDriver implements java.sql.Driver {  
    static {  
        try {  
            java.sql.DriverManager.registerDriver(new Driver());  
        } catch (SQLException E) {  
            throw new RuntimeException("Can't register driver!");  
        }  
    }  
  
    public Driver() throws SQLException {  
        // Required for Class.forName().newInstance()  
    }  
}
```
**我们看到Driver注册到DriverManager中的操作写在了静态代码块中，这就是为什么在写JDBC时使用Class.forName()的原因了。**

**Spring 框架的使用**

Spring 通过 XML配置模式装载Bean，也是反射的一个典型例子。

装载过程：
- 将程序内XML 配置文件加载入内存中
- Java类解析xml里面的内容，得到相关字节码信息
- 使用反射机制，得到Class实例对象
- 动态配置实例的属性，使用

我们看看怎么实现的

**获取构造器**
```
@Override
public Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {
    if (!bd.hasMethodOverrides()) {
        Constructor<?> constructorToUse;
        synchronized (bd.constructorArgumentLock) {
            constructorToUse = (Constructor<?>) bd.resolvedConstructorOrFactoryMethod;
            if (constructorToUse == null) {
                final Class<?> clazz = bd.getBeanClass();
                // .........
                if (System.getSecurityManager() != null) {
                    constructorToUse = AccessController.doPrivileged(
                            (PrivilegedExceptionAction<Constructor<?>>) clazz::getDeclaredConstructor);
                }
                else {
                    constructorToUse = clazz.getDeclaredConstructor();
                }
                bd.resolvedConstructorOrFactoryMethod = constructorToUse;
               // ......
            }
        }
        return BeanUtils.instantiateClass(constructorToUse);
    }
   // .......
}
```

**通过Constructor实例化对象**
```
public T newInstance(Object ... initargs){
    // .......
    ConstructorAccessor ca = constructorAccessor;   // read volatile
    if (ca == null) {
        ca = acquireConstructorAccessor();
    }
    @SuppressWarnings("unchecked")
    T inst = (T) ca.newInstance(initargs);
    return inst;
}
```
其实spring IOC就是通过Constructor的方式创建对象的

**这样做当然是有好处的**：不用每次都去new实例了，并且可以修改配置文件，比较灵活

##### 反射存在的问题
1. **性能问题**：java反射的性能并不好，原因主要是编译器没法对反射相关的代码做优化
2. **安全问题**：我们知道单例模式的设计过程中，会强调将构造器设计为私有，因为这样可以防止从外部构造对象。但是反射可以获取类中的域、方法、构造器，修改访问权限。所以这样并不一定是安全的

##### 通过反射手动模拟一个Autowired实现

**controller**
```
public class IndexController {

    @MyAutowired
    private MyAutowiredService myAutowiredService;

    public MyAutowiredService getCountServiceIntense(){
        return myAutowiredService;
    }
}
```
**自定义注解：MyAutowired**

```
@Retention(RetentionPolicy.RUNTIME) // 注解的生命周期
@Target(ElementType.FIELD) // 表示注解用在属性上
@Documented //该注解将被包含在javaDoc中
@Inherited  // 子类可以继承父类中的注解
public @interface MyAutowired {
}
```

依赖注入的实现类，==注意实现类的构造器的方法为私有方法==
```
public class MyAutowiredService {
    private MyAutowiredService() {
        System.out.println("myAutowiredService construct is start .....");
    }
}
```
**测试代码**
```
@Slf4j
public class MyAutowiredDemo {
    public static void main(String[] args) {
        IndexController controller = new IndexController();
        //1.获取相应类对象
        Class<? extends IndexController> clazz = controller.getClass();
        //2.通过反射获取所有的属性
        Stream.of(clazz.getDeclaredFields()).forEach(field -> {
            //3.获取标注了@Autowired注解的属性
            MyAutowired annotation = field.getAnnotation(MyAutowired.class);
            if (Objects.isNull(annotation)){
                return;
            }
            //4.修改属性的权限值
            field.setAccessible(true);
            //获取出相应的类型值
            Class<?> type = field.getType();

            try {
                //5.new出相应类型的对象
                //Object newInstance = type.newInstance(); // 无法获取私有构造器
                Constructor<?> constructor = type.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object instance = constructor.newInstance();
                System.out.println(instance);
                //6.把值进行设置
                field.set(controller,instance);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        log.info("myAutowiredDemo get userService:[{}]",controller.getCountServiceIntense());
    }
}
```
**Class.newInstance()是不能访问私有构造器的**