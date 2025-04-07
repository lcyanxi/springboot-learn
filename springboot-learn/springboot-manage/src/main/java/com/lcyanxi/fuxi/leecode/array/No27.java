package com.lcyanxi.fuxi.leecode.array;

/**
 * 27. 移除元素
 *
 * 给你一个数组 nums 和一个值 val，你需要 原地 移除所有数值等于 val 的元素。元素的顺序可能发生改变。然后返回 nums 中与 val 不同的元素的数量。
 *
 *输入：nums = [3,2,2,3], val = 3
 *输出：2, nums = [2,2,_,_]
 *
 * 输入：nums = [0,1,2,2,3,0,4,2], val = 2
 * 输出：5, nums = [0,1,4,0,3,_,_,_]
 *
 * 双指针
 */
public class No27 {
    public static int removeElement(int[] nums, int val) {
        int left = 0;
        int right = nums.length-1;
        while (left < right){
            if (nums[left] != val){
               left++;
            }else {
                if (nums[right] == val){
                    right--;
                }else {
                    int temp = nums[left];
                    nums[left] = nums[right];
                    nums[right] = temp;
                }
            }

        }
        return left;
    }

    public static void main(String[] args) {
        int i = removeElement(new int[]{3, 2, 2, 3}, 2);
        System.out.println(i);
    }
}
