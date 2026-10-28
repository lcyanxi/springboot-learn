package com.lcyanxi.offer.str;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 回文排列 II
 * 给定一个字符串s，返回所有回文排列(不重复)。如果没有回文排列，则返回空列表。
 * 输入: s = "aabb"
 * 输出: ["abba","baab"]
 * 思路： 先拿到字符串的全排列 在判断是否是回文字符串
 */
public class No3 {

    public static void main(String[] args) {
        String s = "aabb";
        List<String> result = generatePalindromes(s);
        System.out.println(result);
    }

    private static List<String> generatePalindromes(String str) {
        char[] charArray = str.toCharArray();
        Arrays.sort(charArray);
        StringBuilder temp = new StringBuilder();
        for (char c : charArray) {
            temp.append(c);
        }
        str = temp.toString();
        List<String> result = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        boolean[] visit = new boolean[str.length()];
        core(result, builder, visit, str);
        return result;
    }

    private static void core(List<String> result, StringBuilder builder, boolean[] visit, String str) {
        if (builder.length() == str.length() && check(builder.toString())) {
            result.add(builder.toString());
            return;
        }
        for (int i = 0; i < str.length(); i++) {
            if (visit[i]) {
                continue;
            }
            if (i > 0 && str.charAt(i) == str.charAt(i - 1) && !visit[i - 1]) {
                continue;
            }

            visit[i] = true;
            builder.append(str.charAt(i));
            core(result, builder, visit, str);
            visit[i] = false;
            builder.deleteCharAt(builder.length() - 1);
        }
    }

    private static boolean check(String str) {
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
