package com.lcyanxi.basics.juc.future;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/04/19/3:47 下午
 */
public class CompletableFutureDemo {
    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(5);
        doSomething(executor, Lists.newArrayList(), Lists.newArrayList());
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("执行step 1");
            return "step1 result";
        }, executor);
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("执行step 2");
            return "step2 result";
        });
        cf1.thenCombine(cf2, (result1, result2) -> {
            System.out.println(result1 + " , " + result2);
            System.out.println("执行step 3");
            return "step3 result";
        }).thenAccept(System.out::println);
    }

    private static void doSomething(ExecutorService executor, List<Integer> resultList,
            List<CompletableFuture<Void>> futureList) {
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    System.out.println("------- 开始" + finalI);
                    int nextInt = new Random().nextInt(20);
                    Thread.sleep(nextInt * 1000);
                    System.out.println("-------" + finalI + "random" + nextInt);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (finalI == 4) {
                    int a = 4 / 0;
                }
            }, executor).handleAsync((result, e) -> {
                if (e != null) {
                    System.out.println("CompletableFuture处理异常 -> " + e);
                }
                return result;
            });
            futureList.add(future);
        }

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        System.out.println(futureList.size());
        System.out.println(resultList);
    }
}
