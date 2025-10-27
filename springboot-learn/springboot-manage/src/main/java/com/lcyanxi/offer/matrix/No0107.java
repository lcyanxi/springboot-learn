package com.lcyanxi.offer.matrix;

import java.util.Arrays;

/**
 * 旋转矩阵
 * 给你一幅由 N × N 矩阵表示的图像，其中每个像素的大小为 4 字节。请你设计一种算法，将图像旋转 90 度。
 * 不占用额外内存空间能否做到？
 * 示例 1：
 *
 * 给定 matrix =
 * [
 *   [1,2,3],
 *   [4,5,6],
 *   [7,8,9]
 * ],
 *
 * 原地旋转输入矩阵，使其变为:
 * [
 *   [7,4,1],
 *   [8,5,2],
 *   [9,6,3]
 * ]
 * 示例 2：
 *
 * 给定 matrix =
 * [
 *   [ 5, 1, 9,11],
 *   [ 2, 4, 8,10],
 *   [13, 3, 6, 7],
 *   [15,14,12,16]
 * ],
 *
 * 原地旋转输入矩阵，使其变为:
 * [
 *   [15,13, 2, 5],
 *   [14, 3, 4, 1],
 *   [12, 6, 8, 9],
 *   [16, 7,10,11]
 * ]
 * @author chang.li
 * @date 2025/10/27
 * @version 1.0
 */
public class No0107 {
    public static void main(String[] args) {
        int[][] matrix = {{5, 1, 9, 11}, {2, 4, 8, 10}, {13, 3, 6, 7}, {15, 14, 12, 16}};
        process(matrix);

        for (int[] ints : matrix) {
            System.out.println(Arrays.toString(ints));
        }

    }

    /**
     * 对角线交换
     * 竖着中间交换
     */
    private static void process(int[][] matrix) {
        // 对折线
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix[0].length; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }
        // 竖着线
        for (int i = 0; i < matrix.length; i++) {
            int right = matrix[0].length - 1;
            int left = 0;
            while (left < right) {
                int temp = matrix[i][right];
                matrix[i][right] = matrix[i][left];
                matrix[i][left] = temp;
                left++;
                right--;
            }
        }
    }
}
