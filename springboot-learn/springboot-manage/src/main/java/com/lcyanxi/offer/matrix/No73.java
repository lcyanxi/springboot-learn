package com.lcyanxi.offer.matrix;

import java.util.HashSet;
import java.util.Set;

/**
 * 73. 矩阵置零
 * 给定一个 m x n 的矩阵，如果一个元素为 0 ，则将其所在行和列的所有元素都设为 0 。请使用 原地 算法。
 *输入：matrix = [
 *                 [1,1,1],
 *                 [1,0,1],
 *                 [1,1,1]
 *              ]
 *
 * 输出：     [
 *                 [1,0,1],
 *                 [0,0,0],
 *                 [1,0,1]
 *           ]
 *
 * 思路 1： 第一遍 扫描 matrix 记录那些行 那些列有 0
 *         第二遍 将这些行 、 列设置0；
 * @author chang.li
 * @date 2025/11/12
 * @version 1.0
 */
public class No73 {
    public static void main(String[] args) {
        int[][] matrix = {{1,1,1},{1,0,1},{1,1,1}};
        process(matrix);
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                System.out.print(matrix[row][col] + " ");
            }
            System.out.println();
        }

    }
    private static void process(int[][] matrix) {
        Set<Integer> rows = new HashSet<>();
        Set<Integer> cols = new HashSet<>();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == 0) {
                    rows.add(i);
                    cols.add(j);
                }
            }
        }

        for (Integer row : rows) {
            for (int index = 0; index < matrix[0].length; index++) {
                matrix[row][index] = 0;
            }
        }

        for (Integer col : cols) {
            for (int index = 0; index < matrix.length; index++) {
                matrix[index][col] = 0;
            }
        }

    }
}
