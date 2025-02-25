package com.lcyanxi.fuxi.leecode;

import java.util.HashMap;
import java.util.Map;

/**
 * 3. 无重复字符的最长子串
 * <p>
 * 给定一个字符串 s ，请你找出其中不含有重复字符的 最长 子串 的长度。
 * 示例 1:
 * <p>
 * 输入: s = "abcabcbb"
 * 输出: 3
 * 解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。
 * 示例 2:
 * <p>
 * 输入: s = "bbbbb"
 * 输出: 1
 * 解释: 因为无重复字符的最长子串是 "b"，所以其长度为 1。
 * 示例 3:
 * <p>
 * 输入: s = "pwwkew"
 * 输出: 3
 * 解释: 因为无重复字符的最长子串是 "wke"，所以其长度为 3。
 * 请注意，你的答案必须是 子串 的长度，"pwke" 是一个子序列，不是子串。
 */
public class No3 {

    public static int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> map = new HashMap<>();
        int left = 0;
        int right = 0;
        int maxLength = 0;
        while (right < s.length()) {
            char c = s.charAt(right++);
            map.put(c, map.getOrDefault(c, 0) + 1);
            while (map.getOrDefault(c, 0) > 1) {
                map.put(s.charAt(left), map.getOrDefault(s.charAt(left), 0) - 1);
                left++;
            }
            maxLength = Math.max(maxLength,right - left);
        }
        return maxLength;
    }

    public static void main(String[] args) {
        int i = lengthOfLongestSubstring("bbbbb");
        System.out.println(i);
    }
}
