package com.lcyanxi.fuxi.leecode.Str;

/**
 * 字符交换 描述： 把一个0-1（只包含0和1的串）进行排序，你可以交换任意两个位置，问最少交换多少次？
 * eg ： 输入 ： str = "00101110101100101" 输出：5
 * 那如果每次只能相邻两个数交换 最少多少次呢
 * 思路： 快排思路
 */
public class CharExchange {
    private static int charExchange(String string) {
        char[] chars = string.toCharArray();
        int left = 0;
        int right = chars.length - 1;
        int change = 0;
        while (left < right) {
            while (left < right && chars[left] == '0') {
                left++;
            }
            while (left < right && chars[right] == '1') {
                right--;
            }
            char temp = chars[right];
            chars[right] = chars[left];
            chars[right] = temp;
            change++;
        }
        return change;
    }

    public static void main(String[] args) {
        String string = "00101110101100101";
        System.out.println(charExchange(string));
    }
}
