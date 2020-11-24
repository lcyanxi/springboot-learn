package com.lcyanxi.algorithm.arry;

/**
 *  顺时针打印矩阵
 *  描述：输入一个矩阵，按照从外向里以顺时针的顺序依次打印出每一个数字。
 *  eg：
 *  输入：matrix = [
 *                  [1, 2, 3],
 *                  [4, 5, 6],
 *                  [7, 8, 9]
 *                  ]
 *  输出：[1,2,3,6,9,8,7,4,5]
 *
 * @author lichang
 * @date 2020/11/24
 */
public class SpiralOrder {
    public static void main(String[] args) {
        int[][] matrix =  { {1,2,3},
                            {4,5,6},
                            {7,8,9}
                          };
        int[] ints = spiralOrder(matrix);
        for (int e : ints){
            System.out.print(e);
        }

    }
    // 定义top、left、right、bottom四个子针
    private static int[] spiralOrder(int[][] matrix){
        int top = 0;
        int left = 0;
        int right = matrix[0].length - 1;
        int bottom = matrix.length - 1;
        int[] newArr = new int[matrix[0].length * matrix.length];
        int index = 0;
        while (left <= right && top <= bottom){
            for (int i = left ; i <= right; i++){
                newArr[index ++] = matrix[top][i];
            }
            top ++;
            for (int i  = top; i <= bottom; i++){
                newArr[index ++] = matrix[i][right];
            }
            right --;
            for (int i = right;i >= left;i--){
                newArr[index ++] = matrix[bottom][i];
            }
            bottom --;
            for (int i = bottom; i >= top; i--){
                newArr[index ++] = matrix[i][left];
            }
            left ++;

        }
        return newArr;
    }
}
