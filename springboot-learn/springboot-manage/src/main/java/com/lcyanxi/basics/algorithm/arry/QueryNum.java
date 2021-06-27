package com.lcyanxi.basics.algorithm.arry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;

/**
 * 指定范围查询
 * 描述：数组为{3,2,2,3,1},查询为（0,3,2）,意思是在数组里下标0-3这个范围上，有几个2？ 返回2。
 * 假设给你一个数组arr，对这个数组的查询非常频繁，请返回所有查询结果
 * eg：
 * 输入：arr={3,2,2,3,1}, startId = 0, endId = 3, queryId = 2    输出：2
 * @author lichang
 * @date 2020/12/3
 */
public class QueryNum {
    static HashMap<Object, List<Integer>>  map;
    public static void main(String[] args) {
        int[] arr = {3,2,2,3,1,4,5,6,7,2,3,4,2,2,3,2,3,2,4,9,11,22,2,2,1,2,11};
        System.out.println(queryNum(arr,0,24,2));
        initData(arr);
        System.out.println(queryNum2(0,24,2));
    }

    /**
     * 满足不了这个条件：对这个数组的查询非常频繁，加入这个数组我要查询100万次怎么办？？
     */
    private static int queryNum(int[] arr,int startId,int endId,int queryId){
        if (arr == null ){
            return 0;
        }
        int sign = 0;
        for (;startId <= endId; startId++){
            if (arr[startId] == queryId){
                sign ++;
            }
        }
        return sign;
    }

    private static void initData(int[] arr){
        map = Maps.newHashMap();
        for (int i = 0;  i < arr.length ; i++){
            List<Integer> list = map.getOrDefault(arr[i], Lists.newArrayList());
            list.add(i);
            map.put(arr[i],list);
        }
    }
    private static int queryNum2(int startId,int endId,int queryId){
        if (!map.containsKey(queryId)){
            return 0;
        }
        // 目标queryId 所有所在的数组下标
        List<Integer> integers = map.get(queryId);
        //问题 转换为 有序数组里查找指定范围的数据
        int a = queryNumUtil(startId, integers);
        int b = queryNumUtil(endId,integers);
        return b - a;
    }

    // 二分范围查找
    private static int queryNumUtil(int indexId, List<Integer> list){
        int left = 0;
        int right = list.size() - 1;
        int mostRight = -1;
        while (left <= right){
            int mid = (left + right)/2;
            if (list.get(mid) > indexId){
                right = mid - 1;
            }else {
                mostRight = mid;
                left = mid + 1;
            }
        }
        return mostRight + 1;
    }
}
