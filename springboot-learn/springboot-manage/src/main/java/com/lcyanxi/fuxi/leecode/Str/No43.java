package com.lcyanxi.fuxi.leecode.Str;

import java.util.LinkedHashSet;

/**
 * 43. 字符串相乘
 *
 * 给定两个以字符串形式表示的非负整数 num1 和 num2，返回 num1 和 num2 的乘积，它们的乘积也表示为字符串形式。
 *
 * 注意：不能使用任何内置的 BigInteger 库或直接将输入转换为整数。
 *
 * 示例 1:
 *
 * 输入: num1 = "2", num2 = "3"
 * 输出: "6"
 * 示例 2:
 *
 * 输入: num1 = "123", num2 = "456"
 * 输出: "56088"
 *
 * 1 2 3          1 2 3               1 2 3
 *     6  ===>      5          ==>    4
 *7 3  8         6  1  5  0          5 9 2 0 0
 *
 *
 * 思路： 遍历两个字符串， 前面一个字符串
 */
public class No43 {
    public String multiply(String num1, String num2) {
        LinkedHashSet<Integer> set = new LinkedHashSet<>();
        set.add(11);

        for (Integer a : set){
            System.out.println(a);
        }

        if (num1.equals("0") || num2.equals("0")){
            return "0";
        }
        int len1 = num1.length();
        int len2 = num2.length();
        StringBuilder res = new StringBuilder();
        return "0";
    }
}
