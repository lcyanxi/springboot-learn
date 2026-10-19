package com.lcyanxi.offer.matrix;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 给定一个正整数 n，生成一个包含 1 到 n^2 所有元素，且元素按顺时针顺序螺旋排列的正方形矩阵。
 * <p>
 * 示例:
 * <p>
 * 输入: 3 输出: [
 * [ 1, 2, 3 ],
 * [ 8, 9, 4 ],
 * [ 7, 6, 5 ]
 * ]
 * <p>
 * 1  2  3  4
 * 12 13 14 5
 * 11 16 15 6
 * 10 9  8  7
 *
 * @author chang.li
 * @version 1.0
 * @date 2025/10/15
 */
public class No59 {
    public static void main(String[] args) {
        int[][] process = process(5);
        for (int[] data : process) {
            for (int i : data) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    private static int[][] process(int n) {
        int startX = 0;
        int startY = 0;
        int offset = 1;
        int count = 1;
        int temp = n / 2;
        int[][] result = new int[n][n];
        while (temp > 0) {
            int length = n - offset;
            for (int j = startY; j < length; j++) {
                result[startX][j] = count++;
            }
            for (int i = startX; i < length; i++) {
                result[i][length] = count++;
            }
            for (int j = length; j > startY; j--) {
                result[length][j] = count++;
            }
            for (int i = length; i > startX; i--) {
                result[i][startY] = count++;
            }
            temp--;
            startX++;
            startY++;
            offset++;
        }
        if (n % 2 != 0) {
            result[startX][startY] = count;
        }
        return result;
    }
}
