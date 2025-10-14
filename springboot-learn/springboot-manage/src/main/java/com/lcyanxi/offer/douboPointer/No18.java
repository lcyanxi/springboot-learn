package com.lcyanxi.offer.douboPointer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 题意：给定一个包含 n 个整数的数组 nums 和一个目标值 target，判断 nums 中是否存在四个元素 a，b，c 和 d ，使得 a + b + c + d 的值与 target 相等？找出所有满足条件且不重复的四元组。
 *
 * 注意：
 *
 * 答案中不可以包含重复的四元组。
 *
 * 示例： 给定数组 nums = [1, 0, -1, 0, -2, 2]，和 target = 0。 满足要求的四元组集合为： [ [-1, 0, 0, 1], [-2, -1, 1, 2], [-2, 0, 0, 2] ]
 * @author chang.li
 * @date 2025/10/14
 * @version 1.0
 */
public class No18 {
    public static void main(String[] args) {
        int[] nums = {2, 2, 2, 2, 2};
        System.out.println(process(nums, 8));
    }

    private static List<List<Integer>> process(int[] nums, int target) {
        List<List<Integer>> res = new ArrayList<>();
        if (nums == null || nums.length < 4) {
            return res;
        }
        Arrays.sort(nums);
        for (int k = 0; k < nums.length; k++) {
            if (nums[k] > 0 && nums[k] > target){
                break;
            }
            if (k > 0 && nums[k] == nums[k - 1]) {
                continue;
            }
            for (int i = k + 1; i < nums.length; i++) {
                if (i > k + 1 && nums[i] == nums[i - 1]) {
                    continue;
                }
                handle(nums, target, nums[k], nums[i], i + 1, res);
            }
        }

        return res;
    }

    private static void handle(int[] nums, int target, int num1, int num2, int left, List<List<Integer>> res) {
        int right = nums.length - 1;
        while (left < right) {
            int total = num1 + num2 + nums[left] + nums[right];
            if (total == target) {
                res.add(Arrays.asList(num1, num2, nums[left], nums[right]));
                right--;
                left++;
            } else if (total > target) {
                right--;
            } else {
                left++;
            }
        }
    }
}













