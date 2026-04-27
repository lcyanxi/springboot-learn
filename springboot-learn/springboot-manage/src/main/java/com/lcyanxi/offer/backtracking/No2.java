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


    public static void main(String[] args) {
        String str = "abc";
        List<String> permutation = permutation(str);
        for (String s : permutation) {
            System.out.println(s);
        }
    }

    private static List<String> permutation(String str) {
        char[] charArray = str.toCharArray();
        Arrays.sort(charArray);
        boolean[] visited = new boolean[charArray.length];
        StringBuilder temp = new StringBuilder();
        List<String> result = new ArrayList<>();
        core(charArray, visited, temp, result);
        return result;
    }

    private static void core(char[] charArray, boolean[] visited, StringBuilder temp, List<String> result) {
        if (temp.length() == charArray.length) {
            result.add(String.join("", temp));
            return;
        }
        for (int i = 0; i < charArray.length; i++) {
            if (visited[i] || (i > 0 && charArray[i] == charArray[i - 1] && !visited[i - 1])) {
                continue;
            }
            visited[i] = true;
            temp.append(charArray[i]);
            core(charArray, visited, temp, result);
            visited[i] = false;
            temp.deleteCharAt(temp.length() - 1);
        }
    }

}
