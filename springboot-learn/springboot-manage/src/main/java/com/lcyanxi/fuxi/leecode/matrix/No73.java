package com.lcyanxi.fuxi.leecode.matrix;

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
 *
 */
public class No73 {
    public static void setZeroes(int[][] matrix) {
        Set<Integer> cow = new HashSet<>();
        Set<Integer> col = new HashSet<>();
        int colLength = matrix[0].length;
        int cowLength = matrix.length;

        for (int i = 0; i<cowLength; i++){
            for (int j=0;j<colLength;j++){
                if (matrix[i][j] == 0){
                    cow.add(i);
                    col.add(j);
                }
            }
        }

        for (int i =0; i<cowLength; i++){
            for (int j = 0; j<colLength; j++){
                if (cow.contains(i) || col.contains(j)){
                    matrix[i][j] = 0;
                }
            }
        }
    }

    public static void main(String[] args) {
        int[][] aa = {{1,1,1},{1,0,1},{1,1,1}};
        setZeroes(aa);

        for (int[] a: aa){
            for (int i :a){
                System.out.print(i);
            }
            System.out.println();
        }
    }
}
