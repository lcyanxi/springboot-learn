package com.lcyanxi.fuxi.leecode.matrix;

import java.util.ArrayList;
import java.util.List;

/**
 * 54. 螺旋矩阵
 * 描述：输入一个矩阵，按照从外向里以顺时针的顺序依次打印出每一个数字。
 * 输入：matrix = [
 *                 [1,2,3],
 *                 [4,5,6],
 *                 [7,8,9]
 *               ]
 * <p>
 * 输出：[1,2,3,6,9,8,7,4,5]
 * <p>
 * 思路：第一上 下 左 右 四个指针，从左到右 从上到下 从右到左 从下到上遍历
 */
public class No54 {
    public static List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        int left = 0;
        int right = matrix[0].length -1;
        int top = 0;
        int bottom = matrix.length-1;
        while (true){
            for (int i = left; i <= right; i++){
                result.add(matrix[top][i]);
            }
            top++;
            if (top > bottom){
                break;
            }
            for (int i = top ; i<=bottom;i++){
                result.add(matrix[i][right]);
            }
            right--;
            if (right < left){
                break;
            }
            for (int i = right; i>= left; i--){
                result.add(matrix[bottom][i]);
            }
            bottom--;
            if (bottom < top){
                break;
            }
            for (int i = bottom; i>= top;i--){
                result.add(matrix[i][left]);
            }
            left++;
            if (left > right){
                break;
            }
        }
        return result;
    }

    public static List<Integer> spiralOrder2(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        int left = 0;
        int right = matrix[0].length - 1;
        int top = 0;
        int bottom = matrix.length - 1;

        int direction = 0;// 方向 0右 1下 2左 3上
        while (left <= right && top <= bottom) {
            switch (direction) {
                case 0:
                    for (int i = left; i <= right; i++) {
                        result.add(matrix[top][i]);
                    }
                    top++;
                    direction = 1;
                    break;
                case 1:
                    for (int i = top; i <= bottom; i++) {
                        result.add(matrix[i][right]);
                    }
                    right--;
                    direction = 2;
                    break;
                case 2:
                    for (int i = right; i >= left; i--) {
                        result.add(matrix[bottom][i]);
                    }
                    bottom--;
                    direction = 3;
                    break;
                case 3:
                    for (int i = bottom; i >= top; i--) {
                        result.add(matrix[i][left]);
                    }
                    left++;
                    direction = 0;
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[][] aa = {
                {1,2,3,4},
                {5,6,7,8},
                {9,10,11,12}};
        System.out.println(spiralOrder(aa));
    }
}
