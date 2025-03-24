package com.lcyanxi.fuxi.leecode.Str;

import java.util.Arrays;

/**
 * 字符串指定长度整体交换
 * 描述：给定一个字符串str和长度leftSize，请把字符串leftSize左边的整体部分与右边交换
 * 要求额外空间复杂度O(1)
 * eg : 输入：str = "abdesfc" leftSize = 3   输出："esfcabd"
 *
 * 思路： 先左边交换 再右边交换 然后整体交换
 */
public class StrExchange {

    public static void main(String[] args) {
        String str = "abdesfc";
        System.out.println(exchange1(str,3));
        String exchange = exchange(str, 3);
        System.out.println(exchange);
    }

    private static String exchange1(String str, int leftSize){
        char[] charArray = str.toCharArray();
        int length = charArray.length;
        while (leftSize >= 0){
            char temp = charArray[length -1];
            int len = length-1;
            while (len >0){
                charArray[len] = charArray[len -1];
                len--;
            }
            charArray[0] = temp;
            leftSize--;
        }
        return String.valueOf(charArray);
    }



    private  static String exchange(String str, int leftSize){
        char[] charArray = str.toCharArray();
        int length = str.length() - 1;
        process(0,leftSize-1,charArray);
        process(leftSize,length,charArray);
        process(0,length, charArray);
        return String.valueOf(charArray);
    }

    private static void process(int left, int right,char[] charStr){
        while (left < right){
            char temp = charStr[left];
            charStr[left] = charStr[right];
            charStr[right] = temp;
            left++;
            right--;
        }
    }
}
