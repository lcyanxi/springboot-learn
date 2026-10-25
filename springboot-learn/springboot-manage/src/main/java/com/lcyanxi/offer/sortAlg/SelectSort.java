package com.lcyanxi.offer.sortAlg;

/**
 * 选择排序
 * 7 1 3 5 8 4 9
 * 思路：每一轮选者一个最小的数
 */
public class SelectSort {
    public static void main(String[] args) {
        int[] arr = {7, 1, 3, 5, 8, 4, 9};
        selectSort(arr);
        for (int i : arr) {
            System.out.print(i);
        }

    }

    private static void selectSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[min]) {
                    min = j;
                }
            }
            if (min != i){
                int temp = arr[i];
                arr[i] = arr[min];
                arr[min] = temp;
            }
        }
    }
}
