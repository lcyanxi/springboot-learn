package com.lcyanxi.offer.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 给定一个正整数 n，生成一个包含 1 到 n^2 所有元素，且元素按顺时针顺序螺旋排列的正方形矩阵。
 *
 * 示例:
 *
 * 输入: 3 输出: [
 *                 [ 1, 2, 3 ],
 *                 [ 8, 9, 4 ],
 *                 [ 7, 6, 5 ]
 *              ]
 *
 *             1  2  3  4
 *             12 13 14 5
 *             11 16 15 6
 *             10 9  8  7
 *
 * @author chang.li
 * @date 2025/10/15
 * @version 1.0
 */
public class No59 {
    public static void main(String[] args) {
        System.out.println(process(4));
    }

    private static List<List<Integer>> process(int n) {
        int[][] res = new int[n][n];
        int count = 1;
        int row = 0;
        int col = 0;
        int offset = 1;
        int flag = n / 2;
        while (offset <= flag) {
            int i = row;
            int j = col;
            for (; j < n - offset; j++) {
                res[i][j] = count++;
            }
            for (; i < n - offset; i++) {
                res[i][j] = count++;
            }
            for (; j > col; j--) {
                res[i][j] = count++;
            }
            for (; i > row; i--) {
                res[i][j] = count++;
            }
            row++;
            col++;
            offset++;
        }

        if (flag == 1) {
            res[row][col] = count;
        }

        List<List<Integer>> resList = new ArrayList<>();
        for (int[] r : res) {
            List<Integer> list = Arrays.stream(r).boxed().collect(Collectors.toList());
            resList.add(list);
        }
        return resList;
    }
}
