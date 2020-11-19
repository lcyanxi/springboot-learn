package com.lcyanxi.algorithm.arry;

/**
 * 青蛙跳台阶：
 * 一只青蛙一次可以跳上1级台阶，也可以跳上2级。求该青蛙跳上一个n级的台阶总共有多少种跳法（先后次序不同算不同的结果）
 *
 *     0: 0
 *     1: 1
 *     2: 2 （1,1）、（2）
 *     3: 3  (111) (12) （21）
 *     4: 5  (1111）（22）（112）（211）（121）
 *     5: 8  （11111）（1112）（1121）1211） （122）（2111） （221） （212）
 *     f(n) = f(n-1) + f(n-2)
 * @author lichang
 * @date 2020/11/16
 */
public class JumpFloor {
    public static void main(String[] args) {
        System.out.println(getNum(5));
    }
    private static Integer getNum(Integer args){
        if (args == 0){
            return 0;
        }
        if (args == 1){
            return 1;
        }
        if (args == 2){
            return 2;
        }
        int temp = 0;
        int a = 1;
        int b = 2;
        for (int i = 3 ; i < args; i++){
            temp = a + b;
            a = b;
            b = temp;
        }
        return a + b;
    }
}
