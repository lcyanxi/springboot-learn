package com.lcyanxi.fuxi.leecode;

/**
 * 5. 最长回文子串
 * <p>
 * 给你一个字符串 s，找到 s 中最长的 回文 子串。
 * <p>
 * 示例 1：
 * <p>
 * 输入：s = "babad"
 * 输出："bab"
 * 解释："aba" 同样是符合题意的答案。
 * <p>
 * 示例 2：
 * <p>
 * 输入：s = "cbbd"
 * 输出："bb"
 */
public class No5 {
    public static String longestPalindrome(String s) {
        if (s == null || s.length() <= 1) {
            return s;
        }
        int maxLen = 0;
        int index = 0;
        for (int i = 0; i < s.length(); i++) {
            int len1 = process(s, i, i);
            int len2 = process(s, i, i + 1);
            int len = Math.max(len2, len1);
            if (len > maxLen) {
                maxLen = len;
                index = i;
            }
        }
        return s.substring(index - (maxLen - 1) / 2, index + maxLen / 2 +1);
    }

    private static int process(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1;
    }

    public static void main(String[] args) {
        String s = longestPalindrome("babad");
        System.out.println(s);
    }

}
