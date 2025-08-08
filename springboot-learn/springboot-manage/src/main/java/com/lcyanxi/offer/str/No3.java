package com.lcyanxi.offer.str;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 回文排列 II
 * 给定一个字符串s，返回所有回文排列(不重复)。如果没有回文排列，则返回空列表。
 * 输入: s = "aabb"
 * 输出: ["abba","baab"]
 * 思路： 先拿到字符串的全排列 在判断是否是回文字符串
 */
public class No3 {
    public static List<String> generatePalindromes(String s) {
        if (s == null || s.length() == 0) {
            return Lists.newArrayList();
        }
        List<String> result = Lists.newArrayList();
        char[] charArray = s.toCharArray();
        Arrays.sort(charArray);

        boolean[] used = new boolean[charArray.length];
        StringBuilder temp = new StringBuilder();

        process(result, temp, used, charArray);


        return result.stream().filter(No3::check).collect(Collectors.toList());
    }

    private static boolean check(String s) {
        int left = 0;
        int right = s.length() - 1;
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    private static void process(List<String> result, StringBuilder temp, boolean[] used, char[] charArray) {
        if (temp.length() == charArray.length) {
            result.add(temp.toString());
            return;
        }
        for (int i = 0; i < charArray.length; i++) {
            if (used[i] || i > 0 && charArray[i] == charArray[i - 1] && !used[i - 1]) {
                continue;
            }
            temp.append(charArray[i]);
            used[i] = true;
            process(result, temp, used, charArray);
            used[i] = false;
            temp.deleteCharAt(temp.length() - 1);
        }

    }

    public static void main(String[] args) {
        String s = "a";
        List<String> strings = generatePalindromes(s);
        for (String str : strings){
            System.out.println(str);
        }
    }
}
