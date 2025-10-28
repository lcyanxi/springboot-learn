package com.lcyanxi.offer.backtracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 回文排列 II
 * 给定一个字符串 s ，返回其通过重新排列组合后所有可能的回文字符串，并去除重复的组合。
 * 如不能形成任何回文排列时，则返回一个空列表。
 *
 * 示例 1：
 * 输入: "aabb"
 * 输出: ["abba", "baab"]
 *
 * 示例 2：
 * 输入: "abc"
 * 输出: []
 * @author chang.li
 * @date 2025/10/28
 * @version 1.0
 */
public class No267 {
    public static void main(String[] args) {
        System.out.println(process("abc"));
    }

    private static List<String> process(String str) {
        List<String> res = new ArrayList<>();
        char[] charArray = str.toCharArray();
        Arrays.sort(charArray);
        StringBuilder sb = new StringBuilder();
        boolean[] used = new boolean[charArray.length];

        handle(res, charArray, sb, used);
        return res;
    }

    private static void handle(List<String> res, char[] charArray, StringBuilder sb, boolean[] used) {
        if (sb.length() == charArray.length) {
            if (check(sb.toString())) {
                res.add(sb.toString());
            }
            return;
        }
        for (int i = 0; i < charArray.length; i++) {
            if (i > 0 && charArray[i] == charArray[i - 1] && !used[i - 1]) {
                continue;
            }
            if (used[i]) {
                continue;
            }
            used[i] = true;
            sb.append(charArray[i]);
            handle(res, charArray, sb, used);
            used[i] = false;
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    private static boolean check(String str) {
        int start = 0;
        int end = str.length() - 1;
        while (start < end) {
            if (str.charAt(start) != str.charAt(end)) {
                return false;
            }
            start++;
            end--;
        }
        return true;
    }
}
