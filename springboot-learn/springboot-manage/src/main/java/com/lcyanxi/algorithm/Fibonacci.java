package com.lcyanxi.algorithm;

/**
 * 大家都知道斐波那契数列，现在要求输入一个整数n，请你输出斐波那契数列的第n项（从0开始，第0项为0）n<=39
 * 0 1 1 2 3 5 8 13
 *
 * n = 0, num = 0
 * n = 1, num = 1
 * n = 2, num = 1
 * .....
 *
 * 当 n = k(n>1),f(k) = f(k-1) + f(k-2)
 * 当 n = 1, f(1) = 1
 * 当 n = 0, f(0) = 0
 * @author lichang
 * @date 2020/11/16
 */
public class Fibonacci {
    public static void main(String[] args) {
        Integer aa = aa(2);
        System.out.println(aa);
        System.out.println(bb(2));
    }

    /**
     * 递归会有大量的重复计算
     * @param args 初始值
     * @return
     */
    private static Integer aa(int args){
        if (args == 1){
            return 1;
        }else if (args == 0){
            return 0;
        }else {
            return aa(args - 1) + aa( args - 2);
        }
    }

    private static Integer bb(Integer args){
        if (args == 0){
            return 0;
        }else if (args == 1){
            return 1;
        }
        int temp = 0;
        int a = 1;
        int b = 0;
        for (int i = 2; i < args; i++){
            temp = a + b;
            b = a;
            a = temp;
        }
        return a + b;
    }
}
