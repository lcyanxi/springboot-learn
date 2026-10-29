package com.lcyanxi.offer.str;

import java.util.HashSet;
import java.util.Set;

/**
 * LCR 016. 无重复字符的最长子串
 * 给定一个字符串 s ，请你找出其中不含有重复字符的 最长连续子字符串 的长度。
 * <p>
 * 输入: s = "abcabcbb"
 * 输出: 3
 * 解释: 因为无重复字符的最长子字符串是 "abc"，所以其长度为 3。
 */
public class LCR016 {
    public static void main(String[] args) {
        int result = process("abcabcbb");
        System.out.println(result);

    }

    private static int process(String str) {
        int fast = 0;
        int slow = 0;
        int max = 0;
        int length = 0;
        Set<Character> set = new HashSet<>();
        while (fast < str.length()) {
            if (set.contains(str.charAt(fast))) {
                while (set.contains(str.charAt(slow))) {
                    set.remove(str.charAt(slow));
                    length--;
                    max = Math.max(max, length);
                    slow++;
                }
            }
            set.add(str.charAt(fast));
            length++;
            max = Math.max(max, length);
            fast++;
        }
        return max;
    }
}
