package com.lcyanxi.offer.backtracking;

import java.util.ArrayList;
import java.util.List;

/**
 * 子集
 * 给你一个整数数组 nums ，数组中的元素 互不相同 。返回该数组所有可能的子集（幂集）。
 * 解集 不能 包含重复的子集。你可以按 任意顺序 返回解集。
 * <p>
 * 输入：nums = [1,2,3]
 * 输出：[[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
 * <p>
 * 思路 1：先定义一个收集数据的集合，然后挨个加进去, 注意是集合新复制
 * 思路 2: 回溯法，收集树形结构的每个节点，传入一个 startIndex, 回溯的条件 startIndex >= nums.length()
 */
public class No4 {

    /**
     * 迭代法
     */

    public static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        res.add(new ArrayList<>());
        for (int num : nums) {
            int size = res.size();
            for (int i = 0; i < size; i++) {
                List<Integer> integers = res.get(i);
                List<Integer> temp = new ArrayList<>(integers);
                temp.add(num);
                res.add(temp);
            }
        }
        return res;
    }

    /**
     * 递归法：
     */
    public static List<List<Integer>> subsets2(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> temp = new ArrayList<>();
        process(res,temp,0,nums);
        return res;
    }

    private static void process(List<List<Integer>> res, List<Integer> temp, int startIndex, int[] nums) {
        res.add(new ArrayList<>(temp));
        if (startIndex >= nums.length) {
            return;
        }
        for (int i = startIndex; i < nums.length; i++) {
            temp.add(nums[i]);
            process(res, temp, i + 1, nums);
            temp.remove(temp.size() - 1);
        }
    }


    public static void main(String[] args) {
        int[] nums = {1, 2, 3};
        for (List<Integer> sub : subsets2(nums)) {
            System.out.println(sub);
        }
    }
}
