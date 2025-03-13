package com.lcyanxi.fuxi.leecode;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * 215. 数组中的第K个最大元素
 *
 * 定整数数组 nums 和整数 k，请返回数组中第 k 个最大的元素。
 *
 * 请注意，你需要找的是数组排序后的第 k 个最大的元素，而不是第 k 个不同的元素。
 *
 * 你必须设计并实现时间复杂度为 O(n) 的算法解决此问题。
 *
 * 示例 1:
 *
 * 输入: [3,2,1,5,6,4], k = 2
 * 输出: 5
 * 示例 2:
 *
 * 输入: [3,2,3,1,2,4,5,5,6], k = 4
 * 输出: 4
 *
 * 快排思想
 */
public class No215 {
    public static int findKthLargest(int[] nums, int k) {
        if (nums == null || nums.length < k){
            return 0;
        }
        Queue<Integer> queue = new PriorityQueue<>();
        int len = nums.length-1;
        while (len >= 0){
            queue.add(nums[len]);
            if (queue.size() > k){
                queue.poll();
            }
            len --;
        }
        return  queue.peek();
    }

    public static void main(String[] args) {
        int kthLargest = findKthLargest(new int[]{3, 2, 1, 5, 6, 4}, 2);
        System.out.println(kthLargest);
    }
}
