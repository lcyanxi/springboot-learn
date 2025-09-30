package com.lcyanxi.offer.backtracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 给定一个可能包含重复元素的整数数组 nums，返回该数组所有可能的子集（幂集）。
 * 说明：解集不能包含重复的子集。
 * 示例:
 * <p>
 * 输入: [1,2,2]
 * 输出: [ [2], [1], [1,2,2], [2,2], [1,2], [] ]
 *
 * @author chang.li
 * @version 1.0
 * @date 2025/9/30
 */
public class No90 {
    public static void main(String[] args) {
        int[] nums = {1, 2, 2};
        System.out.println(subsetsWithDup(nums));
    }

    public static List<List<Integer>> subsetsWithDup(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> items = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack(res, items, used, nums, 0);
        return res;
    }

    private static void backtrack(List<List<Integer>> res, List<Integer> items, boolean[] used, int[] nums, int index) {
        if (items.size() > nums.length) {
            return;
        }
        res.add(new ArrayList<>(items));
        for (int i = index; i < nums.length; i++) {
            if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) {
                continue;
            }
            if (used[i]) {
                continue;
            }
            used[i] = true;
            items.add(nums[i]);
            backtrack(res, items, used, nums, i + 1);
            used[i] = false;
            items.remove(items.size() - 1);
        }
    }
}
