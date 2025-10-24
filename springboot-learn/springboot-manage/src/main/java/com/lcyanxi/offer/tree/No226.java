package com.lcyanxi.offer.tree;

import java.util.LinkedList;
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
 * @author chang.li
 * @date 2025/10/24
 * @version 1.0
 */
public class No226 {
    public static void main(String[] args) {
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(7);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(9);

        TreeNode process = process(root);
        System.out.println(process.val);

    }

    private static TreeNode process(TreeNode root) {
        if (root == null) {
            return root;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            while (size-- > 0) {
                TreeNode poll = queue.poll();
                if (poll == null) {
                    break;
                }
                TreeNode left = poll.left;
                poll.left = poll.right;
                poll.right = left;
                if (poll.left != null) {
                    queue.add(poll.left);
                }
                if (poll.right != null) {
                    queue.add(poll.right);
                }
            }
        }
        return root;
    }
}
