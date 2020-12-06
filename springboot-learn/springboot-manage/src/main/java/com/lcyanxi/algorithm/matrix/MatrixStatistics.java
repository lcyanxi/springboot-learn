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
                {1 ,1 ,1 ,1 ,1 ,1},
                {0 ,0 ,1 ,1 ,1 ,1},
                {0 ,0 ,0 ,0 ,0 ,0},
                {1 ,1 ,1 ,1 ,1 ,1}
        };

        System.out.println(matrixStatistics(matrix));
        System.out.println(matrixStatistics2(matrix));
    }
    // 这中从右上往左下遍历的方式问题，但是如果每一行的数据有100万个元素怎么办？ 还是挨个遍历吗
    private static List<Integer> matrixStatistics(int[][] matrix){
        List<Integer> list = Lists.newArrayList();
        int max = matrix[0].length - 1;
        int j = matrix[0].length - 1;
        for (int i = 0 ; i < matrix.length; i++){
            if (matrix[i][j] == 0){
                continue;
            }
            if (j == 0){
                list.add(i);
                continue;
            }
            while (j > 0 && matrix[i][j - 1] == 1){
                j -- ;
            }
            if (max > j){
                list.clear();
                max = j;
            }
            list.add(i);
        }
        return list;
    }
    // 每一行找是否有1通过二分查找
    private static List<Integer> matrixStatistics2(int[][] matrix){
        List<Integer> list = Lists.newArrayList();
        int j = matrix[0].length - 1;
        for (int i = 0 ; i < matrix.length; i++){
            if (matrix[i][j] == 0){
                continue;
            }
            if (j == 0){
                list.add(i);
                continue;
            }
            int mid = matrixStatisticsUtil(matrix[i],0,j);
            if (mid < j){
                j = mid;
                list.clear();
            }
            list.add(i);
        }
        return list;

    }
    private static int matrixStatisticsUtil(int[] arr,int left,int right){
        int result = 0;
        while (left <= right){
            int mid = (left + right)/2;
            if (arr[mid] == 1){
                result = mid;
                right = mid - 1;
            }else {
                left = mid + 1;
            }
        }
        return result;
    }

}
