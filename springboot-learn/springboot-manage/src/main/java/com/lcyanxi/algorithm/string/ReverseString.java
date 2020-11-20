package com.lcyanxi.algorithm.string;

/**
 * 反转字符串
 * 描述：
 * 编写一个函数，其作用是将输入的字符串反转过来。输入字符串以字符数组 char[] 的形式给出。
 * 不要给另外的数组分配额外的空间，你必须原地修改输入数组、使用 O(1) 的额外空间解决这一问题。
 * 你可以假设数组中的所有字符都是 ASCII 码表中的可打印字符。
 * eg:
 * 输入：["h","e","l","l","o"]
 * 输出：["o","l","l","e","h"]
 * @author lichang
 * @date 2020/11/20
 */
public class ReverseString {
    public static void main(String[] args) {
        String str = "hello";
        System.out.println(reverseString(str.toCharArray()));
    }
    private static char[] reverseString(char [] characters){
        int left = 0;
        int right = characters.length - 1;
        char tmp ;
        while (left <= right){
            tmp = characters[left];
            characters[left] = characters[right];
            characters[right] = tmp;
            left ++;
            right --;
        }
        return characters;
    }
}
