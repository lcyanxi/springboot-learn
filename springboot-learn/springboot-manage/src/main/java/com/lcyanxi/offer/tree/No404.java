package com.lcyanxi.offer.tree;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 左叶子之和
 * 给定二叉树的根节点 root ，返回所有左叶子之和。
 * 示例 1：
 * 输入: root = [3,9,20,null,null,15,7]
 * 输出: 24
 * 解释: 在这个二叉树中，有两个左叶子，分别是 9 和 15，所以返回 24
 * 示例 2:
 *
 * 输入: root = [1]
 * 输出: 0
 *
 * @author chang.li
 * @date 2025/11/10
 * @version 1.0
 */
public class No404 {
    public static void main(String[] args) {
        Integer[] aa = new Integer[]{3,9,20,null,null,15,7};
        TreeNode treeNode = buildTree(aa);
        System.out.println(treeNode);

    }

    private static TreeNode buildTree(Integer[] nums) {
        if (nums == null || nums.length == 0) {
            return null;
        }
        TreeNode root = new TreeNode(nums[0]);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        int indx = 1;
        while (!queue.isEmpty()) {
            queue.offer(queue.poll());
            if (indx < nums.length && nums[indx] != null) {
                root.left = new TreeNode(nums[indx]);
                queue.add(root.left);
            }
            indx++;
            if (indx < nums.length && nums[indx] != null) {
                root.right = new TreeNode(nums[indx]);
                queue.add(root.right);

            }
            indx++;
        }
        return root;
    }


    private static Integer process(TreeNode root){
        if (root == null){
            return 0;
        }
        if (root.left == null && root.right == null){
            return 0;
        }
        int left = process(root.left);
        if (root.left != null && root.left.left == null && root.left.right == null){
            left =  root.left.val;
        }
        int right = process(root.right);
        return left + right;

    }
}
