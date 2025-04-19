package com.lcyanxi.fuxi.leecode.Str;

/**
 * 415. 字符串相加
 * <p>
 * 输入：num1 = "11", num2 = "123"
 * 输出："134"
 */
public class No415 {

    public static String addStrings(String num1, String num2) {
        if (num1 == null){
            return num2;
        }
        if (num2 == null){
            return num1;
        }
        int len1 = num1.length() -1;
        int len2 = num2.length() -1;
        int temp = 0;
        StringBuilder builder = new StringBuilder();
        while (len1 >= 0 || len2 >=0){
            int a1 = len1 < 0 ? 0 :num1.charAt(len1) - '0';
            int b1 = len2 < 0 ? 0 :num2.charAt(len2) - '0';
            int sum = a1 + b1 + temp;
            builder.append(sum % 10);
            temp = sum / 10;
            len1--;
            len2--;
        }
        if (temp != 0){
            builder.append(temp);
        }
        return builder.reverse().toString();
    }

    public static void main(String[] args) {
        System.out.println(addStrings("11","123"));
    }
}
