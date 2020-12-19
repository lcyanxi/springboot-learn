##### 曾经面试的时候被问到Apollo配置中心挂了怎么办？

其实也好办，这个问题其实和dubbo的注册中心一样，zk集群挂了消费者还能跟生产者通信吗？

答案**肯定是可以的**，因为在dubbo的consumer本地会缓存注册中心所有provider的集群地址的，只是说如果costumer重启重新去zk拉去数据的时候拉区不到。


在我们的日常开发中，Apollo作为配置中心，使用的频率比较高的同时它的重要新也非常重要，**为了防止Apollo集群挂到导致整体服务不可用我们采取将配置中心本地缓**存。

**方案采取：在Spring容器初始化完成后将namespace的配置信息缓存到local**

**引入依赖**
```
<dependency>
  <groupId>com.ctrip.framework.apollo</groupId>
  <artifactId>apollo-client</artifactId>
  <version>1.2.0</version>
</dependency>
```
具体代码如下：

**ApolloDictionary**

```
@Component
public class ApolloDictionary {

    /**
     * 获取配置，如果传入的是key是null或者""，直接返回null
     * @param key
     */
    public String get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return InitApolloLocalCacheBeanPostProcessor.get(key);
    }
}
```
**InitApolloLocalCache**

```
@Component
public class InitApolloLocalCache implements ApplicationListener<ContextRefreshedEvent> {
    // ContextRefreshedEvent 事件会在Spring容器初始化完成会触发该事件
    // namespace
    static final String LOAD_NAMESPACE = "commonConfig";
    /**
     * local cache
     */
    private static final Map<String, String> LOCAL_CACHE_MAP = new ConcurrentHashMap<>(16);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("===========initApolloLocalCache onApplicationEvent start=====================");
        try{
            if (event.getApplicationContext().getParent() != null) {
                return;
            }
            // 获取指定namespace下的配置
            Config config = ConfigService.getConfig(LOAD_NAMESPACE);
            if (Objects.isNull(config)) {
                return;
            }
            Set<String> propertyNames = config.getPropertyNames();
            if (CollectionUtils.isEmpty(propertyNames)){
                return;
            }
            propertyNames.forEach(propertyName -> {
                String value = config.getProperty(propertyName, "");
                LOCAL_CACHE_MAP.put(propertyName, value);
            });
            config.addChangeListener(changeEvent -> {
                Set<String> changedKeys = changeEvent.changedKeys();
                changedKeys.forEach(key -> {
                    ConfigChange change = changeEvent.getChange(key);
                    String newValue = change.getNewValue();
                    String oldVale = LOCAL_CACHE_MAP.get(key);
                    log.info("initApolloLocalCache listener key:[{}],oldValue:[{}],newValue:[{}]",key,oldVale,newValue);
                    LOCAL_CACHE_MAP.put(key, newValue);
                });
            });

            Set<String> keySet = LOCAL_CACHE_MAP.keySet();

            log.info("###############################################");
            log.info("#					                            #");
            keySet.forEach(s -> {
                String value = LOCAL_CACHE_MAP.get(s);
                log.info("#	 initApolloLocalCache init key:[{}] -> value:[{}]   #", s, value);
            });
            log.info("#					                            #");
            log.info("###############################################");

        }catch (Exception e){
            log.error("initApolloLocalCache apollo is error,e ", e);
        }
    }

    /**
     * @param key
     * @return
     */
    public static String get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return LOCAL_CACHE_MAP.get(key);
    }
}
```
**运行结果**
```
 [main] c.l.dictionary.InitApolloLocalCache      : ===========initApolloLocalCache onApplicationEvent start=====================
[main] c.l.dictionary.InitApolloLocalCache      : ###############################################
[main] c.l.dictionary.InitApolloLocalCache      : #                                              #
[main] c.l.dictionary.InitApolloLocalCache      : #   initApolloLocalCache init key:[class_status_modify_MQ_switch] -> value:[1]   #
[main] c.l.dictionary.InitApolloLocalCache      : #                                              #
[main] c.l.dictionary.InitApolloLocalCache      : ###############################################
[main] .d.InitApolloLocalCacheBeanPostProcessor : ================================
```

### Apollo推送原理

##### 应用程序是如何实时感知到Apollo的修改的？

![image](http://assets.processon.com/chart_image/5fddec7d5653bb4ccf3b0d56.png)

1. Apollo客户端和Apollo服务端保持长连接，从而能第一时间获得配置更新的推送
2. Apollo客户端会定时从Apollo服务端拉取应用的最新配置
    * fallback机制，防止推送机制失效导致配置不更新
    * Apollo客户端定时拉取上报本地版本，所以一般情况下，对于定时拉取的操作，服务端会返回304-Not Modified
    * 拉取频率默认为5分钟/次，客户端可以指定
3. Apollo客户端从Apollo服务端获取到应用的最新配置后，会保存在内存中
4. 客户端会把从服务端获取到的配置在本地文件系统缓存一份
    * 在遇到服务不可用，或网络不通的时候，依然能从本地恢复配置
5. 应用程序从Apollo客户端获取最新的配置、订阅配置更新通知