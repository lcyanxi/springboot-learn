package com.lcyanxi.offer.dp;

/**
 * 给定一个数组 prices ，它的第 i 个元素 prices[i] 表示一支给定股票第 i 天的价格。
 *
 * 你只能选择 某一天 买入这只股票，并选择在 未来的某一个不同的日子 卖出该股票。设计一个算法来计算你所能获取的最大利润。
 *
 * 返回你可以从这笔交易中获取的最大利润。如果你不能获取任何利润，返回 0 。
 *
 * 示例 1：
 *
 * 输入：[7,1,5,3,6,4]
 *
 * 输出：5
 * 解释：在第 2 天（股票价格 = 1）的时候买入，在第 5 天（股票价格 = 6）的时候卖出，最大利润 = 6-1 = 5 。注意利润不能是 7-1 = 6, 因为卖出价格需要大于买入价格；同时，你不能在买入前卖出股票。
 *
 * 示例 2：
 *
 * 输入：prices = [7,6,4,3,1]
 *
 * 输出：0
 * 解释：在这种情况下, 没有交易完成, 所以最大利润为 0。
 * @author chang.li
 * @date 2025/10/20
 * @version 1.0
 */
public class No121 {
    public static void main(String[] args) {
        int[] arr = {7, 1, 5, 3, 6, 4};
        System.out.println(process(arr));
        System.out.println(process2(arr));
        System.out.println(process3(arr));

    }

    /**
     * 确定递推公式
     * 如果第i天持有股票即dp[i][0]， 那么可以由两个状态推出来
     *
     * 第i-1天就持有股票，那么就保持现状，所得现金就是昨天持有股票的所得现金 即：dp[i - 1][0]
     * 第i天买入股票，所得现金就是买入今天的股票后所得现金即：-prices[i]
     * 那么dp[i][0]应该选所得现金最大的，所以dp[i][0] = max(dp[i - 1][0], -prices[i]);
     *
     * 如果第i天不持有股票即dp[i][1]， 也可以由两个状态推出来
     *
     * 第i-1天就不持有股票，那么就保持现状，所得现金就是昨天不持有股票的所得现金 即：dp[i - 1][1]
     * 第i天卖出股票，所得现金就是按照今天股票价格卖出后所得现金即：prices[i] + dp[i - 1][0]
     * 同样dp[i][1]取最大的，dp[i][1] = max(dp[i - 1][1], prices[i] + dp[i - 1][0]);
     */
    private static Integer process3(int[] arr) {
        int[][] dp = new int[arr.length][arr.length];
        dp[0][0] = -arr[0];
        dp[0][1] = 0;
        int max = 0;
        for (int i = 1; i < arr.length; i++) {
            dp[i][0] = Math.max(dp[i - 1][0], -arr[i]);
            dp[i][1] = Math.max(dp[i - 1][1], dp[i - 1][0] + arr[i]);
            max = Math.max(max, Math.max(dp[i][0], dp[i][1]));
        }
        return max;
    }


    /**
     * 记录历史最低点， 当前值 - 最底点记录最大利润
     */
    private static Integer process2(int[] arr) {
        int max = 0;
        int minPrice = Integer.MAX_VALUE;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < minPrice) {
                minPrice = arr[i];
            } else if (arr[i] > minPrice) {
                max = Math.max(max, arr[i] - minPrice);
            }
        }

        return max;
    }

    /**
     * 超时
     */
    private static Integer process(int[] arr) {
        int result = 0;
        int min = arr[0];
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > min) {
                continue;
            }
            min = Math.min(min, arr[i]);
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] < arr[j]) {
                    result = Math.max(result, arr[j] - arr[i]);
                }
            }
        }
        return result;
    }
}
