package com.lcyanxi.offer.douboPointer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 给你一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？请你找出所有满足条件且不重复的三元组。
 *
 * 注意： 答案中不可以包含重复的三元组。
 *
 * 示例：
 *
 * 给定数组 nums = [-1, 0, 1, 2, -1, -4]，
 *
 * 满足要求的三元组集合为： [ [-1, 0, 1], [-1, -1, 2] ]
 *
 * #
 * @author chang.li
 * @date 2025/10/11
 * @version 1.0
 */
public class No15 {
    public static void main(String[] args) {
        int[] nums = {-1, 0, 1, 2, -1, -4};
        System.out.println(process(nums));

    }

    private static List<List<Integer>> process(int[] nums) {
        /**
         * i ： [i+1 , end] 双指针解决
         */
        List<List<Integer>> res = new ArrayList<>();
        Arrays.sort(nums);
        for (int i = 0; i < nums.length; i++) {
            handle(nums, nums[i], i + 1, nums.length - 1, res);
        }
        return res;
    }

    private static void handle(int[] nums, int target, int start, int end, List<List<Integer>> res) {
        while (start < end) {
            int sum = target + nums[start] + nums[end];
            if (sum == 0) {
                List<Integer> item = new ArrayList<>();
                item.add(target);
                item.add(nums[start]);
                item.add(nums[end]);
                res.add(item);
                return;
            } else if (sum > 0) {
                end--;
            } else {
                start++;
            }
        }
    }
}
