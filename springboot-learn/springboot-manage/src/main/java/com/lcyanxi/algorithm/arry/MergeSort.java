package com.lcyanxi.algorithm.arry;

/**
 * 归并排序
 * 思想：
 * 将两个或两个以上的有序序列合并成一个新的有序序列
 * 合并的方法是比较各子序列的第一个记录的键值，最小的一个就是排序的第一个记录的键值。
 * 取出这个记录，继续比较各子序列现有的第一个记录的键值，便可以找出排序后的第二个键值。
 * 然后开始递归，最终得到排序结果。
 * eg:arr = [5,4,7,1,2,8,9]    输出：[1,2,4,5,7,8,9]
 * @author lichang
 * @date 2020/11/27
 */
public class MergeSort {
    public static void main(String[] args) {

        int[] arr = {5,4,7,1,2,8,9};
        int[] temp = new int[arr.length];
        sort(arr,0,arr.length - 1,temp);
        for (int e : arr){
            System.out.print(e);
        }
    }
    private static void sort(int[] arr,int left,int right,int[] temp){
        if (left < right){
            int mid = (left + right)/2;
            sort(arr,left,mid,temp);
            sort(arr,mid + 1,right,temp);
            mergeSort(arr,left,mid,right,temp);
        }

    }
    private static void mergeSort(int[] arr,int left,int mid,int right,int[] temp){
        int i = left;
        int j = mid + 1;
        int index = 0;
        while (i <= mid && j <= right){
            if (arr[i] < arr[j]){
                temp[index ++] = arr[i ++];
            }else {
                temp[index ++] = arr[j ++];
            }
        }
        while (j <= right){
            temp[index ++] = arr[j++];
        }
        while (i <= mid){
            temp[index ++] = arr[i++];
        }
        index = 0;
        while (left <= right){
            arr[left++] = temp[index++];
        }
    }
}
