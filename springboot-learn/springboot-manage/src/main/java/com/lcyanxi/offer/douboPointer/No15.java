package com.lcyanxi.offer.douboPointer;

import java.util.*;

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
        int[] nums = {-2,0,1,1,2};
        System.out.println(process(nums));
        System.out.println(process2(nums));

    }

    /**
     * i ： [i+1 , end] 双指针解决
     */
    private static List<List<Integer>> process(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        Arrays.sort(nums);
        for (int i = 0; i < nums.length; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            handle(nums, nums[i], i + 1, nums.length - 1, res);
        }
        return res;
    }

    private static void handle(int[] nums, int target, int start, int end, List<List<Integer>> res) {
        while (start < end) {
            int sum = target + nums[start] + nums[end];
            if (sum == 0) {
                List<Integer> item = Arrays.asList(target, nums[start], nums[end]);
                res.add(item);
                // 去重 解决 0 0 0 0 0 情况
                while (start < end && nums[end] == nums[end-1]){
                    end--;
                }
                if (start < end && nums[start] == nums[start+1]){
                    start++;
                }
                start++;
                end--;
            } else if (sum > 0) {
                end--;
            } else {
                start++;
            }
        }
    }

    private static List<List<Integer>> process2(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        Arrays.sort(nums);
        for (int i = 0; i < nums.length; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            if (nums[i] > 0) {
                break;
            }
            Set<Integer> set = new HashSet<>();
            for (int j = i + 1; j < nums.length; j++) {
                int target = -(nums[i] + nums[j]);
                if (set.contains(target)) {
                    List<Integer> item = Arrays.asList(target, nums[i], nums[j]);
                    res.add(item);
                    set.remove(target);
                }else {
                    set.add(nums[j]);
                }
            }
        }

        return res;
    }
}
