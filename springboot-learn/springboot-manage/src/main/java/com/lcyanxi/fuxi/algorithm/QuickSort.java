package com.lcyanxi.fuxi.algorithm;

/**
 * 快排
 * 先从数列中取出一个数作为基准数。
 * 分区过程，将比这个数大的数全放到它的右边，小于或等于它的数全放到它的左边。
 * 再对左右区间重复第二步，直到各区间只有一个数。
 * eg: 输入：arr = [5,4,7,1,2,8,9]    输出：[1,2,4,5,7,8,9]
 */
public class QuickSort {

    private static void quickSort2(int[] num){
        if (num == null || num.length == 0){
            return;
        }
        quick(num,0,num.length-1);
    }

    private static void quick(int[] num, int left , int right){
        if (left >= right){
            return;
        }
        int mid = process(num, left, right);
        quick(num,left,mid);
        quick(num,mid +1 ,right);
    }

    private static int  process(int[] num,int left ,int right){
        int target = num[left];
        while (left < right){
            while (left < right && num[left] < target){
                left ++;
            }
            while (left < right && num[right] > target){
                right--;
            }
            int temp = num[left];
            num[left] = num[right];
            num[right] = temp;
        }
        num[left] = target;
        return left;
    }
    static int [] arr = new int[]{5,4,7,1,2,8,9};
    public static void main(String[] args) {
        quickSort2(arr);
         for (int a : arr){
             System.out.print(a);
         }
    }
}
