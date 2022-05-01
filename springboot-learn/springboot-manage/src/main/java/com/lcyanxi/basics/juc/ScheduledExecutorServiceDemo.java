package com.lcyanxi.basics.juc;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.ThreadFactoryImpl;

/**
 * @author : lichang
 * @desc : 可在指定延迟后或周期性地执行线程任线程池
 * @since : 2022/05/01/9:39 上午
 */
@Slf4j
public class ScheduledExecutorServiceDemo {
    private static final ScheduledExecutorService executor =
            Executors.newScheduledThreadPool(3, new ThreadFactoryImpl("ScheduledExecutorServiceDemo_"));
    private static final ScheduledExecutorService executor1 =
            Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl("ScheduledExecutorServiceDemo_"));
    private static final ScheduledExecutorService executor2 =
            new ScheduledThreadPoolExecutor(3, new ThreadFactoryImpl("ScheduledExecutorServiceDemo_"));

    public static void main(String[] args) {
//        schedule();
        scheduleWithFixedDelay();
//        scheduleAtFixedRate();
    }


    public static void schedule() {
        executor.schedule(
                () -> log.info("run schedule @ {}", LocalDateTime.now()),
                1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 延迟 5s 开始执行，每隔 3s 执行一次 (不等上一个任务执行完毕)
     */
    @SneakyThrows
    public static void scheduleAtFixedRate() {
        executor.scheduleAtFixedRate(
                () -> {
                    try {
                        Thread.sleep(5000); // 执行时间超过执行周期
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    log.info("run scheduleAtFixedRate threadName {} @ {}", Thread.currentThread().getName(),
                            LocalDateTime.now());
                }, 5, 3, TimeUnit.SECONDS);
    }

    /**
     * 上次执行完成后，延迟多久执行周期性的执行
     */
    @SneakyThrows
    public static void scheduleWithFixedDelay() {
        executor.scheduleWithFixedDelay(
                () -> {
                    try {
                        Thread.sleep(1000); // 执行时间超过执行周期
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    log.info("run scheduleWithFixedDelay threadName {} @ {}", Thread.currentThread().getName(),
                            LocalDateTime.now());
                }, 5, 3, TimeUnit.SECONDS);
    }
}
