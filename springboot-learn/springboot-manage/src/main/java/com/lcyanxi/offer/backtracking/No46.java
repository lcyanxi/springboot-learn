package com.lcyanxi.offer.backtracking;

import java.util.ArrayList;
import java.util.List;

/**
 * 给定一个 没有重复 数字的序列，返回其所有可能的全排列。
 * <p>
 * 示例:
 * <p>
 * 输入: [1,2,3]
 * 输出: [ [1,2,3], [1,3,2], [2,1,3], [2,3,1], [3,1,2], [3,2,1] ]
 *
 * @author chang.li
 * @version 1.0
 * @date 2025/10/9
 */
public class No46 {
    public static void main(String[] args) {
        int[] nums = {1, 2, 3};
        System.out.println(process(nums));

    }

    private static List<List<Integer>> process(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> items = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack(res, items, used, nums);
        return res;
    }

    private static void backtrack(List<List<Integer>> res, List<Integer> items, boolean[] used, int[] nums) {
        if (items.size() == nums.length) {
            res.add(new ArrayList<>(items));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) {
                continue;
            }
            items.add(nums[i]);
            used[i] = true;
            backtrack(res, items, used, nums);
            used[i] = false;
            items.remove(items.size() - 1);
        }
    }

}
