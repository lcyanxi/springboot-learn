package com.lcyanxi.offer.hash;

/**
 * 给定两个字符串 s 和 t ，编写一个函数来判断 t 是否是 s 的字母异位词。
 *
 * 示例 1: 输入: s = "anagram", t = "nagaram" 输出: true
 *
 * 示例 2: 输入: s = "rat", t = "car" 输出: false
 *
 * 说明: 你可以假设字符串只包含小写字母。
 * @author chang.li
 * @date 2025/10/17
 * @version 1.0
 */
public class No242 {
    public static void main(String[] args) {
        System.out.println(process("rat", "car"));
    }

    private static boolean process(String strA, String strB) {
        int[] chars = new int[25];
        for (int i = 0; i < strA.length(); i++) {
            chars[strA.charAt(i) - 'a']++;
        }
        for (int i = 0; i < strB.length(); i++) {
            chars[strB.charAt(i) - 'a']--;
        }
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == 1) {
                return false;
            }
        }
        return true;
    }
}
