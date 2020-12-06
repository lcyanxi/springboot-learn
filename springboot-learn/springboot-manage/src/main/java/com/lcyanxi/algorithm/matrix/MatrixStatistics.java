package com.lcyanxi.algorithm.matrix;

import com.google.common.collect.Lists;
import java.util.List;

/**
 * 二维数组统计1的个数
 * 描述：给定一个只由0和1组成的二维数组matrix，每一行都可以保证所有的0在左侧，1在右侧，
 *      那些行拥有最多的1，请放入一个列表返回
 * 输入：
 *    0 0 0 0 1 1
 *    0 0 0 0 0 1
 *    0 0 1 1 1 1
 *    1 1 1 1 1 1
 *    0 0 0 0 0 0
 *    1 1 1 1 1 1
 * 输出：[3,5]
 * 思路：每一行从右向左扫描，记录最大值，放入一个临时空间，最终的结果应该是右上到左下的一个过程
 * @author lichang
 * @date 2020/12/6
 */
public class MatrixStatistics {
    public static void main(String[] args) {
        int[][] matrix = {
                {0 ,0 ,0 ,1 ,1 ,1},
                {0 ,0 ,0 ,0 ,0 ,1},
                {0 ,0 ,0 ,1 ,1 ,1},
                {0 ,0 ,0 ,1 ,1 ,1},
                {0 ,0 ,0 ,0 ,0 ,0},
                {0 ,0 ,0 ,1 ,1 ,1}
        };

        System.out.println(matrixStatistics(matrix));
    }
    // 这中从右上往左下遍历的方式问题，但是如果每一行的数据有100万个元素怎么办？ 还是挨个遍历吗
    private static List<Integer> matrixStatistics(int[][] matrix){
        List<Integer> list = Lists.newArrayList();
        int max = 0;
        int j = matrix[0].length - 1;
        for (int i = 0 ; i < matrix.length; i++){
            if (matrix[i][j] == 0){
                continue;
            }
            if (j == 0){
                list.add(i);
                continue;
            }
            int temp = max;
            if (i != 0){
                j --;
            }
            for (; j > 0; j--){
                if (matrix[i][j] == 1){
                    temp ++;
                }else {
                    break;
                }
            }
            j ++;
            if (temp != max) {
                list.clear();
            }
            list.add(i);
        }
        return list;
    }
}
