package com.lcyanxi.basics.algorithm.arry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;

/**
 * 查找N/K次数的数
 * 描述：给定一个长度为N的数组，和一个大于1的正整数K，如果有那些数次数大于N/K,就放回这些数
 * 要求：时间复杂度O(N),额外空间复杂度O(k)
 * eg:
 *    输入：arr = [4,1,7,5,5,5,8,2,7]  k = 3
 *    输出：[5]
 * @author lichang
 * @date 2020/12/7
 */
public class FindNKForArray {
    public static void main(String[] args) {
        int[] arr = {4,1,7,5,5,5,8,2,7};

        System.out.println(findNKForArray(arr,3));
    }
    private static List<Integer> findNKForArray(int[] arr,int K){
        List<Integer> list = Lists.newArrayList();
        int M = arr.length;
        if (M < K){
            return list;
        }
        int target = M/K;
        // 关键在于这儿  是O(k)
        Map<Integer,Integer> map = Maps.newHashMap();
        for (int i = 0; i < arr.length; i++){
            if (map.containsKey(arr[i])){
                map.put(arr[i],map.get(arr[i]) + 1);
            }else {
                if (map.size() == K - 1){
                    // 如果满了所有元素减一，移除V = 0 的
                    findNKForArrayUtil(map);
                }else {
                    map.put(arr[i],1);
                }
            }
        }
        Map<Integer, Integer> realData = getRealData(arr, map);
        realData.forEach((k,v)->{
            if (v >= target){
                list.add(k);
            }
        });
        return list;
    }
    private static void findNKForArrayUtil(Map<Integer,Integer> map){
        List<Integer> list = Lists.newArrayList();
        map.forEach((k,v)->{
            if (v == 1){
                list.add(k);
            }else {
                map.put(k,v - 1);
            }
        });
        for (Integer e : list){
            map.remove(e);
        }
    }
    private static Map<Integer,Integer> getRealData(int[] arr,Map<Integer,Integer> map){
        Map<Integer,Integer> integerMap = Maps.newHashMap();
        for (int i = 0; i < arr.length ; i++){
            if (map.containsKey(arr[i])){
                if (integerMap.containsKey(arr[i])){
                    integerMap.put(arr[i],integerMap.get(arr[i]) + 1);
                }else {
                    integerMap.put(arr[i],1);
                }
            }
        }
        return integerMap;
    }
}
