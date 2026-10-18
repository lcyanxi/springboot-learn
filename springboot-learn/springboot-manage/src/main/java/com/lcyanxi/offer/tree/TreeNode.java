package com.lcyanxi.offer.tree;

/**
 * @author chang.li
 * @version 1.0
 * @date 2025/10/24
 */
public class TreeNode {
    Integer val;
    TreeNode left;
    TreeNode right;

    TreeNode(Integer val) {
        this.val = val;
    }

    TreeNode(Integer val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }

    /**
     * 数组转化为 TreeNode
     */
    public static TreeNode buildTree(int[] nums) {
        TreeNode[] res = new TreeNode[nums.length];
        // 初始化节点
        for (int i = 0; i < nums.length; i++) {
            res[i] = new TreeNode(nums[i]);
        }
        for (int i = 0; i < nums.length; i++) {
            int leftIndex = 2 * i + 1;
            int rightIndex = 2 * i + 2;
            if (leftIndex < nums.length) {
                res[i].left = res[leftIndex];
            }
            if ((rightIndex) < nums.length) {
                res[i].right = res[rightIndex];
            }
        }
        return res[0];
    }
}
