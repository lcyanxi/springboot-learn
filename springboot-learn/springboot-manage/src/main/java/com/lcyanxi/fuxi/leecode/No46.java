package com.lcyanxi.fuxi.leecode;

import java.util.ArrayList;
import java.util.List;

/**
 * 46. 全排列
 * 给定一个不含重复数字的数组 nums ，返回其 所有可能的全排列 。你可以 按任意顺序 返回答案。
 * <p>
 * 示例 1：
 * <p>
 * 输入：nums = [1,2,3]
 * 输出：[[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
 * 示例 2：
 * <p>
 * 输入：nums = [0,1]
 * 输出：[[0,1],[1,0]]
 * 示例 3：
 * <p>
 * 输入：nums = [1]
 * 输出：[[1]]
 */
public class No46 {
    public static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        if (nums == null) {
            return res;
        }
        int[] count = new int[nums.length];
        List<Integer> temp = new ArrayList<>();
        process(res, temp, count, nums);
        return res;
    }

    private static void process(List<List<Integer>> res, List<Integer> temp, int[] count, int[] nums) {
        if (nums.length == temp.size()) {
            res.add(new ArrayList<>(temp));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (count[i] == 1) {
                continue;
            }
            temp.add(nums[i]);
            count[i] = 1;
            process(res, temp, count, nums);
            temp.remove(temp.size() - 1);
            count[i] = 0;
        }
    }

    public static void main(String[] args) {
        List<List<Integer>> permute = permute(new int[]{1, 2, 3});
        System.out.println(permute);
    }

}
