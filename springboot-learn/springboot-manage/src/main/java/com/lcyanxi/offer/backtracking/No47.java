package com.lcyanxi.offer.backtracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 给定一个可包含重复数字的序列 nums ，按任意顺序 返回所有不重复的全排列。
 * <p>
 * 示例 1：
 * <p>
 * 输入：nums = [1,1,2]
 * 输出： [[1,1,2], [1,2,1], [2,1,1]]
 * 示例 2：
 * <p>
 * 输入：nums = [1,2,3]
 * 输出：[[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
 *
 * @author chang.li
 * @version 1.0
 * @date 2025/9/29
 */
public class No47 {
    public static void main(String[] args) {
        System.out.println(process(new int[]{1, 2, 3}));
    }

    private static List<List<Integer>> process(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> temp = new ArrayList<>();
        boolean visit[] = new boolean[nums.length];
        core(result, temp, nums, visit);
        return result;

    }

    private static void core(List<List<Integer>> result, List<Integer> temp, int[] nums, boolean visit[]) {
        if (temp.size() == nums.length) {
            result.add(new ArrayList<>(temp));
        }
        for (int i = 0; i < nums.length; i++) {
            if (i > 0 && nums[i] == nums[i - 1] && !visit[i - 1]) {
                continue;
            }
            if (visit[i]) {
                continue;
            }
            visit[i] = true;
            temp.add(nums[i]);
            core(result, temp, nums, visit);
            visit[i] = false;
            temp.remove(temp.size() - 1);
        }
    }
}
