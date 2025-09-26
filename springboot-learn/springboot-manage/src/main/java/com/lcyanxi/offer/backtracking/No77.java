package com.lcyanxi.offer.backtracking;

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
        System.out.println(process(4, 2));

        System.out.println(process2(4, 2));

        System.out.println(process3(4, 2));
    }

    private static List<List<Integer>> process(int n, int k) {

        List<List<Integer>> res = new ArrayList<>();
        List<Integer> itemList = new ArrayList<>();

        backtracking(res, itemList, n, k, 1);
        return res;
    }

    // 组合
    private static void backtracking(List<List<Integer>> res, List<Integer> itemList, int n, int k, int index) {
        if (itemList.size() == k) {
            res.add(new ArrayList<>(itemList));
            return;
        }
        for (int i = index; i <= n; i++) {
            itemList.add(i);
            backtracking(res, itemList, n, k, i + 1);
            itemList.remove(itemList.size() - 1);
        }
    }

    private static List<List<Integer>> process2(int n, int k) {

        List<List<Integer>> res = new ArrayList<>();
        List<Integer> itemList = new ArrayList<>();
        boolean[] used = new boolean[n + 1];
        backtracking2(res, itemList, used, n, k);
        return res;
    }

    // 排列
    private static void backtracking2(List<List<Integer>> res, List<Integer> itemList, boolean[] use, int n, int k) {
        if (itemList.size() == k) {
            res.add(new ArrayList<>(itemList));
            return;
        }

        for (int i = 1; i <= n; i++) {
            if (use[i]) {
                continue;
            }
            use[i] = true;
            itemList.add(i);
            backtracking2(res, itemList, use, n, k);
            use[i] = false;
            itemList.remove(itemList.size() - 1);
        }
    }

    private static List<List<Integer>> process3(int n, int k) {

        List<List<Integer>> res = new ArrayList<>();
        List<Integer> itemList = new ArrayList<>();
        backtracking3(res, itemList, n, 1);
        return res;
    }

    // 子集
    private static void backtracking3(List<List<Integer>> res, List<Integer> itemList, int n, int index) {
        if (index > n){
            return;
        }
        res.add(new ArrayList<>(itemList));
        for (int i = index; i <= n; i++) {
            itemList.add(i);
            backtracking3(res, itemList, n, i + 1);
            itemList.remove(itemList.size() - 1);
        }
    }


}
