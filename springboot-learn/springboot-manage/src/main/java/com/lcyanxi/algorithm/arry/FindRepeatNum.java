package com.lcyanxi.algorithm.arry;

import java.util.HashSet;
import java.util.Set;

/**
 * 找出数组中重复的数字
 * 描述：在一个长度为 n 的数组 nums 里的所有数字都在 0～n-1 的范围内。数组中某些数字是重复的，
 *      但不知道有几个数字重复了，也不知道每个数字重复了几次。请找出数组中任意一个重复的数字。
 * eg:
 * 输入：
 * [2, 3, 1, 0, 2, 5, 3]
 * 输出：2 或 3
 * @author lichang
 * @date 2020/11/19
 */
public class FindRepeatNum {
    public static void main(String[] args) {
        Integer[] arr = {4,1,0,2,5,3,3};
        System.out.println(findRepeatNum(arr));
        System.out.println(findRepeatNum2(arr));
        int[] arr1 = {4,1,0,2,5,3,3};
        System.out.println(findRepeatNum3(arr1));


    }
    private static Integer findRepeatNum(Integer[] arr){
        // nums 里的所有数字都在 0～n-1 的范围内
        int temp = 0;
        for (int i = 0; i < arr.length; i++){
            while (i != arr[i]){
                if (arr[i] == arr[arr[i]]){
                    return arr[i];
                }
                temp = arr[i];
                arr[i] = arr[temp];
                arr[temp] = temp;
            }
        }
        return -1;
    }

    private static Integer findRepeatNum2(Integer arr []){
        Set<Integer> set = new HashSet<>();
        for (Integer e : arr){
            if (!set.add(e)){
                return e;
            }
        }
        return -1;
    }

    /**
     * 将数组映射到一个新数组的位子
     * @param arr 数组
     * @return
     */
    private static Integer findRepeatNum3(int[] arr){
        int[] newArr = new int[arr.length];
        for (Integer e : arr){
            newArr[e] ++ ;
            if (newArr[e] > 1){
                return e;
            }
        }
        return -1;
    }
}
