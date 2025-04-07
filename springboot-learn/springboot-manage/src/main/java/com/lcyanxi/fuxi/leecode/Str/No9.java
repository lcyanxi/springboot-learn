package com.lcyanxi.fuxi.leecode.Str;

/**
 * 9. 回文数
 *
 * 回文数是指正序（从左向右）和倒序（从右向左）读都是一样的整数。
 *
 * 示例 1：
 *
 * 输入：x = 121
 * 输出：true
 * 示例 2：
 *
 * 输入：x = -121
 * 输出：false
 * 解释：从左向右读, 为 -121 。 从右向左读, 为 121- 。因此它不是一个回文数。
 * 示例 3：
 *
 * 输入：x = 10
 * 输出：false
 * 解释：从右向左读, 为 01 。因此它不是一个回文数。
 *
 */
public class No9 {
    public static boolean isPalindrome(int x) {
        if (x< 0){
            return  false;
        }
        String s = String.valueOf(x);
        int left = 0;
        int right = s.length() -1;
        while (left < right){
            if (s.charAt(left) != s.charAt(right)){
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println( isPalindrome(10));
    }
}
