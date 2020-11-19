package com.lcyanxi.algorithm;

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
}
