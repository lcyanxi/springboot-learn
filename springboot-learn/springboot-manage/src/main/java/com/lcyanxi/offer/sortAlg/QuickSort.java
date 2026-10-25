package com.lcyanxi.offer.sortAlg;

/**
 * 7
 * 7 1 3 5 8 4 9
 * <p>
 * 4 1 3 5 8 x 9
 * 4 1 3 5 x 8 9
 * 快排
 */
public class QuickSort {
    public static void main(String[] args) {
        int[] arr = {7, 1, 3, 5, 8, 4, 9};
        quickSort(arr, 0, arr.length - 1);
        for (int i : arr) {
            System.out.print(i);
        }

    }

    private static void quickSort(int[] arr, int left, int right) {
        if (left < right) {
            int mid = core(arr, left, right);
            quickSort(arr, left, mid - 1);
            quickSort(arr, mid + 1, right);
        }
    }

    private static int core(int[] arr, int left, int right) {
        int temp = arr[left];
        while (left < right) {
            while (left < right && arr[right] > temp) {
                right--;
            }
            arr[left] = arr[right];
            while (left < right && arr[left] < temp) {
                left++;
            }
            arr[right] = arr[left];
        }

        arr[left] = temp;
        return left;
    }


}
