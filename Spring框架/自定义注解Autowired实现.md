##### SpringIOC原理
SpringIOC的原理无外乎就是反射 + 工厂嘛
![image](http://assets.processon.com/chart_image/5fda000f63768906e6e46fca.png)

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