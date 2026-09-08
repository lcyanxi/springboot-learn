package com.lcyanxi.offer.backtracking;

import java.util.ArrayList;
import java.util.List;

/**
 * 给定一个字符串 s，将 s 分割成一些子串，使每个子串都是回文串。
 * 返回 s 所有可能的分割方案。
 * 示例: 输入: "aab" 输出: [ ["aa","b"], ["a","a","b"] ]
 *
 * @author chang.li
 * @version 1.0
 * @date 2025/9/26
 */
public class No131 {
    public static void main(String[] args) {
        System.out.println(process("aab"));
    }

    private static List<List<String>> process(String s) {
        List<List<String>> res = new ArrayList<>();
        List<String> temp = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        backtrack(res, temp, sb, 0, s);
        return res;

    }

    private static void backtrack(List<List<String>> res, List<String> temp, StringBuilder sb, int index, String s) {
        if (index == s.length()) {
            res.add(new ArrayList<>(temp));
            return;
        }

        for (int i = index; i < s.length(); i++) {
            sb.append(s.charAt(i));
            if (!check(sb)) {
                continue;
            }
            temp.add(sb.toString());
            backtrack(res, temp, new StringBuilder(), i + 1, s);
            temp.remove(temp.size() - 1);
        }
    }

    private static boolean check(StringBuilder sb) {
        int i = 0;
        int j = sb.length() - 1;
        while (i <= j) {
            if (sb.charAt(i) != sb.charAt(j)) {
                return false;
            }
            i++;
            j--;
        }
        return true;
    }

}
