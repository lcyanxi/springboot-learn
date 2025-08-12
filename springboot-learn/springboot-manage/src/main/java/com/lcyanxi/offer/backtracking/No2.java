package com.lcyanxi.offer.backtracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 字符串的排列
 * 题目描述：输入一个字符串，按字典序打印出该字符串中字符的所有排列。
 * 例如输入字符串 abc，则打印出由字符 a, b, c 所能排列出来的所有字符串 abc, acb, bac, bca, cab 和 cba。
 * <p>
 * 思路： 先把字符串排序，然后用递归方式，用一个变量数组标记是否访问过，用一个集合存储访问过的值，回溯的时候改回标记，删除集合最后一个元素
 */
public class No2 {
    public static List<String> permutation(String str) {
        char[] charArray = str.toCharArray();
        Arrays.sort(charArray);
        boolean[] use = new boolean[charArray.length];
        StringBuilder temp = new StringBuilder();
        List<String> res = new ArrayList<>();
        process(res, temp, use, charArray);
        return res;
    }

    private static void process(List<String> res,StringBuilder temp, boolean[] use, char[] charArray) {
        if (temp.length() == charArray.length) {
            res.add(temp.toString());
            return;
        }
        for (int i = 0; i < charArray.length; i++) {
            if (use[i] || i > 0 && charArray[i] == charArray[i-1]  && !use[i-1]) {
                continue;
            }
            temp.append(charArray[i]);
            use[i] = true;
            process(res, temp, use, charArray);
            temp.deleteCharAt(temp.length() - 1);
            use[i] = false;
        }
    }

    public static void main(String[] args) {
        String str = "aac";
        List<String> permutation = permutation(str);
        for (String s : permutation){
            System.out.println(s);
        }
    }

}
