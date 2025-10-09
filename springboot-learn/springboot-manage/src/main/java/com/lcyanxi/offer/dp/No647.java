package com.lcyanxi.offer.dp;

import java.util.ArrayList;
import java.util.List;

/**
 * 给定一个字符串，你的任务是计算这个字符串中有多少个回文子串。
 * <p>
 * 具有不同开始位置或结束位置的子串，即使是由相同的字符组成，也会被视作不同的子串。
 * <p>
 * 示例 1：
 * <p>
 * 输入："abc"
 * 输出：3
 * 解释：三个回文子串: "a", "b", "c"
 * 示例 2：
 * <p>
 * 输入："aaa"
 * 输出：6
 * 解释：6个回文子串: "a", "a", "a", "aa", "aa", "aaa"
 * 提示：输入的字符串长度不会超过 1000 。
 *
 * @author chang.li
 * @version 1.0
 * @date 2025/10/9
 */
public class No647 {
    public static void main(String[] args) {
        System.out.println(process("aaa"));
        System.out.println(process2("aaa"));
    }

    /**
     * 双指针解法
     */
    private static List<String> process2(String str) {
        List<String> res = new ArrayList<>();

        for (int i = 0; i < str.length(); i++) {
            handler(res, i, i, str);
            handler(res, i, i + 1, str);
        }

        return res;
    }

    private static void handler(List<String> res, int left, int right, String str) {
        while (left >= 0 && right < str.length() && str.charAt(left) == str.charAt(right)) {
            res.add(str.substring(left, right + 1));
            left--;
            right++;
        }
    }

    /**
     * 回溯法
     */
    private static List<String> process(String str) {
        List<String> res = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        backtrack(res, sb, str, 0);

        return res;
    }

    private static void backtrack(List<String> res, StringBuilder sb, String str, int start) {
        if (check(sb.toString())) {
            res.add(sb.toString());
        }

        for (int i = start; i < str.length(); i++) {
            sb.append(str.charAt(i));
            backtrack(res, sb, str, i + 1);
            sb.deleteCharAt(sb.length() - 1);
        }

    }

    private static boolean check(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        int left = 0;
        int right = str.length() - 1;
        while (left < right) {
            if (str.charAt(left) != str.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}
