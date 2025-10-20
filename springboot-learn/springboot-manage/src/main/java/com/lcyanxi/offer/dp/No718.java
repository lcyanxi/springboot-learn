package com.lcyanxi.offer.dp;

/**
 * 给两个整数数组 A 和 B ，返回两个数组中公共的、长度最长的子数组的长度。
 *
 * 示例：
 *
 * 输入：
 *
 * A: [1,2,3,2,1]
 * B: [3,2,1,4,7]
 * 输出：3
 * 解释：长度最长的公共子数组是 [3, 2, 1] 。
 * @author chang.li
 * @date 2025/10/16
 * @version 1.0
 */
public class No718 {
    public static void main(String[] args) {
        int[] numsA = {1, 2, 3, 2, 1};
        int[] numsB = {3, 2, 1, 4, 7};
        System.out.println(process(numsA, numsB));
        System.out.println(process2(numsA, numsB));
    }

    /**
     * 动态规划：
     * i-1 = j-1 : dp[i][j] = dp[i-1]dp[j-1] + 1
     *
     */
    private static Integer process2(int[] numsA, int[] numsB) {
        int[][] dp = new int[numsA.length + 1][numsB.length + 1];
        int res = 0;
        for (int i = 1; i < numsA.length + 1; i++) {
            for (int j = 1; j < numsB.length + 1; j++) {
                if (numsA[i - 1] == numsB[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    res = Math.max(res, dp[i][j]);
                }
            }
        }
        return res;
    }

    /**
     * 暴力解法： N^3
     * 例子：A=[1,2,3,2,1], B=[3,2,1,4,7]
     * 当i=0，j=0: A[0]=1, B[0]=3 -> 不匹配
     * 当i=0，j=1: A[0]=1, B[1]=2 -> 不匹配
     * 当i=0，j=2: A[0]=1, B[2]=1 -> 匹配，然后向后比较：A[1]=2, B[3]=4 -> 不匹配，所以长度为1。
     * 当i=1，j=0: A[1]=2, B[0]=3 -> 不匹配
     * 当i=1，j=1: A[1]=2, B[1]=2 -> 匹配，然后向后比较：A[2]=3, B[2]=1 -> 不匹配，所以长度为1。
     * 当i=1，j=2: A[1]=2, B[2]=1 -> 不匹配
     * 当i=2，j=0: A[2]=3, B[0]=3 -> 匹配，然后向后比较：A[3]=2, B[1]=2 -> 匹配；A[4]=1, B[2]=1 -> 匹配；然后A到末尾，B到B[2]之后是B[3]=4，所以长度为3。此时更新maxLen=3。
     */
    private static Integer process(int[] numsA, int[] numsB) {
        int result = 0;
        for (int i = 0; i < numsA.length; i++) {
            for (int j = 0; j < numsB.length; j++) {
                if (numsA[i] != numsB[j]) {
                    continue;
                }
                result = Math.max(result, check(i, j, numsA, numsB));
            }
        }
        return result;
    }

    private static Integer check(int i, int j, int[] numsA, int[] numsB) {
        int temp = 0;
        while (i < numsA.length && j < numsB.length) {
            if (numsA[i] == numsB[j]) {
                i++;
                j++;
                temp++;
            } else {
                break;
            }
        }
        return temp;
    }

}
