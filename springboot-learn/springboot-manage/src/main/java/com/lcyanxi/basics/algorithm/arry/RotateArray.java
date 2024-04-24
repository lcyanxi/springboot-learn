package com.lcyanxi.basics.algorithm.arry;

/**
 * @author : lichang
 * @desc : 旋转数组
 *       输入：6,2,[1,2,3,4,5,6]
 *       返回值：[5,6,1,2,3,4]
 * @since : 2024/04/24/10:09 上午
 */
public class RotateArray {
    public static void main(String[] args) {
        int a[] = new int[] {1, 2, 3, 4, 5, 6};
        solve(6, 2, a);
        for (int val : a) {
            System.out.print(val);
        }
    }

    public static int[] solve(int n, int m, int[] a) {
        if (n == 0 || m % n == 0) {
            return a;
        }
        int k = m % n;
        for (int i = 0; i < k; i++) {
            swap(a);
        }

        return a;
    }

    public static void swap(int[] a) {
        int item = a[a.length - 1];
        for (int i = a.length - 1; i > 0; i--) {
            a[i] = a[i - 1];
        }
        a[0] = item;
    }

}
