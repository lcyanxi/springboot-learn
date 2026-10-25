package com.lcyanxi.offer.sortAlg;

/**
 * 冒泡排序
 * [5, 3, 8, 4, 6]
 * 第一轮遍历：
 * <p>
 * 比较 5 和 3，交换位置，列表变为 [3, 5, 8, 4, 6]。
 * <p>
 * 比较 5 和 8，不交换。
 * <p>
 * 比较 8 和 4，交换位置，列表变为 [3, 5, 4, 8, 6]。
 * <p>
 * 比较 8 和 6，交换位置，列表变为 [3, 5, 4, 6, 8]。
 * <p>
 * 第一轮结束后，最大的元素 8 已经"冒泡"到列表的最后。
 */
public class BubbleSort {
    public static void main(String[] args) {
        int[] arr = {7, 1, 3, 5, 8, 4, 9};
        bubbleSort(arr);
        for (int i : arr) {
            System.out.print(i);
        }
    }

    private static void bubbleSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] > arr[j]) {
                    int temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }
    }

}
