package com.lcyanxi.offer.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 翻转二叉树
 * 给你一棵二叉树的根节点 root ，翻转这棵二叉树，并返回其根节点。
 * 示例 1：
 * 输入：root = [4,2,7,1,3,6,9]
 * 输出：[4,7,2,9,6,3,1]
 * 示例 2：
 * 输入：root = [2,1,3]
 * 输出：[2,3,1]
 * 示例 3：
 * 输入：root = []
 * 输出：[]
 *
 * @author chang.li
 * @version 1.0
 * @date 2025/10/24
 */
public class No226 {
    public static void main(String[] args) {
        int[] nums = {4, 2, 7, 1, 3, 6, 9};
        TreeNode root = TreeNode.buildTree(nums);
        TreeNode process = process2(root);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(process);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> temp = new ArrayList<>();
            while (!queue.isEmpty() && size > 0) {
                TreeNode poll = queue.poll();
                if (poll == null) {
                    break;
                }
                temp.add(poll.val);
                if (poll.left != null) {
                    queue.add(poll.left);
                }
                if (poll.right != null) {
                    queue.add(poll.right);
                }
                size--;
            }
            System.out.println(temp);
        }

    }

    private static TreeNode process2(TreeNode root) {
        if (root == null) {
            return null;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            while (!queue.isEmpty() && size > 0) {
                TreeNode poll = queue.poll();
                if (poll == null) {
                    break;
                }
                TreeNode temp = poll.left;
                poll.left = poll.right;
                poll.right = temp;
                if (poll.left != null) {
                    queue.add(poll.left);
                }
                if (poll.right != null) {
                    queue.add(poll.right);
                }
                size--;
            }
        }
        return root;
    }

    private static TreeNode process(TreeNode root) {
        if (root == null) {
            return null;
        }
        process(root.left);
        process(root.right);

        TreeNode temp = root.left;
        root.left = root.right;
        root.right = temp;
        return root;
    }
}
