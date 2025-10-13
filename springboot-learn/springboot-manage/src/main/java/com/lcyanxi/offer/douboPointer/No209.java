package com.lcyanxi.offer.douboPointer;

/**
 * 给定一个含有 n 个正整数的数组和一个正整数 s ，找出该数组中满足其和 ≥ s 的长度最小的 连续 子数组，并返回其长度。如果不存在符合条件的子数组，返回 0。
 * 示例：
 *
 * 输入：s = 7, nums = [2,3,1,2,4,3]
 * 输出：2
 * 解释：子数组 [4,3] 是该条件下的长度最小的子数组。
 * @author chang.li
 * @date 2025/10/13
 * @version 1.0
 */
public class No209 {
    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4, 5};
        System.out.println(process(nums, 11));
        System.out.println(process2(nums, 11));

    }

    /**
     * 滑动窗口：双指针解法
     * @param nums
     * @param target
     * @return
     */
    private static Integer process2(int[] nums, int target) {
        Integer count = 1;
        Integer res = Integer.MAX_VALUE;
        int left = 0;
        int sum = 0;
        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];
            count++;
            while (sum >= target) {
                sum -= nums[left];
                left++;
                count--;
                res = Math.min(res, count);
            }
        }

        return res == Integer.MAX_VALUE ? 0 : res;
    }

    private static Integer process(int[] nums, int target) {
        Integer res = Integer.MAX_VALUE;
        boolean flag = false;
        for (int i = 0; i < nums.length; i++) {
            int sum = nums[i];
            if (sum == target) {
                flag = true;
                res = 1;
                break;
            }
            for (int j = i + 1; j < nums.length; j++) {
                sum = sum + nums[j];
                if (sum >= target) {
                    res = Math.min(res, j - i + 1);
                    flag = true;
                }
            }
        }

        return flag ? res : 0;
    }
}
