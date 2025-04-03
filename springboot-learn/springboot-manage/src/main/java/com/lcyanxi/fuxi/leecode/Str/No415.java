package com.lcyanxi.fuxi.leecode.Str;

/**
 * 415. 字符串相加
 * <p>
 * 输入：num1 = "11", num2 = "123"
 * 输出："134"
 */
public class No415 {
    public static String addStrings(String num1, String num2) {
        if (num1 == null) {
            return num2;
        }
        if (num2 == null) {
            return num1;
        }
        int len1 = num1.length() - 1;
        int len2 = num2.length() - 1;
        StringBuffer sb = new StringBuffer();
        int temp = 0;
        while (len1 >= 0 || len2 >= 0) {
            int char1 = len1 < 0 ? 0 : (num1.charAt(len1) - 48);
            int char2 = len2 < 0 ? 0 : (num2.charAt(len2) - 48);
            int sum = char1 + char2 + temp;
            int num = sum % 10;
            temp = sum / 10;
            sb.append(num);
            len2--;
            len1--;
        }
        if (temp != 0){
            sb.append(temp);
        }
        sb.reverse();
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(addStrings("11","123"));
    }
}
