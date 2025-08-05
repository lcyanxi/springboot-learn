package com.lcyanxi.offer.array;

/**
 * 调整数组顺序使奇数位于偶数前面
 * 题目:输入一个整数数组，实现一个函数来调整该数组中数字的顺序，使得所有奇数位于数组的前半部分，所有偶数位于数组的后半部分
 *  输入：nums = [1,2,3,4]
 *  输出：[1,3,2,4]
 *  注：[3,1,2,4] 也是正确的答案之一。
 *  思路： 双指针，运用快排思想
 */
public class No1 {
    /**
     * 思路 1 ： 新创建一个数组，将基数放到数组前面，偶数放到数组后面
     */
    public static void reOrderArray(int[] nums) {
        int length = nums.length;
        if (length <= 1){
            return;
        }
        int[] temp = nums.clone();
        int i = 0; int j = length -1;
        for (int k = 0; k < length ; k++){
            if (temp[k] % 2 != 0){
                nums[i++] = temp[k];
            }else {
                nums[j--] = temp[k];
            }
        }
    }

    /**
     * 思路2 : 双指针 快排思想，奇数放左边， 偶数放右边
     */
    public static void reOrderArray2(int[] nums){
        if (nums.length <= 1){
            return;
        }
        int left = 0; int right = nums.length-1;
        while (left < right){
             while (left < right && nums[left] % 2 != 0){
                 left++;
             }
             while (left < right && nums[right] % 2 ==0){
                 right--;
             }
             int temp = nums[left];
             nums[left] = nums[right];
             nums[right] = temp;
        }
    }

    public static void main(String[] args) {
        int[] nums = {1,2,3,4};
        reOrderArray2(nums);
        for (int i: nums){
            System.out.print(i);
        }
    }
}
