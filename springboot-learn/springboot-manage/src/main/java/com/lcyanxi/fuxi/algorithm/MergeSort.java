package com.lcyanxi.fuxi.algorithm;

/**
 * 思想：
 * 将两个或两个以上的有序序列合并成一个新的有序序列
 * 合并的方法是比较各子序列的第一个记录的键值，最小的一个就是排序的第一个记录的键值。
 * 取出这个记录，继续比较各子序列现有的第一个记录的键值，便可以找出排序后的第二个键值。
 * 然后开始递归，最终得到排序结果。
 * eg:arr = [5,4,7,1,2,8,9]    输出：[1,2,4,5,7,8,9]
 */
public class MergeSort {
    public static void main(String[] args) {
        int[] arr = new int[]{5,4,7,1,2,8,9};
        int[]temp = new int[arr.length];
        process(arr,0,arr.length-1, temp);
        for (int ar: arr){
            System.out.print(ar);
        }
    }

    public static void process(int[] nums, int left , int right, int[] temps){
        if (left>= right){
            return;
        }
        int mid = (left + right) / 2;
        process(nums, left, mid, temps);
        process(nums, mid +1,right, temps);
        merge(nums,left,mid,right,temps);
    }
    private static void merge(int[] nums, int left, int mid, int right, int[] temps){
        int l = left;
        int r = mid +1;
        int index = 0;
        while (l <= mid && r <= right){
            if (nums[l] < nums[r]){
                temps[index++] = nums[l++];
            }else {
                temps[index++] = nums[r++];
            }
        }
        while (l <=mid){
            temps[index++] = nums[l++];
        }
        while (r <= right){
            temps[index++] = nums[r++];
        }
        index = 0;
        while (left <= right){
            nums[left++] = temps[index++];
        }
    };
}
