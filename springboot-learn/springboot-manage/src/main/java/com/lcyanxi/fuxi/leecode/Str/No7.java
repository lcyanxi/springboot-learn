package com.lcyanxi.fuxi.leecode.Str;

/**
 * 7. 整数反转
 *示例 1：
 *
 * 输入：x = 123
 * 输出：321
 *
 * 示例 3：
 *
 * 输入：x = 120
 * 输出：21
 * 思路： 取最后一位 x % 10 取剩下的 x / 10
 *
 *
 */
public class No7 {
    public static int reverse(int x) {
        int result = 0;
        while (x !=0){
            result = result * 10 + x % 10;
            if (result > 214748364 || (result == 214748364 && x % 10 >7)){
                return 0;
            }
            if (result < -214748364 || (result == -214748364 && x% 10 <-8)){
                return 0;
            }
            x= x/10;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(reverse(-12300));
    }

}
