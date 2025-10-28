package com.lcyanxi.offer.dp;

/**
 * 打家劫舍II
 * 你是一个专业的小偷，计划偷窃沿街的房屋，每间房内都藏有一定的现金。这个地方所有的房屋都 围成一圈 ，这意味着第一个房屋和最后一个房屋是紧挨着的。同时，相邻的房屋装有相互连通的防盗系统，如果两间相邻的房屋在同一晚上被小偷闯入，系统会自动报警 。
 * 给定一个代表每个房屋存放金额的非负整数数组，计算你 在不触动警报装置的情况下 ，能够偷窃到的最高金额。
 * 示例 1：
 * 输入：nums = [2,3,2]
 * 输出：3
 * 解释：你不能先偷窃 1 号房屋（金额 = 2），然后偷窃 3 号房屋（金额 = 2）, 因为他们是相邻的。
 *
 * 示例 2：
 * 输入：nums = [1,2,3,1]
 * 输出：4
 * 解释：你可以先偷窃 1 号房屋（金额 = 1），然后偷窃 3 号房屋（金额 = 3）。偷窃到的最高金额 = 1 + 3 = 4 。
 *
 * 示例 3：
 * 输入：nums = [0]
 * 输出：0
 * @author chang.li
 * @date 2025/10/28
 * @version 1.0
 */
public class No213 {
    public static void main(String[] args) {
        int[] nums = {1, 7, 9, 2};
        System.out.println(process(nums));
    }

    /**
     * 首尾元素都不偷： dp[i] = max(dp[i-2] + num[i],dp[i-1])
     * 偷首元素：i 的范围（0, length -1）
     * 偷尾元素：i 的范围（1，length）
     */
    private static Integer process(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        if (nums.length == 1) {
            return nums[0];
        }
        if (nums.length == 2) {
            return Math.max(nums[0], nums[1]);
        }
        Integer num1 = process2(nums, 0, nums.length - 1);
        Integer num2 = process2(nums, 1, nums.length);
        return Math.max(num2, num1);
    }

    private static Integer process2(int[] nums, int start, int end) {
        int[] dp = new int[nums.length];
        dp[start] = nums[start];
        dp[start + 1] = Math.max(nums[start + 1], nums[start]);
        int res = dp[start + 1];
        for (int i = start + 2; i < end; i++) {
            dp[i] = Math.max(dp[i - 2] + nums[i], dp[i - 1]);
            res = Math.max(res, dp[i]);
        }
        return res;
    }
}
