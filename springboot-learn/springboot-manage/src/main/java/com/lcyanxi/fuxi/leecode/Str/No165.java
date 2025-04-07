package com.lcyanxi.fuxi.leecode.Str;

/**
 * 165. 比较版本号
 * 如果 version1 < version2 返回 -1，
 * 如果 version1 > version2 返回 1，
 * 除此之外返回 0。
 *
 *1. 输入：version1 = "1.2", version2 = "1.10"
 * 输出：-1
 *
 *2. 输入：version1 = "1.01", version2 = "1.001"
 * 输出：0
 *
 *3. 输入：version1 = "1.0", version2 = "1.0.0.0"
 * 输出：0
 *
 * 思路：方法一： 字符串分割 spilt 分割 \\.  从左到右依次转换成 int 比较
 * 方法二：  双指针 跳过 . 号 拼接字符串
 */
public class No165 {

    public static int compareVersion(String version1, String version2) {
        if (version1 == null || version2 == null){
            return -0;
        }
        String[] split1 = version1.split("\\.");
        String[] split2 = version2.split("\\.");

        int length1 = split1.length;
        int length2 = split2.length;

        for (int i = 0;  i<length1 || i<length2; i++){
          int x = 0;
          int y = 0;
          if (i < length1){
              x = Integer.parseInt(split1[i]);
          }
          if (i < length2){
              y = Integer.parseInt(split2[i]);
          }
          if (x > y){
              return 1;
          }
          if (x < y){
              return -1;
          }
        }
        return 0;
    }


    public static int compareVersion2(String version1, String version2) {
        int length1 = version1.length();
        int length2 = version2.length();
        int i =0;
        int j =0;
        while (i < length1 || j < length2){
            int x = 0;
            while ( i<length1 && version1.charAt(i) != '.'){
                x = x * 10 + version1.charAt(i) - '0';
                i++;
            }
            i++;
            int y = 0;
            while (j < length2 && version2.charAt(j) != '.'){
                y = y * 10 + version2.charAt(j) - '0';
                j++;
            }
            j++;
            if (x != y){
                return x < y ? -1:1;
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        System.out.println(compareVersion2("1.2", "1.10"));
    }
}
