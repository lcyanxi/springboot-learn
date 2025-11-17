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
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        backtrack(s, res, list, sb, 0);
        return res;
    }

    private static void backtrack(String s, List<List<String>> res, List<String> list, StringBuilder sb, int index) {
        if (index == s.length()) {
            res.add(new ArrayList<>(list));
            return;
        }
        for (int i = index; i < s.length(); i++) {
            sb.append(s.charAt(i));
            if (!check(sb)) {
                continue;
            }
            list.add(sb.toString());
            backtrack(s, res, list, new StringBuilder(), i + 1);
            list.remove(list.size() - 1);
        }
    }

    private static boolean check(StringBuilder s) {
        int left = 0;
        int right = s.length() - 1;
        while (left <= right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

}
