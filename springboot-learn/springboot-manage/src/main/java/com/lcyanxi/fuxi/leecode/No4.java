package com.lcyanxi.fuxi.leecode;

/**
 * 4. 寻找两个正序数组的中位数
 *
 * 给定两个大小分别为 m 和 n 的正序（从小到大）数组 nums1 和 nums2。请你找出并返回这两个正序数组的 中位数 。
 *
 * 算法的时间复杂度应该为 O(log (m+n)) 。
 *
 * 示例 1：
 *
 * 输入：nums1 = [1,3], nums2 = [2]
 * 输出：2.00000
 * 解释：合并数组 = [1,2,3] ，中位数 2
 * 示例 2：
 *
 * 输入：nums1 = [1,2], nums2 = [3,4]
 * 输出：2.50000
 * 解释：合并数组 = [1,2,3,4] ，中位数 (2 + 3) / 2 = 2.5
 *
 *  123456679
 */
public class No4 {
    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int len1 = nums1.length;
        int len2 = nums2.length;
        int total = len2 + len1;
        int[] nums = new int[total];

        int i = 0;
        int j = 0;
        int k = 0;
        while (i < len1 && j < len2){
            if (nums1[i] < nums2[j]){
                nums[k++] = nums1[i++];
            }else {
                nums[k++] = nums2[j++];
            }
        }
        while ( i< len1){
            nums[k++] = nums1[i++];
        }

        while (j < len2){
            nums[k++] = nums2[j++];
        }

        double result = 0;
        if (total % 2 ==  0){
            result = (nums[total / 2] + nums[total / 2 -1]) / 2.0;
        }else {
            result = nums[total / 2];
        }
        return result;
    }

    public static void main(String[] args) {
        int[] nums1 = new int[]{1,2};
        int[] nums2 = new int[]{3,4};
        double arrays = findMedianSortedArrays(nums1, nums2);
        System.out.println(arrays);
    }
}
