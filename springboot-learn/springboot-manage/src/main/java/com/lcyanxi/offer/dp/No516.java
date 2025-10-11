package com.lcyanxi.offer.dp;

/**
 * 给定一个字符串 s ，找到其中最长的回文子序列，并返回该序列的长度。可以假设 s 的最大长度为 1000 。
 *
 * 示例 1: 输入: "bbbab" 输出: 4 一个可能的最长回文子序列为 "bbbb"。
 *
 * 示例 2: 输入:"cbbd" 输出: 2 一个可能的最长回文子序列为 "bb"。
 * @author chang.li
 * @date 2025/10/10
 * @version 1.0
 */
public class No516 {
    public static void main(String[] args) {
        System.out.println(process("bbbab"));
    }

    private static Integer process(String str) {
        int[][] dp = new int[str.length() + 1][str.length() + 1];
        /**
         * i == j : dp[i,j] = dp[i+1,j-1] +2
         * i != j: max(dp[i+1][j],dp[i][j-1]);
         *
         */
        for (int i = str.length() - 1; i >= 0; i--) {
            dp[i][i] = 1;
            for (int j = i + 1; j < str.length(); j++) {
                if (str.charAt(i) == str.charAt(j)) {
                    dp[i][j] = dp[i + 1][j - 1] + 2;
                } else {
                    dp[i][j] = Math.max(dp[i][j - 1], dp[i + 1][j]);
                }
            }
        }
        return dp[0][dp.length - 1];
    }

}
