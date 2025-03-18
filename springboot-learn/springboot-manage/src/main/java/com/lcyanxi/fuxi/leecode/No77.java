package com.lcyanxi.fuxi.leecode;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * 77. 组合
 *
 * 给定两个整数 n 和 k，返回范围 [1, n] 中所有可能的 k 个数的组合。
 *
 * 你可以按 任何顺序 返回答案。
 *
 * 示例 1：
 *
 * 输入：n = 4, k = 2
 * 输出：
 * [
 *   [2,4],
 *   [3,4],
 *   [2,3],
 *   [1,2],
 *   [1,3],
 *   [1,4],
 * ]
 * 示例 2：
 *
 * 输入：n = 1, k = 1
 * 输出：[[1]]
 *
 */
public class No77 {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> temps = new ArrayList<>();
        return Lists.newArrayList();
    }

    private void process(int n, int k, List<List<Integer>> res,List<Integer> temps){
        if (temps.size() == k){
            res.add(new ArrayList<>(temps));
        }
        for (int i =1; i<=n;i++){

        }

    }
}
