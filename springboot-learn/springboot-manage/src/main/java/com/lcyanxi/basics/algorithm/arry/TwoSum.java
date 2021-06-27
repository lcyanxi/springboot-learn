package com.lcyanxi.basics.algorithm.arry;

import com.google.common.collect.Lists;
import java.util.List;

/**
 * 和为s的两个数字
 * 描述：输入一个递增排序的数组和一个数字s，在数组中查找两个数，使得它们的和正好是s。如果有多对数字的和等于s，则输出任意一对即可。
 * eg:
 * 输入：nums = [2,7,11,15], target = 9
 * 输出：[2,7] 或者 [7,2]
 * @author lichang
 * @date 2020/12/1
 */
public class TwoSum {
    public static void main(String[] args) {
        int[] arr = {2,7,11,15};
        System.out.println(twoSum(arr,9));

        System.out.println(twoSum2(arr,9));
    }
    private static List<Integer> twoSum(int [] arr, int num){
        for (int i = 0; i < arr.length; i++){
            if (arr[i] > num){
                break;
            }
            for (int j = i + 1; j < arr.length; j++){
                if (arr[j] > num){
                    break;
                }
                if ((arr[j] + arr[i]) == num){
                    return Lists.newArrayList(arr[j],arr[i]);
                }
            }
        }
        return null;
    }

    // 双指针
    private static  List<Integer> twoSum2(int[] arr,int num){
        int left = 0;
        int right = arr.length - 1;
        while (left < right){
            if (arr[right] + arr[left] == num){
                return Lists.newArrayList(arr[left],arr[right]);
            }else if (arr[right] + arr[left] > num){
                right --;
            }else {
                left ++;
            }
        }
        return null;
    }
}
