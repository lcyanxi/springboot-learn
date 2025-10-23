package com.lcyanxi.offer.stack;

import java.util.Stack;

/**
 * 给定 n 个非负整数表示每个宽度为 1 的柱子的高度图，计算按此排列的柱子，下雨之后能接多少雨水。
 * 示例 1：
 * 输入：height = [0,1,0,2,1,0,1,3,2,1,2,1]
 * 输出：6
 * 解释：上面是由数组 [0,1,0,2,1,0,1,3,2,1,2,1] 表示的高度图，在这种情况下，可以接 6 个单位的雨水（蓝色部分表示雨水）。
 * 示例 2：
 *
 * 输入：height = [4,2,0,3,2,5]
 * 输出：9
 * @author chang.li
 * @date 2025/10/23
 * @version 1.0
 */
public class No42 {
    public static void main(String[] args) {
        int[] arr = {4,2,0,3,2,5};
        System.out.println(process(arr));
    }

    private static Integer process(int[] nums) {
        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        int res = 0;
        for (int i = 1; i < nums.length; i++) {
            Integer top = stack.peek(); // 查看栈顶元素
            if (nums[i] < nums[top]) {
                stack.push(i);
            } else if (nums[i] == nums[top]) {
                stack.pop();
                stack.push(i);
            } else {
                while (!stack.isEmpty() && nums[i] > nums[stack.peek()]) {
                    int mid = stack.pop();
                    if (stack.isEmpty()){
                        break;
                    }
                    int left = stack.peek();
                    int height = Math.min(nums[left], nums[i]) - nums[mid];
                    int length = i - left - 1;
                    res += height * length;
                }
                stack.push(i);
            }
        }
        return res;
    }
}
