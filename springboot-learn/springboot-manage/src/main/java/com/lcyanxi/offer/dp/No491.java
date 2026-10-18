package com.lcyanxi.offer.dp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 递增子序列
 * 给定一个整型数组, 你的任务是找到所有该数组的递增子序列，递增子序列的长度至少是2。
 * <p>
 * 示例:
 * <p>
 * 输入: [4, 6, 7, 7]
 * 输出: [[4, 6], [4, 7], [4, 6, 7], [4, 6, 7, 7], [6, 7], [6, 7, 7], [7,7], [4,7,7]]
 * 说明:
 * <p>
 * 给定数组的长度不会超过15。
 * 数组中的整数范围是 [-100,100]。
 * 给定数组中可能包含重复数字，相等的数字应该被视为递增的一种情
 */
public class No491 {

    public static void main(String[] args) {
        int[] nums = {4, 6, 7, 7};
        System.out.println(findSubsequences(nums));
    }

    public static List<List<Integer>> findSubsequences(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> path = new ArrayList<>();

        core(nums, 0 , path, result);
        return result;

    }

    private static void core(int[] nums, int startIndex, List<Integer> path, List<List<Integer>> result) {
        if (path.size() >= 2) {
            result.add(new ArrayList<>(path));
        }
        HashSet<Integer> use = new HashSet<>();
        for (int i = startIndex; i < nums.length; i++) {
            if (!path.isEmpty() &&  nums[i] < path.get(path.size()-1)) {
                continue;
            }
            if (use.contains(nums[i])){
                continue;
            }
            use.add(nums[i]);
            path.add(nums[i]);
            core(nums, i + 1, path, result);
            path.remove(path.size() - 1);
        }
    }
}
