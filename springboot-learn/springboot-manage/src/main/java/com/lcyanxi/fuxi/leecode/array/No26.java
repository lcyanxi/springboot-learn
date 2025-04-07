package com.lcyanxi.fuxi.leecode.array;

/**
 * 26. 删除有序数组中的重复项
 * 给你一个 非严格递增排列 的数组 nums ，请你 原地 删除重复出现的元素，使每个元素 只出现一次 ，返回删除后数组的新长度。
 * 元素的 相对顺序 应该保持 一致 。然后返回 nums 中唯一元素的个数。
 *
 * 示例 1：
 *
 * 输入：nums = [1,1,2]
 * 输出：2, nums = [1,2,_]
 * 解释：函数应该返回新的长度 2 ，并且原数组 nums 的前两个元素被修改为 1, 2 。不需要考虑数组中超出新长度后面的元素。
 * 示例 2：
 *
 * 输入：nums = [0,0,1,1,1,2,2,3,3,4]
 * 输出：5, nums = [0,1,2,3,4]
 * 解释：函数应该返回新的长度 5 ， 并且原数组 nums 的前五个元素被修改为 0, 1, 2, 3, 4 。不需要考虑数组中超出新长度后面的元素。
 *
 * 双指针
 */
public class No26 {

    public static int removeDuplicates(int[] nums) {
        if (nums == null || nums.length ==0){
            return 0;
        }
        int left =0;
        int right = 1;
        while (right < nums.length){
            if (nums[right] == nums[left]){
                right++;
            }else {
                nums[++left] = nums[right];
            }
        }
        return left+1;

    }

    public static void main(String[] args) {
        int i = removeDuplicates(new int[]{1,1,2});
        System.out.println(i);
    }
}
