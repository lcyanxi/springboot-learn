package com.lcyanxi.offer.backtracking;

import java.util.ArrayList;
import java.util.List;

/**
 * 77. 组合
 * 给定两个整数 n 和 k，返回范围 [1, n] 中所有可能的 k 个数的组合。你可以按 任何顺序 返回答案。
 * <p>
 * 输入：n = 4, k = 2
 * 输出：
 * [
 * [2,4],
 * [3,4],
 * [2,3],
 * [1,2],
 * [1,3],
 * [1,4],
 * ]
 * 思路： 回溯法，需要一个startIndex 游标 终止条件：收集的数据大小等于 k 值
 *              第一轮：1，2，3，4 取：1
 *              第二轮：2，3，4   取：2
 *              第三轮：3，4      取：3
 *              第四轮：4        取：4
 */
public class No5 {
    static List<List<Integer>> res = new ArrayList<>();
    static List<Integer> temp = new ArrayList<>();

    public static List<List<Integer>> combine(int n, int k) {
        backtrack(1, n, k);
        return res;
    }

    private static void backtrack(int startIndex, int n, int k) {
        if (temp.size() == k) {
            res.add(new ArrayList<>(temp));
            return;
        }
        for (int i = startIndex; i <= n; i++) {
            temp.add(i);
            backtrack(i + 1, n, k);
            temp.remove(temp.size() - 1);
        }
    }

    public static void main(String[] args) {
        List<List<Integer>> combine = combine(4, 2);
        combine.forEach(System.out::println);
    }
}
