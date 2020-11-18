package com.lcyanxi.algorithm;

import java.util.Objects;
import java.util.PriorityQueue;

/**
 * 最小的k个数
 * 描述：给定一个数组nums 和 整数k，找出nums中最小的k个数
 * eg： 输入：nums = [2,4,1,7,6,10] 和 k = 3  输出：[1,2,4]或者[2,4,1]等
 * @author lichang
 * @date 2020/11/18
 */
public class MinKNum {
    public static void main(String[] args) {
        Integer[] arr = {11,1,2,4,1,7,6,10};
        Integer[] topN2 = findMinTopN2(3, arr);
        for (int i = 0 ; i < topN2.length;i++){
            System.out.print(topN2[i]);
        }
    }

    /**
     * 优先级队列实现
     * add : 向队尾插入元素，失败则抛出异常
     * offer : 向队尾插入元素，成功则放回true
     * remove 获取并删除队首元素，失败则抛出异常
     * poll : 获取并删除队首元素，失败则返回null
     * element : 获取队首元素,失败则抛出异常
     * peek : 获取队首元素，失败则返回null
     * @param k 元素个数
     * @param arr 数组
     * @return
     */
    private static Integer[] findMinTopN2(int k, Integer [] arr){
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>((k1,k2)->k2.compareTo(k1));
        for (Integer e : arr){
            // peek ：获取队首元素
            if (priorityQueue.size() <= k || (Objects.nonNull(priorityQueue.peek()) && e < priorityQueue.peek())){
                priorityQueue.offer(e);
            }
            if (priorityQueue.size() > k){
                //获取并删除队首元素
                priorityQueue.poll();
            }
        }
        Integer[] newArr = new Integer[k];
        for (int i = 0;i < newArr.length; i++){
            newArr[i] = priorityQueue.poll();
        }
        return newArr;
    }
}
