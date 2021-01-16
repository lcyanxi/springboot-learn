package com.lcyanxi.java.stream;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lcyanxi.model.UserLesson;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

/**
 * @author lichang
 * @date 2021/1/16
 */
@Slf4j
public class ParallelStreamDemo {
    public static void main(String[] args) {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("parallelStreamDemo-pool-%d").build();
        ExecutorService threadPoolExecutor = new ThreadPoolExecutor(3, 5,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

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
        log.info("parallelStreamDemo main thread is done temp:[{}]",temp);
    }

    private static List<UserLesson> findByIds(List<Integer> userIdSet,List<Integer> ids){
        long time = (long) (Math.random()*10);
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("findByIds userIdSet:[{}],time:[{}]",userIdSet,time);
        return userIdSet.parallelStream().map((userId -> {
            UserLesson userLesson = new UserLesson();
            userLesson.setUserId(userId);
            return userLesson;
        })).collect(Collectors.toList());
    }
}
