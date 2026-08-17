package com.lcyanxi.offer.dp;

import java.util.ArrayList;
import java.util.List;

/**
 * 给定一个字符串，你的任务是计算这个字符串中有多少个回文子串。
 *
 * 具有不同开始位置或结束位置的子串，即使是由相同的字符组成，也会被视作不同的子串。
 *
 * 示例 1：
 *
 * 输入："abc"
 * 输出：3
 * 解释：三个回文子串: "a", "b", "c"
 * 示例 2：
 *
 * 输入："aaa"
 * 输出：6
 * 解释：6个回文子串: "a", "a", "a", "aa", "aa", "aaa"
 * @author chang.li
 * @date 2025/10/14
 * @version 1.0
 */
public class No647 {
    public static void main(String[] args) {
        System.out.println(process("aaa"));
        System.out.println(process2("aaa"));
    }

    /**
     * 动态规划： dp[i+1][j-1] 是否是回文串即可
     */
    private static int process(String s) {
        List<String> result = new ArrayList<>();
        boolean[][] dp = new boolean[s.length() + 1][s.length() + 1];
        char[] chars = s.toCharArray();
        for (int i = chars.length - 1; i >= 0; i--) {
            for (int j = i; j < chars.length; j++) {
                if (chars[i] == chars[j]) {
                    if (j - i <= 1) {
                        dp[i][j] = true;
                        result.add(s.substring(i, j + 1));
                    } else if (dp[i + 1][j - 1]) {
                        dp[i][j] = true;
                        result.add(s.substring(i, j + 1));
                    }
                }
            }
        }
        System.out.println(result);
        return result.size();
    }

    /**
     * 暴力解法: 中间扩散法
     */
    private static int process2(String s) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            core(i, i, s, result);
            core(i, i + 1, s, result);
        }
        System.out.println(result);
        return result.size();
    }

    private static List<String> core(int i, int j, String s, List<String> result) {
        while (i >= 0 && j < s.length()) {
            if (s.charAt(i) == s.charAt(j)) {
                result.add(s.substring(i, j + 1));
            }
            i--;
            j++;
        }
        return result;
    }

}
