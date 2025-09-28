package com.lcyanxi.offer.backtracking;

import java.util.ArrayList;
import java.util.List;

/**
 * 找出所有相加之和为 n 的 k 个数的组合。组合中只允许含有 1 - 9 的正整数，并且每种组合中不存在重复的数字。
 * 说明：
 * 所有数字都是正整数。
 * 解集不能包含重复的组合。
 * 示例 1: 输入: k = 3, n = 7 输出: [[1,2,4]]
 * 示例 2: 输入: k = 3, n = 9 输出: [[1,2,6], [1,3,5], [2,3,4]]
 *
 * @author chang.li
 * @version 1.0
 * @date 2025/9/28
 */
public class No216 {

    public static void main(String[] args) {
        System.out.println(combinationSum3(2, 18));
    }

    private static List<List<Integer>> combinationSum3(int k, int n) {
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> items = new ArrayList<>();
        backtrack(res, items, k, n, 1);
        return res;
    }

    private static void backtrack(List<List<Integer>> res, List<Integer> items, int k, int n, int startIndex) {
        if (items.size() > k){
            return;
        }
        int sum = items.stream().mapToInt(i -> i).sum();
        if (sum > n){
            return;
        }
        if (items.size() == k && sum == n) {
            res.add(new ArrayList<>(items));
            return;
        }

        for (int i = startIndex; i <= 9; i++) {
            items.add(i);
            backtrack(res, items, k, n, i + 1);
            items.remove(items.size() - 1);

        }
    }
}
