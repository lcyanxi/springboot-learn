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
     * num[i] == num[j] : dp[i,j] = dp[i+1,j-1] + 2
     */
    private static Integer process2(String str) {
        List<String> res = new ArrayList<>();
        boolean[][] dp = new boolean[str.length()][str.length()];
        for (int i = str.length() - 1; i >= 0; i--) {
            for (int j = i; j < str.length(); j++) {
                if (str.charAt(i) == str.charAt(j)) {
                    if (j - i <= 1) {
                        res.add(str.substring(i, j + 1));
                        dp[i][j] = true;
                    } else if (dp[i + 1][j - 1]) {
                        res.add(str.substring(i, j + 1));
                        dp[i][j] = true;
                    }
                }
            }
        }
        System.out.println(res);
        return res.size();
    }

    /**
     * 双指针: 中心扩散法
     */
    private static Integer process(String str) {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            handle(str, i, i, res);
            handle(str, i, i + 1, res);

        }
        System.out.println(res);
        return res.size();
    }

    private static void handle(String str, int left, int right, List<String> res) {
        while (left >= 0 && right < str.length()) {
            if (str.charAt(left) == str.charAt(right)) {
                res.add(str.substring(left, right + 1));
                left--;
                right++;
            } else {
                break;
            }
        }
    }

}
