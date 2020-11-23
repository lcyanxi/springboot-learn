package com.lcyanxi.algorithm.arry;

/**
 * 调整数组顺序使奇数位于偶数前面
 * 描述：输入一个整数数组，实现一个函数来调整该数组中数字的顺序，使得所有奇数位于数组的前半部分，所有偶数位于数组的后半部分。
 * eg:
 * 输入：nums = [1,2,3,4]   输出：[1,3,2,4]
 * 注：[3,1,2,4] 也是正确的答案之一。
 * @author lichang
 * @date 2020/11/23
 */
public class ExchangeOddEven {
    public static void main(String[] args) {

        int[] arr = {1,2,3,4,5,6};
        exchangeOddEven(arr);
        for (int i : arr){
            System.out.print(i);
        }
    }
    private static void exchangeOddEven(int[] arr){
        int left = 0;
        int right = arr.length - 1;
        int temp = 0;
        while (left < right){
            while (left < right){
                if (arr[left] % 2 == 0){
                    temp = arr[left];
                    break;
                }
                left ++;
            }
            while (left < right){
                if (arr[right] % 2 != 0){
                   arr[left] = arr[right];
                   arr[right] = temp;
                   break;
                }
                right -- ;
            }
        }
    }
}
