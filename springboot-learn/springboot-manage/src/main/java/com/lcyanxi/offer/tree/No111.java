package com.lcyanxi.offer.tree;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 二叉树的最小深度
 * 给定一个二叉树，找出其最小深度。
 * 最小深度是从根节点到最近叶子节点的最短路径上的节点数量。
 * 说明：叶子节点是指没有子节点的节点。
 * 示例 1：
 *
 * 输入：root = [3,9,20,null,null,15,7]
 * 输出：2
 * 示例 2：
 *
 * 输入：root = [2,null,3,null,4,null,5,null,6]
 * 输出：5
 *
 * @author chang.li
 * @date 2025/10/31
 * @version 1.0
 */
@Slf4j
public class No111 {
    public static void main(String[] args) {
        Integer[] arr = {2, null, 3, null, 4, null, 5, null, 6};
        TreeNode root = buildTree(arr);
        TreeNode treeNode = buildTree2(arr);
        System.out.println(process(treeNode));
    }

    private static Integer process(TreeNode treeNode) {
        if (treeNode == null) {
            return 0;
        }
        int left = process(treeNode.left);
        int right = process(treeNode.right);

        if (treeNode.left == null && treeNode.right != null) {
            return right + 1;
        }
        if (treeNode.left != null && treeNode.right == null) {
            return left + 1;
        }
        return Math.min(left, right) + 1;

    }

    private static TreeNode buildTree2(Integer[] nums){
        if (nums ==  null || nums.length==0){
            return null;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        TreeNode root = new TreeNode(nums[0]);
        queue.add(root);
        int idx = 1;
        while (!queue.isEmpty()){
            TreeNode poll = queue.poll();
            if (idx < nums.length && nums[idx] != null){
                poll.left = new TreeNode(nums[idx]);
                queue.add(poll.left);
            }
            idx++;
            if (idx < nums.length && nums[idx] != null){
                poll.right = new TreeNode(nums[idx]);
                queue.add(poll.right);
            }
            idx++;
        }
        return root;
    }

    private static TreeNode buildTree(Integer[] nums) {
        TreeNode[] treeNode = new TreeNode[nums.length];
        for (int i = 0; i < nums.length; i++) {
            treeNode[i] = new TreeNode(nums[i]);
        }

        for (int i = 0; i < nums.length; i++) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            if (left < nums.length && treeNode[left].val != null) {
                treeNode[i].left = treeNode[left];
            }
            if (right < nums.length && treeNode[right].val != null) {
                treeNode[i].right = treeNode[right];
            }
        }
        return treeNode[0];
    }

}
