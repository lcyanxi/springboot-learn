package com.lcyanxi.basics.algorithm.matrix;

/**
 * 二维数组中的查找:
 * 描述：在一个二维数组中（每个一维数组的长度相同），每一行都按照从左到右递增的顺序排序，
 * 每一列都按照从上到下递增的顺序排序。请完成一个函数，输入这样的一个二维数组和一个整数，
 * 判断数组中是否含有该整数。
 *
 * 1 2 3 4
 * 2 3 4 5
 * 4 6 7 10
 * 9 11 13 15
 * 思路：我们在回到刚刚的规律这块，我们能够发现，给定一个 target，我们可以比较这个数的最后一列
 *      如果这个数在比 某一个数小，那么它肯定处于该行中，进行查找
 * @author lichang
 * @date 2020/11/17
 */
public class MatrixFind {
    public static void main(String[] args) {

        Integer[][] arr = {
                            {1, 2, 3, 4},
                            {2 ,3 ,4 ,5},
                            {4 ,6 ,7 ,10},
                            {9 ,11 ,13 ,15}
                          };
        System.out.println(find(12,arr));
    }
    private static boolean find(Integer num,Integer[][] array){
        for (Integer[] tempArray: array) {
            if (num > tempArray[tempArray.length - 1]){
                continue;
            }
            for (Integer temp : tempArray){
                if (temp.equals(num)){
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
