package com.lcyanxi.basics.algorithm.arry;

import java.util.ArrayList;

/**
 * @author : lichang
 * @desc : 没有重复项数字的全排列 (给出一组数字，返回该组数字的所有排列)
 *       输入：[1,2,3]
 *       输出: [1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2], [3,2,1]
 * @since : 2024/04/26/10:11 上午
 */
public class AllSortArray {
    public static void main(String[] args) {
        int[] num = new int[] {1, 2, 3};
        ArrayList<ArrayList<Integer>> permute = permute(num);
        for (ArrayList<Integer> list : permute) {
            System.out.println(list);
        }
    }

    public static ArrayList<ArrayList<Integer>> permute(int[] num) {
        // write code here
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < num.length; i++) {
            process(result, num[i]);
        }
        return result;
    }

    public static void process(ArrayList<ArrayList<Integer>> result, int val) {
        if (result.isEmpty()) {
            ArrayList<Integer> list = new ArrayList<Integer>();
            list.add(val);
            result.add(list);
        } else {
            int size = result.size();
            for (int i = 0; i < size; i++) {
                ArrayList<Integer> item = result.get(i);
                int length = item.size();
                for (int j = 0; j < length + 1; j++) {
                    if (j == length) {
                        item.add(val);
                    } else {
                        ArrayList<Integer> newList = new ArrayList<Integer>(item);
                        newList.add(j, val);
                        result.add(newList);
                    }
                }
            }
        }
    }
}
