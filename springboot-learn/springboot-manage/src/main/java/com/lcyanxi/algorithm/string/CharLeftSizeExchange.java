package com.lcyanxi.algorithm.string;

/**
 * 字符串指定长度整体交换
 * 描述：给定一个字符串str和长度leftSize，请把字符串leftSize左边的整体部分与右边交换
 * 要求额外空间复杂度O(1)
 * eg : 输入：str = "abdesfc" leftSize = 3   输出："esfcabd"
 * @author lichang
 * @date 2020/11/22
 */
public class CharLeftSizeExchange {
    public static void main(String[] args) {
        String str = "abdesfc";
        System.out.println(charLeftSizeExchange1(str,3));
        System.out.println(charLeftSizeExchange2(str,3));
        System.out.println(charLeftSizeExchange3(str,3));
    }

    // 整体循环移动 缺点时间复杂度O(N^2)
    private static String charLeftSizeExchange1(String string,int leftSize){
        char[] chars = string.toCharArray();
        leftSize = chars.length - leftSize;
        while (leftSize > 0){
            char temp = chars[chars.length - 1];
            for (int i = chars.length -1; i > 0; i--){
                chars[i] = chars[i - 1];
            }
            chars[0] = temp;
            leftSize --;
        }
        return String.valueOf(chars);
    }

    // 思路左边的交换，右边的交换  再整体交换
    private static String charLeftSizeExchange2(String string,int leftSize){
        char[] chars = string.toCharArray();
        exchange(chars,0,leftSize - 1);
        exchange(chars,leftSize,chars.length - 1);
        exchange(chars,0,chars.length - 1);
        return String.valueOf(chars);
    }
    private static void exchange(char [] chars,int left, int right){
        char tmp;
        while (left < right){
            tmp = chars[left];
            chars[left] = chars[right];
            chars[right] = tmp;
            left ++;
            right --;
        }
    }

    private static String charLeftSizeExchange3(String string,int leftSize){
        char[] chars = string.toCharArray();
        int left = 0;
        int right = chars.length - 1;
        int leftPart = leftSize;
        int rightPart = right - leftSize;
        int same = Math.min(leftPart,rightPart);
        exchangeUtil(chars,left,right,same);
        int diff = leftPart - rightPart;
        while (diff != 0){
            if (diff > 0){ // 左侧大
                left += same;
                leftPart = diff;
            }else {  // 右侧大
                right -= same;
                rightPart = -diff;
            }
            diff = leftPart - rightPart;
            same = Math.min(leftPart,rightPart);
            exchangeUtil(chars,left,right,same);
        }
        return String.valueOf(chars);
    }
    private static void exchangeUtil(char[] chars,int left,int right,int same){
        char tmp ;
        while (same > 0){
            tmp = chars[left];
            chars[left] = chars[right - same];
            chars[right - same] = tmp;
            left ++ ;
            same --;
        }
    }
}
