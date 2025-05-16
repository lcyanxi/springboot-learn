package com.lcyanxi.fuxi.leecode;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 78. 子集
 * 给你一个整数数组 nums ，数组中的元素 互不相同 。返回该数组所有可能的子集（幂集）。
 *
 * 解集 不能 包含重复的子集。你可以按 任意顺序 返回解集。
 *
 * 示例 1：
 *
 * 输入：nums = [1,2,3]
 * 输出：[[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
 * 示例 2：
 *
 * 输入：nums = [0]
 * 输出：[[],[0]]
 */
public class No78 {

    public static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        res.add(new ArrayList<>());
        for (int num: nums){
            List<List<Integer>> curList = new ArrayList<>();
            for (List<Integer> oldList : res){
                List<Integer> temps = new ArrayList<>(oldList);
                temps.add(num);
                curList.add(temps);
            }
            res.addAll(curList);
        }
        return  res;

    }

    public static void main(String[] args) {
        int[] nums = {1,2,3};
        System.out.println(subsets(nums));

    }
}
