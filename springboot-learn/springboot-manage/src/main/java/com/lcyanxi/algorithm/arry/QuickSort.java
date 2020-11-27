package com.lcyanxi.algorithm.arry;

/**
 * 快排
 * 先从数列中取出一个数作为基准数。
 * 分区过程，将比这个数大的数全放到它的右边，小于或等于它的数全放到它的左边。
 * 再对左右区间重复第二步，直到各区间只有一个数。
 * eg: 输入：arr = [5,4,7,1,2,8,9]    输出：[1,2,4,5,7,8,9]
 * @author lichang
 * @date 2020/11/27
 */
public class QuickSort {
    public static void main(String[] args) {
        int [] arr ={5,4,7,1,1,2,8,9};
        quickSort(arr,0,arr.length - 1);
        for (int e : arr){
            System.out.print(e);
        }

    }
    private static void quickSort(int[] arr,int left,int right){
        if (left < right){
            int mid = quickSortUtil(arr,left,right);
            quickSort(arr,left,mid - 1);
            quickSort(arr,mid + 1,right);
        }

    }

    private static int quickSortUtil(int[] arr,int left, int right){
        int temp = arr[left];
        while (left < right){
            while (left < right && arr[right] >= temp) right --;
            arr[left] = arr[right];
            while (left < right && arr[left] <= temp) left ++;
            arr[right] = arr[left];
        }
        arr[left] = temp;
        return left;
    }
}
