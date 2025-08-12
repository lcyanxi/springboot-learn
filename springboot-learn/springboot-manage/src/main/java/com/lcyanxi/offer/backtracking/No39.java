package com.lcyanxi.offer.backtracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 39. 组合总和
 * 给你一个 无重复元素 的整数数组 candidates 和一个目标整数 target ，找出 candidates 中可以使数字和为目标数 target 的 所有 不同组合 ，并以列表形式返回。你可以按 任意顺序 返回这些组合。
 * <p>
 * candidates 中的 同一个 数字可以 无限制重复被选取 。如果至少一个数字的被选数量不同，则两种组合是不同的。
 * <p>
 * 对于给定的输入，保证和为 target 的不同组合数少于 150 个。
 * <p>
 * 输入：candidates = [2,3,6,7], target = 7
 * 输出：[[2,2,3],[7]]
 * <p>
 * 输入: candidates = [2,3,5], target = 8
 * 输出: [[2,2,2,2],[2,3,3],[3,5]]
 */
public class No39 {
    static List<List<Integer>> res = new ArrayList<>();
    static List<Integer> temp = new ArrayList<>();

    public static List<List<Integer>> combinationSum(int[] candidates, int target) {
        Arrays.sort(candidates);
        backtrack(0,candidates, target);
        return res;
    }

    private static void backtrack(int startIndex, int[] candidates, int target) {
        int sum = temp.stream().mapToInt(Integer::intValue).sum();
        if (sum > target) {
            return;
        }
        if (sum == target) {
            res.add(new ArrayList<>(temp));
        }
        for (int i = startIndex; i < candidates.length; i++) {
            if (target - candidates[i] < 0){
               break;
            }
            temp.add(candidates[i]);
            backtrack(i, candidates, target);
            temp.remove(temp.size() - 1);
        }

    }

    public static void main(String[] args) {
        List<List<Integer>> lists = combinationSum(new int[]{2, 3, 5}, 8);
        for (List<Integer> list : lists) {
            System.out.println(list);
        }
    }
}
