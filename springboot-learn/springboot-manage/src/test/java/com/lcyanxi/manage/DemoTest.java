package com.lcyanxi.manage;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lcyanxi.model.User;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


/**
 * @author lichang
 *         Date: 2021/09/23/2:53 下午
 */
@Slf4j
public class DemoTest {

    public static void main(String[] args) {

        List<User> users = Lists.newArrayList();
        for (int i = 0 ; i < 10 ; i++){
            String name = "name_"+ i;
            users.add(User.builder().userName(name).build());
        }
        users.forEach(user -> {
            log.info("start" + user.getUserName());
            long time = (long) (Math.random()*10);
            try {
                log.info("start" + user.getUserName() + time);
                TimeUnit.SECONDS.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        ThreadFactory threadFactory =
                new ThreadFactoryBuilder().setNameFormat("DemoTest-%d").build();
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(2 * Runtime.getRuntime().availableProcessors() + 1,
                2 * Runtime.getRuntime().availableProcessors() + 1, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue(100),
                threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());

        List<String> SECTION_TYPE_LIST = Lists.newArrayList("aa", "bb", "cc");
        Map<String, Future<String>> sectionFutureMap = new HashMap<>();
        for (String sectionType : SECTION_TYPE_LIST) {
            Future<String> sectionFuture = executorService.submit(() -> {
                log.info("task key:{} is start thread id:{}", sectionType, Thread.currentThread().getId());
                if (sectionType.equals("aa")) {
                    while (true){
                        System.out.println("aa is run .........");

                    }
//                    TimeUnit.MINUTES.sleep(11);
//                    log.error("Shadow route detail section service is null，section type: {}", sectionType);
//                    return null;
                }

                return sectionType;
            });
            sectionFutureMap.put(sectionType, sectionFuture);
        }
        log.error("main Thread currentThread after11 id:{}=========", Thread.currentThread().getId());
        Map<String, String> sectionMap = Maps.newHashMap();
        for (Map.Entry<String, Future<String>> entry : sectionFutureMap.entrySet()) {
            String key = entry.getKey();
            Future<String> future = entry.getValue();
            try {
                String value = future.get(10, TimeUnit.SECONDS);
                log.info("result key:{},value:{}", key, value);
                sectionMap.put(key, value);
            } catch (Exception e) {
                printThreadPoolStatus(executorService,"队列改变之前");
                log.error("Thread currentThread before id:{} future:{}", Thread.currentThread().getId(),
                        JSON.toJSONString(future));
                Thread.currentThread().interrupt();
                future.cancel(true);
                printThreadPoolStatus(executorService,"队列改变之后");
                log.error("Thread currentThread after id:{} future:{}", Thread.currentThread().getId(),
                        JSON.toJSONString(future));
            }
        }

        log.error("main Thread currentThread after22 id:{}=========", Thread.currentThread().getId());

        log.error("sectionMap :{}", sectionMap);
    }

    private static void printThreadPoolStatus(ThreadPoolExecutor threadPoolExecutor, String message) {
        LinkedBlockingQueue queue = (LinkedBlockingQueue) threadPoolExecutor.getQueue();
        System.out.println(
                Thread.currentThread().getName() + "_" + ":" + message + "-:" +
                        "核心线程数:" + threadPoolExecutor.getCorePoolSize() +
                        "-活动线程数:" + threadPoolExecutor.getActiveCount() +
                        "-最大线程数:" + threadPoolExecutor.getMaximumPoolSize() +
                        "-线程池活跃度:" + divide(threadPoolExecutor.getActiveCount(), threadPoolExecutor.getMaximumPoolSize())
                        +
                        "-任务完成数:" + threadPoolExecutor.getCompletedTaskCount() +
                        "-队列大小:" + (queue.size() + queue.remainingCapacity()) +
                        "-当前线程排队线程数:" + queue.size() +
                        "-队列剩余大小:" + queue.remainingCapacity() +
                        "-队列使用度:" + divide(queue.size(), queue.size() + queue.remainingCapacity()));

    }

    private static String divide(int num1, int num2) {
        return String.format("%1.2f%%", Double.parseDouble(num1 + "") / Double.parseDouble(num2 + "") * 100);
    }
}
