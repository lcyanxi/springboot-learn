package com.lcyanxi.basics.java.stream;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lcyanxi.model.UserLesson;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lichang
 * @date 2021/1/16
 */
@Slf4j
public class ParallelStreamDemo {
    public static void main(String[] args) throws InterruptedException {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("parallelStreamDemo-pool-%d").build();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 5,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(5), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        List<Integer>  userIdSet = Lists.newArrayList();
        List<Integer>  ids = Lists.newArrayList();
        for (int i = 0; i < 10; i++){
            userIdSet.add(i);
            ids.add(i + 10);
        }
        List<UserLesson> temp = Lists.newArrayList();
        userIdSet.parallelStream().forEach(userIdShardingKey -> {
            try {
                log.info("threadPoolTaskExecutor userIdShardingKey :[{}] submit task ",userIdShardingKey);
                printThreadPoolStatus(threadPoolExecutor,"队列改变之前");
                //并发走
                Future<List<UserLesson>> futureMainClassInfoCountMap =
                        threadPoolExecutor.submit(() -> findByIds(Lists.newArrayList(userIdShardingKey), ids));
                List<UserLesson> userLessonList = futureMainClassInfoCountMap.get();
                for (int i = 0; i < userLessonList.size(); i++) {
                    UserLesson userLesson = userLessonList.get(i);
                    temp.add(userLesson);
                }
                log.info("threadPoolTaskExecutor userIdShardingKey :[{}] is done",userIdShardingKey);
            }catch (InterruptedException e) {
                log.error("doFindUserIdsAndIds 线程被打断",e);
            } catch (ExecutionException e) {
                log.error("doFindUserIdsAndIds error",e);
            }
        });
        threadPoolExecutor.setCorePoolSize(10);
        threadPoolExecutor.setMaximumPoolSize(10);
        printThreadPoolStatus(threadPoolExecutor,"队列改变之后");
        Thread.currentThread().join();
        log.info("parallelStreamDemo main thread is done temp:[{}]",temp);
    }

    private static List<UserLesson> findByIds(List<Integer> userIdSet,List<Integer> ids){
        long time = (long) (Math.random()*10);
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        if (userIdSet.contains(5)){
//            log.info("userId:[{}] is death data",userIdSet);
//            while (true){}
//        }
        log.info("findByIds userIdSet:[{}],time:[{}]",userIdSet,time);
        return userIdSet.parallelStream().map((userId -> {
            UserLesson userLesson = new UserLesson();
            userLesson.setUserId(userId);
            return userLesson;
        })).collect(Collectors.toList());
    }

    private static void printThreadPoolStatus(ThreadPoolExecutor threadPoolExecutor,String message){
        LinkedBlockingQueue queue = (LinkedBlockingQueue) threadPoolExecutor.getQueue();
        System.out.println(
                Thread.currentThread().getName() + "_" + ":" + message + "-:" +
                        "核心线程数:" + threadPoolExecutor.getCorePoolSize() +
                        "活动线程数:" + threadPoolExecutor.getActiveCount() +
                        "最大线程数:" + threadPoolExecutor.getMaximumPoolSize() +
                        "线程池活跃度:" + divide(threadPoolExecutor.getActiveCount(),threadPoolExecutor.getMaximumPoolSize()) +
                        "任务完成数:" + threadPoolExecutor.getCompletedTaskCount() +
                        "队列大小:" + (queue.size() + queue.remainingCapacity()) +
                        "当前线程排队线程数:" + queue.size() +
                        "队列剩余大小:" + queue.remainingCapacity() +
                        "队列使用度:" + divide(queue.size(),queue.size() + queue.remainingCapacity()));

    }

    private static String divide(int num1, int num2){
        return String.format("%1.2f%%",Double.parseDouble(num1 + "")/Double.parseDouble(num2 + "")*100);
    }
}
