package com.lcyanxi.offer.greedy;

import java.util.ArrayList;
import java.util.List;

/**
 * 给定一个整数数组 nums ，找到一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。
 * 示例:
 * 输入: [-2,1,-3,4,-1,2,1,-5,4]
 * 输出: 6
 * 解释:  连续子数组  [4,-1,2,1] 的和最大，为  6。
 *
 * @author chang.li
 * @version 1.0
 * @date 2025/9/28
 */
public class No53 {
    public static void main(String[] args) {
        int[] nums = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        System.out.println(process(nums));

        System.out.println(process2(nums));
    }

    private static Integer process(int[] arr) {
        int max = arr[0];
        int sum = 0;
        for (int i = 1; i < arr.length; i++) {
            sum = Math.max(sum + arr[i], 0);
            max = Math.max(max, sum);
        }
        return max;
    }

    private static List<Integer> process2(int[] arr) {
        List<Integer> res = new ArrayList<>();
        List<Integer> items = new ArrayList<>();
        int max = arr[0];
        int sum = 0;
        for (int i = 1; i < arr.length; i++) {
            int temp = arr[i] + sum;
            if (temp < 0){
                items = new ArrayList<>();
                sum = 0;
            }else {
                sum = temp;
                items.add(arr[i]);
            }

            if (max < sum){
                max = sum;
                res = new ArrayList<>(items);
            }
        }
        return res;
    }
}
