package com.lcyanxi.offer.backtracking;

import org.assertj.core.util.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * 给定两个整数 n 和 k，返回 1 ... n 中所有可能的 k 个数的组合。
 * 示例: 输入: n = 4, k = 2 输出: [ [2,4], [3,4], [2,3], [1,2], [1,3], [1,4], ]
 *
 * @author chang.li
 * @version 1.0
 * @date 2025/9/25
 */
public class No77 {
    public static void main(String[] args) {
        System.out.println(process(4, 4));

        System.out.println(process2(4, 2));
    }

    private static List<List<Integer>> process(int n, int k) {
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> tmp = Lists.newArrayList();
        core(n, k,1, tmp, res);
        return res;

    }
    private static void core(int n, int k, int startIndex,List<Integer> temp, List<List<Integer>> res) {
        if (temp.size() == k){
            res.add(Lists.newArrayList(temp));
            return;
        }
        for (int i = startIndex; i <= n; i++) {
            temp.add(i);
            core(n, k, i + 1, temp, res);
            temp.remove(temp.size() - 1);
        }
    }

    /**
     * 排列
     */
    private static List<List<Integer>> process2(int n, int k) {
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> tmp = Lists.newArrayList();
        boolean[] visited = new boolean[n + 1];
        core2(n, k, tmp, res, visited);
        return res;
    }
    private static void core2(int n, int k,  List<Integer> temp, List<List<Integer>> res, boolean[] visited) {
        if (temp.size() == k){
            res.add(Lists.newArrayList(temp));
            return;
        }
        for (int i = 1; i <= n; i++) {
            if (visited[i]){
                continue;
            }
            temp.add(i);
            visited[i] = true;
            core2(n, k, temp, res, visited);
            temp.remove(temp.size() - 1);
            visited[i] = false;
        }
    }

}
