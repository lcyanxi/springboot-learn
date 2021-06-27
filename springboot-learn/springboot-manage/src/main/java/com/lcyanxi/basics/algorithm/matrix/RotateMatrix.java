package com.lcyanxi.basics.algorithm.matrix;

/**
 * 旋转矩阵:
 * 给你一幅由 N × N 矩阵表示的图像，其中每个像素的大小为 4 字节。请你设计一种算法，将图像旋转 90 度。
 * 不占用额外内存空间能否做到？
 * eg:
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
 * @author lichang
 * @date 2020/11/28
 */
public class RotateMatrix {
    public static void main(String[] args) {
        int[][] arr = {
                {1,2,3},
                {4,5,6},
                {7,8,9}
        };
        int[][] matrix = rotateMatrix(arr);
        for (int[] array : matrix){
            for (int e : array){
                System.out.print(e);
            }
            System.out.println();
        }
    }

    // 占用而外空间
    private static int[][] rotateMatrix(int[][] arr){
        if (arr == null){
            return null;
        }
        int temp = arr[0].length;
        int[][] newArr = new int[temp][arr.length];
        for (int i = 0; i < arr.length; i++){
            int[] tmp = arr[0];
            for (int j = 0 ;j < tmp.length; j++){
                newArr[j][temp -1] = arr[i][j];
            }
            temp --;
        }
        return newArr;
    }
    private static void rotateMatrix2(int[][] arr){
        int left = 0;
        int top = 0;
        int right = arr[0].length;
        int bottom = arr.length;
        int index = 2;
        while (index > 0){
            while (top <= bottom && left <= right){
                int temp = 0;
                for (int i = left ;i < right; i ++){
                    temp = arr[top][left];
                    arr[top][left] = 0;
                }
            }
            index --;
        }
    }

}
