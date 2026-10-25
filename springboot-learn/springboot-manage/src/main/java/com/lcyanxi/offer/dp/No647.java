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
 *
 * @author chang.li
 * @version 1.0
 * @date 2025/10/14
 */
public class No647 {
    public static void main(String[] args) {
        System.out.println(process("aaa"));
        System.out.println(process2("aaa"));
    }

    private static int process(String str) {
        // dp[i][j] = dp[i+1][j-1] + 1
        Boolean dp[][] = new Boolean[str.length()][str.length()];
        int count = 0;
        for (int i = str.length() - 1; i >= 0; i--) {
            for (int j = i; j < str.length(); j++) {
                if (str.charAt(i) == str.charAt(j)) {
                    if (j - i <= 1) {
                        dp[i][j] = true;
                        count++;
                    } else if (j - i > 1 && dp[i + 1][j - 1]) {
                        dp[i][j] = true;
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private static List<String> process2(String str) {
        char[] charArray = str.toCharArray();
        List<String> result = new ArrayList<>();
        for (int i = 0; i < charArray.length; i++) {
            check(str, i, i, result);
            check(str, i, i + 1, result);
        }
        return result;
    }

    private static void check(String str, int left, int right, List<String> result) {
        while (left >= 0 && right < str.length()) {
            if (str.charAt(left) == str.charAt(right)) {
                result.add(str.substring(left, right + 1));
            }
            left--;
            right++;
        }
    }

}
