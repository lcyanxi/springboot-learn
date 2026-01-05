package com.lcyanxi.offer.douboPointer;

/**
 * 977.有序数组的平方
 *
 * 给你一个按 非递减顺序 排序的整数数组 nums，返回 每个数字的平方 组成的新数组，要求也按 非递减顺序 排序。
 *
 * 示例 1：
 *
 * 输入：nums = [-4,-1,0,3,10]
 * 输出：[0,1,9,16,100]
 * 解释：平方后，数组变为 [16,1,0,9,100]，排序后，数组变为 [0,1,9,16,100]
 *
 * @author chang.li
 * @date 2026/1/5
 * @version 1.0
 */
public class No977 {
    public static void main(String[] args) {
        int[] nums = {-7,-3,2,3,11};
        for (int i : sortedSquares2(nums)) {
            System.out.print(i + "  ");
        }

    }

    /**
     * 双指针 O（N）
     */
    public static int[] sortedSquares2(int[] nums) {
        int[] res = new int[nums.length];
        int left = 0;
        int right = nums.length - 1;
        int index = nums.length - 1;
        while (left <= right) {
            if (Math.abs(nums[left]) > Math.abs(nums[right])) {
                res[index--] = nums[left] * nums[left];
                left++;
            }else {
                res[index--] = nums[right] * nums[right];
                right--;
            }
        }
        return res;
    }

    /**
     * 暴力 + 排序
     */
    public static int[] sortedSquares(int[] nums) {
        int[] res = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            res[i] = nums[i] * nums[i];
        }
        // 排序【快排】 O(N * logN)
        sort(res, 0 , nums.length - 1);
        return res;
    }

    private static void sort(int[] nums, int left, int right) {
        if (left >= right) {
            return;
        }
        int mid =  quickSort(nums, left, right);
        sort(nums, left, mid);
        sort(nums, mid + 1, right);
    }

    private static int  quickSort(int[] nums, int left, int right){
        int target = nums[left];
        while (left < right){
            while (left < right && nums[right] > target){
                right--;
            }
            while (left < right && nums[left] < target){
                left++;
            }
            int temp = nums[left];
            nums[left] = nums[right];
            nums[right] = temp;
        }
        nums[left] = target;
        return left;
    }
}
