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

    private static List<List<String>> process(String input) {
        List<List<String>> res = new ArrayList<>();
        List<String> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        backtrack(res,items, sb,input,0);
        return res;
    }

    private static void backtrack(List<List<String>> res, List<String> items, StringBuilder sb, String input, int startIndex) {
        if (startIndex == input.length()) {
            res.add(new ArrayList<>(items));
            return;
        }
        for (int i = startIndex; i < input.length(); i++) {
            sb.append(input.charAt(i));
            if (!checkProcess(sb.toString())) {
                continue;
            }
            items.add(sb.toString());
            backtrack(res, items,new StringBuilder(), input, i + 1);
            items.remove(items.size() - 1);
        }
    }

    private static boolean checkProcess(String input) {
        if (input == null || input.isEmpty()) {
            return true;
        }
        int left = 0;
        int right = input.length() - 1;
        while (left <= right) {
            if (input.charAt(left) != input.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

}
