package com.lcyanxi.fuxi.leecode.matrix;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2025/03/31/10:24 下午
 * 48. 旋转图像
 * 给定一个 n × n 的二维矩阵 matrix 表示一个图像。请你将图像顺时针旋转 90 度。
 * 你必须在 原地 旋转图像，这意味着你需要直接修改输入的二维矩阵。请不要 使用另一个矩阵来旋转图像。
 *
 *  1 2 3        7 4 1
 *  4 5 6   ---> 8 5 2
 *  7 8 9        9 6 3
 *
 * 输入：matrix = [[1,2,3],[4,5,6],[7,8,9]]
 * 输出：[[7,4,1],[8,5,2],[9,6,3]]
 *
 * 思路 第一步 对角线 1 5 9 对折调换， 然后竖着中间对折
 * 1 4 7            7 4 1
 * 2 5 8  --->      8 5 2
 * 3 6 9            9 6 3
 *
 */
public class No48 {
    public static void rotate(int[][] matrix) {
        // 对角线对折
        int n = matrix.length;
        for (int i = 0;i<n;i++){
            for (int j =0;j<i;j++){
                process(matrix,i,j,j,i);
            }
        }
        // 竖着中间对折
        for (int i = 0;i<n;i++){
            for (int j = 0; j<n/2;j++){
                process(matrix,i,j,i,n-j-1);
            }
        }

    }

    private static void process(int[][] matrix,int i1, int j1, int i2,int j2){
        int temp =  matrix[i1][j1];
        matrix[i1][j1] = matrix[i2][j2];
        matrix[i2][j2] = temp;
    }

    public static void main(String[] args) {
        int[][] arr = {
                {1,2,3},
                {4,5,6},
                {7,8,9}
        };
        rotate(arr);
        for (int[] ar:arr){
            for (int a: ar){
                System.out.print(a+" ");
            }
            System.out.println();
        }
    }
}
