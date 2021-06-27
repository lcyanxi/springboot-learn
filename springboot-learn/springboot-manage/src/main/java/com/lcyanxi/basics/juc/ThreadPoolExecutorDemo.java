package com.lcyanxi.basics.juc;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.util.CollectionUtils;

/**
 * 模拟  future.get()会阻塞
 * @author lichang
 * @date 2020/11/14
 */
public class ThreadPoolExecutorDemo {
    private static final String SUCCESS = "success";

    public static void main(String[] args) {
        // 构造一个线程池
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 6, 3,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(3));

        List<Future<String>> objects = Lists.newArrayList();

        System.out.println("------------------任务开始执行---------------------");
        for (int i = 0; i < 5; i++){
            Future<String> future = threadPool.submit(() -> {
                try {
                    String name = Thread.currentThread().getName();
                    long time = (long) (Math.random()*10);
                    System.out.println(name + "开始睡眠" + time +"ms");
                    TimeUnit.SECONDS.sleep(time);
                    System.out.println(name + "submit方法执行任务完成");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return SUCCESS;
            });
            objects.add(future);
        }
        threadPool.shutdown();

        if (!CollectionUtils.isEmpty(objects)){
            objects.forEach(future -> {
                String s = null;
                try {
                    s = future.get();
                    System.out.println("获取到数据" + s);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }

        System.out.println("-------------------main thread end---------------------");
    }
}



