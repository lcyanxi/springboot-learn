package com.lcyanxi.offer.matrix;

import java.util.ArrayList;
import java.util.List;

/**
 * 给你一个 m 行 n 列的矩阵 matrix ，请按照 顺时针螺旋顺序 ，返回矩阵中的所有元素。
 *
 * 输入：matrix = [[1,2,3],
 *                [4,5,6],
 *                [7,8,9]]
 * 输出：[1,2,3,6,9,8,7,4,5]
 * @author chang.li
 * @date 2025/10/13
 * @version 1.0
 */
public class No54 {
    public static void main(String[] args) {
        int[][] matrix ={{1,2,3},{4,5,6},{7,8,9}};
        System.out.println(process(matrix));
    }

    private static List<Integer> process2(int[][] matrix){
        List<Integer> res = new ArrayList<>();
        int row = 0;
        int col = 0;
        while (row < matrix.length && col < matrix[0].length){
            while (col < matrix[0].length){
                res.add(matrix[row][col]);
                col++;
            }
            row++;
            while (row < matrix.length){
                res.add(matrix[row][col]);
                row++;
            }
            col--;
            while (row >=0){
                res.add(matrix[row][col]);
                col--;
            }
            row--;
            while (col >=0){
                res.add(matrix[row][col]);
                row--;
            }
        }
        return res;
    }

    private static List<Integer> process(int[][] matrix) {
        List<Integer> res = new ArrayList<>();
        int left = 0;
        int right = matrix[0].length - 1;
        int top = 0;
        int bottom = matrix.length - 1;
        while (left <= right && top <= bottom) {
            for (int i = left; i <= right; i++) {
                res.add(matrix[top][i]);
            }
            top++;
            for (int i = top; i <= bottom; i++) {
                res.add(matrix[i][right]);
            }
            right--;
            for (int i = right; i >= left; i--) {
                res.add(matrix[bottom][i]);
            }
            bottom--;
            for (int i = bottom; i >= top; i--) {
                res.add(matrix[i][left]);
            }
            left++;
        }
        return res;
    }
}
