package com.lcyanxi.fuxi.algorithm;

/**
 * 快排
 * 先从数列中取出一个数作为基准数。
 * 分区过程，将比这个数大的数全放到它的右边，小于或等于它的数全放到它的左边。
 * 再对左右区间重复第二步，直到各区间只有一个数。
 * eg: 输入：arr = [5,4,7,1,2,8,9]    输出：[1,2,4,5,7,8,9]
 */
public class QuickSort {
    static int [] arr = new int[]{5,4,7,1,2,8,9};
    public static void main(String[] args) {
         quickSort(0,arr.length-1);
         for (int a : arr){
             System.out.print(a);
         }
    }
    public static void quickSort(int left, int right){
        if (left < right){
            int mid = process(left, right);
            quickSort(left,mid);
            quickSort(mid+1,right);
        }
    }
    
    private static int  process(int left, int right){
        int temp = arr[left];
        while (left < right){
            while (left < right && temp <arr[right]) right --;
            arr[left] = arr[right];
            while (left < right && temp > arr[left]) left++;
            arr[right] = arr[left];
        }
        arr[left] = temp;
        return left;
    }
}
