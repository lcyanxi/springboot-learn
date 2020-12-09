package com.lcyanxi.util;

/**
 * @author lichang
 * @date 2020/9/28
 */
public class Test01 {
    public static void main(String[] args) {

        String str = "1//";
        String[] split = str.split("/");
        try {
            for (String s : split) {
                int temp = Integer.parseInt(s);
                System.out.println("bbbbbb");
            }
        } catch (Exception e) {
            System.out.println("333333");
        }

        String str1 = "12  ";
        System.out.println(str1.trim());
    }

}
