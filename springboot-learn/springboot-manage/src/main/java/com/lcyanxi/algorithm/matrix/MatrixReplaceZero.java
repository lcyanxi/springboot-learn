package com.lcyanxi.algorithm.matrix;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 二维数据消除0
 * 描述：给你一个二维数组matrix,所有数据都是整数，请你把其中所有的0做上下左右方向的延迟，放回处理之后的matrix
 * 比如：注意其中有两个0，那么每个0都向上下左右延伸
 *   7 0 1 4 6
 *   6 5 3 2 1
 *   4 2 1 0 3
 *   8 2 1 2 9
 * 应处理成：
 *   0 0 0 0 0
 *   6 0 3 0 1
 *   0 0 0 0 0
 *   8 0 1 0 9
 * 要求：整个处理过程都直接在matrix上发生，除此之外额外空间复杂度请做到O(1),也就是只用有限介个变量
 * @author lichang
 * @date 2020/12/4
 */
public class MatrixReplaceZero {
    public static void main(String[] args) {
        int[][] arr = {
                {7 ,0 ,1 ,4 ,6},
                {6 ,5 ,3 ,2 ,1},
                {4 ,2 ,1 ,0 ,3},
                {8 ,2 ,1 ,2 ,9},
                {0 ,3 ,6 ,4 ,8}
        };
        matrixReplaceZero(arr);
        for (int i = 0 ;i< arr.length ; i++){
            for (int j = 0; j< arr[0].length; j++){
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }

        int[][] arr2 = {
                {7 ,0 ,1 ,4 ,6},
                {6 ,5 ,3 ,2 ,1},
                {4 ,2 ,1 ,0 ,3},
                {8 ,2 ,1 ,2 ,9},
                {0 ,3 ,6 ,4 ,8}
        };
        System.out.println("=================");
        matrixReplaceZero2(arr2);
        for (int i = 0 ;i< arr2.length ; i++){
            for (int j = 0; j< arr2[0].length; j++){
                System.out.print(arr2[i][j] + " ");
            }
            System.out.println();
        }

    }

    /**
     * 不符合题意，运用了额外空间
     * @param arr 原数据
     */
    private static void matrixReplaceZero(int[][] arr){
        if (arr == null){
            return;
        }
        ArrayList<Integer> xList = Lists.newArrayList();
        ArrayList<Integer> yList = Lists.newArrayList();
        for (int i = 0; i < arr.length; i++){
            for (int j = 0; j< arr[0].length; j++){
                if (arr[i][j] == 0){
                    xList.add(i);
                    yList.add(j);
                }
            }
        }
        for (Integer integer : xList){
            for (int i = 0 ; i< arr[0].length; i++){
                arr[integer][i] = 0;
            }
        }

        for (Integer integer : yList){
            for (int i = 0; i< arr.length; i++){
                arr[i][integer] = 0;
            }
        }
    }

    private static void matrixReplaceZero2(int[][] arr){
        // 用于表示第一行和第一列是否有原生的0
        boolean col_x = false;
        boolean col_y = false;
        for (int i = 0; i < arr.length; i++){
            for (int j = 0; j< arr[0].length; j++){
                if (arr[i][j] == 0){
                    if (i == 0){
                        col_x = true;
                    }else {
                        arr[i][0] = 0;
                    }
                    if (j == 0){
                        col_y = true;
                    }else {
                        arr[0][j] = 0;
                    }
                }
            }
        }
        for (int i = 1 ; i< arr.length ; i++){
            if (arr[i][0] == 0){
                for (int j = 1; j< arr[0].length ; j++){
                    arr[i][j] = 0;
                }
            }
            if (arr[0][i] == 0){
                for (int j = 1; j< arr.length ; j++){
                    arr[j][i] = 0;
                }
            }
        }
        // 单独处理第一行和第一列
        if (col_x){
            Arrays.fill(arr[0], 0);
        }
        if (col_y){
            for (int i = 0; i < arr.length; i++){
                arr[i][0] = 0;
            }
        }

    }
}
