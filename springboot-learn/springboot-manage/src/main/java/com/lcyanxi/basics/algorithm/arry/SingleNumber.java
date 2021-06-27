package com.lcyanxi.basics.algorithm.arry;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;

/**
 * 数组中数字出现的次数 II
 * 描述：在一个数组 nums 中除一个数字只出现一次之外，其他数字都出现了三次。请找出那个只出现一次的数字。
 * 输入：nums = [9,1,7,9,7,9,7]
 * 输出：1
 * @author lichang
 * @date 2020/12/1
 */
public class SingleNumber {
    public static void main(String[] args) {
        int arr[] = {9,1,7,9,7,9,7};
        System.out.println(singleNumber(arr));
    }
    // 算了  就只会这种最简单的解法了
    private static int  singleNumber(int[] arr){
        HashMap<Integer, Integer> hashMap = Maps.newHashMap();
        for (int e : arr){
            hashMap.put(e,hashMap.getOrDefault(e,0) + 1);
        }
        for (Map.Entry<Integer,Integer> entrySet: hashMap.entrySet()){
            if (entrySet.getValue() == 1 ){
                return entrySet.getKey();
            }
        }
        return 0;
    }

}
