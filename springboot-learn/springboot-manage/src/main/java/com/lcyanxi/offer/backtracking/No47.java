package com.lcyanxi.offer.backtracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 给定一个可包含重复数字的序列 nums ，按任意顺序 返回所有不重复的全排列。
 *
 * 示例 1：
 *
 * 输入：nums = [1,1,2]
 * 输出： [[1,1,2], [1,2,1], [2,1,1]]
 * 示例 2：
 *
 * 输入：nums = [1,2,3]
 * 输出：[[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
 *
 * @author chang.li
 * @version 1.0
 * @date 2025/9/29
 */
public class No47 {
    public static void main(String[] args) {
        System.out.println(process(new  int[]{1,1,2}));
    }
    private static List<List<Integer>> process(int[] nums){
        Arrays.sort(nums);
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> items = new ArrayList<>();
        boolean[] used = new boolean[nums.length];

        backtrack(res, items,nums,used);
        return res;
    }
    private static void backtrack(List<List<Integer>> res,List<Integer> items,int[] nums, boolean[] used){
        if (items.size() == nums.length){
            res.add(new ArrayList<>(items));
            return;
        }
        for (int i= 0 ; i< nums.length; i++){
            if (i> 0 && nums[i] == nums[i-1] && !used[i-1]){
                continue;
            }
            if (!used[i]){
                used[i] = true;
                items.add(nums[i]);
                backtrack(res,items,nums,used);
                used[i] = false;
                items.remove(items.size() -1);
            }
        }

    }
}
