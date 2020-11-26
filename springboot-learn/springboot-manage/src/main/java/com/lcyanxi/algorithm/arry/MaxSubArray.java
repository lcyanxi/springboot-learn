package com.lcyanxi.algorithm.arry;

/**
 * 连续子数组的最大和
 * 输入一个整型数组，数组中的一个或连续多个整数组成一个子数组。求所有子数组的和的最大值。
 * 要求时间复杂度为O(n)。
 * eg：
 * 输入: nums = [-2,1,-3,4,-1,2,1,-5,4]
 * 输出: 6
 * 解释: 连续子数组 [4,-1,2,1] 的和最大，为 6。
 * @author lichang
 * @date 2020/11/26
 */
public class MaxSubArray {
    public static void main(String[] args) {
     int[] arr = {-2,1,-3,4,-1,2,1,-5,4};
        System.out.println(maxSubArray(arr));
        System.out.println(maxSubArray2(arr));
        int[] arr2 = {-2,1,-3,4,-1,2,1,-5,4};
        System.out.println(maxSubArray3(arr2));
    }

    // 时间复杂度o（n^2）
    private static int maxSubArray(int[] arr){
        int sum = 0;
        for (int i = 0; i < arr.length; i++){
            int temp = arr[i];
            for (int j = i + 1; j < arr.length; j++){
                temp = temp + arr[j];
                if (temp > sum){
                    sum = temp;
                }
            }
        }
        return sum;
    }
    // 动态规划
    private static int maxSubArray2(int[] arr){
        int sum = 0;
        for (int i = 1; i < arr.length; i++){
            arr[i] += Math.max(arr[i-1],0);
            sum = Math.max(sum,arr[i]);
        }
        return sum;
    }

    private static int maxSubArray3(int[] arr){
        if (arr == null || arr.length == 0){
            return 0;
        }
        int temp = 0;
        int sum = arr[0];
        for (int i = 1; i < arr.length; i++){
            if ((temp + arr[i]) < arr[i]){
                temp = arr[i];
            }else {
                temp += arr[i];
            }
            sum = Math.max(sum,temp);
        }
        return sum;
    }

}
