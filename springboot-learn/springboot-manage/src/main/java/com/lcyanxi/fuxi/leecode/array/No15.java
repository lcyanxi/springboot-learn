package com.lcyanxi.fuxi.leecode.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 15. 三数之和
 * <p>
 * 给你一个整数数组 nums ，判断是否存在三元组 [nums[i], nums[j], nums[k]] 满足 i != j、i != k 且 j != k ，
 * 同时还满足 nums[i] + nums[j] + nums[k] == 0 。请你返回所有和为 0 且不重复的三元组。
 * <p>
 * 注意：答案中不可以包含重复的三元组。
 * <p>
 * 输入：nums = [-1,0,1,2,-1,-4]
 * 输出：[[-1,-1,2],[-1,0,1]]
 * <p>
 * 思路： 先将数组排序 + 转换成两数之和
 * 双指针快速移动
 */
public class No15 {
    static List<List<Integer>> result = new ArrayList<>();

    public static List<List<Integer>> threeSum(int[] nums) {
        int length = nums.length;
        Arrays.sort(nums);

        for (int i = 0; i < length - 2; i++) {
            if (nums[i] > 0) {
                break;
            }
            // 去重
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            int target = -nums[i];
            findSumTo(nums, i, target);
        }
        return result;
    }

    private static void findSumTo(int[] nums, int i, int target) {
        int right = nums.length - 1;
        for (int left = i + 1; left < right; ++left) {
            // 去重
            if (left > i+1 && nums[left] == nums[left - 1]){
                continue;
            }
            while (left < right && nums[left] + nums[right] > target) {
                right--;
            }
            while (left < right && nums[left] + nums[right] < target) {
                left++;
            }
            if (left == right) {
                break;
            }
            if (nums[left] + nums[right] == target) {
                result.add(Arrays.asList(nums[i], nums[left], nums[right]));
            }
        }
    }

    public static void main(String[] args) {
        List<List<Integer>> lists = threeSum(new int[]{0,0,0,0});
        System.out.println(lists);
        System.out.println(threeSum2(new int[]{0,0,0,0}));
    }

    public static List<List<Integer>> threeSum2(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> res = new ArrayList<>();
        for(int k = 0; k < nums.length - 2; k++){
            if(nums[k] > 0) break;
            if(k > 0 && nums[k] == nums[k - 1]) continue;
            int i = k + 1, j = nums.length - 1;
            while(i < j){
                int sum = nums[k] + nums[i] + nums[j];
                if(sum < 0){
                    while(i < j && nums[i] == nums[++i]);
                } else if (sum > 0) {
                    while(i < j && nums[j] == nums[--j]);
                } else {
                    res.add(Arrays.asList(nums[k], nums[i], nums[j]));
                    i++;
                }
            }
        }
        return res;
    }

}
