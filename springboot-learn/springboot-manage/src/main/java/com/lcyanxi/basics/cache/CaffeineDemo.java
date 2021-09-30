package com.lcyanxi.basics.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2021/09/26/10:52 上午
 */
@Slf4j
public class CaffeineDemo {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Cache<String, String> cache = Caffeine.newBuilder()
                .initialCapacity(10) // 初始大小
                .maximumSize(100)  // 最大数量
                .expireAfterWrite(10, TimeUnit.SECONDS) // 过期时间
                .recordStats()
                .build();
        // 1.如果缓存中能查到，则直接返回
        // 2.如果查不到，则从我们自定义的getValue方法获取数据，并加入到缓存中
        for (int i = 0; i < 10 ; i++){
            TimeUnit.MILLISECONDS.sleep(20);
            String val = cache.get("java金融", CaffeineDemo::getValue);
            System.out.println(val);
        }


        LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                //写之后30ms过期
                .expireAfterWrite(30L, TimeUnit.MILLISECONDS)
                //访问之后30ms过期
                .expireAfterAccess(30L, TimeUnit.MILLISECONDS)
                //20ms之后刷新
                .refreshAfterWrite(20L, TimeUnit.MILLISECONDS)
                //开启weakKey key 当启动垃圾回收时，该缓存也被回收
                .weakKeys()
                .build(createCacheLoader());

        System.out.println(loadingCache.get("hello"));
        loadingCache.put("hello1", "我是hello1");
        System.out.println(loadingCache.get("hello1"));
        loadingCache.put("hello1", "我是hello2");
        System.out.println(loadingCache.get("hello1"));


        com.google.common.cache.Cache<String, String> cache2 = CacheBuilder.newBuilder()
                .maximumSize(100)
                //写之后5s过期
                .expireAfterWrite(5, TimeUnit.MILLISECONDS)
                .concurrencyLevel(1)
                .build();
        cache.put("hello1", "我是hello1");
        cache.put("hello2", "我是hello2");
        cache.put("hello3", "我是hello3");
        cache.put("hello4", "我是hello4");
        //至少睡眠5ms
        Thread.sleep(5);
        System.out.println(cache2.size());
        String hello1 = cache2.getIfPresent("hello1");
        System.out.println(hello1);
        System.out.println(cache2.size());
        cache.put("hello5", "我是hello5");
        System.out.println(cache2.size());



    }

    public static com.google.common.cache.CacheLoader<String, String> createCacheLoader() {
        return new com.google.common.cache.CacheLoader<String, String>() {
            @Override
            public String load(String key) throws Exception {
                return key;
            }
        };
    }


    /**
     * 缓存中找不到，则会进入这个方法。一般是从数据库获取内容
     * @param k
     * @return
     */
    private static String getValue(String k) {
        log.info("getValue key:{}",k);
        return k + ":value";
    }
}
