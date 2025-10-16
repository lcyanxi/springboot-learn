package com.lcyanxi.offer.dp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 给定一个只包含正整数的非空数组。是否可以将这个数组分割成两个子集，使得两个子集的元素和相等。
 *
 * 注意: 每个数组中的元素不会超过 100 数组的大小不会超过 200
 *
 * 示例 1:
 *
 * 输入: [1, 5, 11, 5]
 * 输出: true
 * 解释: 数组可以分割成 [1, 5, 5] 和 [11].
 * 示例 2:
 *
 * 输入: [1, 2, 3, 5]
 * 输出: false
 * 解释: 数组不能分割成两个元素和相等的子集.
 * @author chang.li
 * @date 2025/10/16
 * @version 1.0
 */
public class No416 {

    public static void main(String[] args) {
        System.out.println(process(new int[]{1, 2, 3, 5}));
    }

    private static boolean process(int[] nums) {
        int sum = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
        }
        if (sum % 2 != 0) {
            return false;
        }
        Arrays.sort(nums);
        int target = sum / 2;
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack(res, list, nums, 0,0, target, used);

        System.out.println(res);
        return res.size() > 0;
    }

    private static void backtrack(List<List<Integer>> res, List<Integer> list, int[] nums,int sum, int index, int target, boolean[] used) {
        if (sum == target) {
            res.add(new ArrayList<>(list));
        }
        if (sum > target) {
            return;
        }
        for (int i = index; i < nums.length; i++) {
            if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) {
                continue;
            }
            if (!used[i]) {
                used[i] = true;
                list.add(nums[i]);
                sum+=nums[i];
                backtrack(res, list, nums,sum, i, target, used);
                used[i] = false;
                list.remove(list.size() - 1);
                sum-=nums[i];
            }
        }
    }

}
