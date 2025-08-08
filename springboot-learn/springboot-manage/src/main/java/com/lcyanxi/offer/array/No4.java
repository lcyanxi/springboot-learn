package com.lcyanxi.offer.array;

import java.util.ArrayList;
import java.util.List;

/**
 * 子集
 * 给你一个整数数组 nums ，数组中的元素 互不相同 。返回该数组所有可能的子集（幂集）。
 * 解集 不能 包含重复的子集。你可以按 任意顺序 返回解集。
 *
 * 输入：nums = [1,2,3]
 * 输出：[[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
 *
 * 思路：先定义一个收集数据的集合，然后挨个加进去, 注意是集合新复制
 */
public class No4 {
    public static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        res.add(new ArrayList<>());
        for (int num : nums){
            int size = res.size();
            for (int i = 0; i < size; i++){
                List<Integer> integers = res.get(i);
                List<Integer> temp = new ArrayList<>(integers);
                temp.add(num);
                res.add(temp);
            }
        }
        return  res;
    }


    public static void main(String[] args) {
        int[] nums = {1,2,3};
        for (List<Integer> sub : subsets(nums)){
            System.out.println(sub);
        }
    }
}
