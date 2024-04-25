package com.lcyanxi.basics.algorithm.string;

import org.apache.commons.lang3.StringUtils;

/**
 * @author : lichang
 * @desc : 判断是否为回文字符串，给定一个长度为 n 的字符串，请编写一个函数判断该字符串是否回文。如果是回文请返回true，否则返回false。
 *       输入："absba"
 *       返回值：true
 * @since : 2024/04/25/4:27 下午
 */
public class PalindromeNumber {
    public static void main(String[] args) {
        System.out.println(judge("ranko"));
    }

    public static boolean judge(String str) {
        if (StringUtils.isBlank(str) || str.length() == 1) {
            return true;
        }
        int left = 0;
        int right = str.length() - 1;
        boolean result = true;
        while (left < right) {
            if (str.charAt(left++) == str.charAt(right--)) {
                continue;
            }
            result = false;
            break;
        }
        return result;
        // write code here
    }
}
