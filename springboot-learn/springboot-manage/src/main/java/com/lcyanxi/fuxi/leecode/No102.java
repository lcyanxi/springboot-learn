package com.lcyanxi.fuxi.leecode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 102. 二叉树的层序遍历
 *
 * 给你二叉树的根节点 root ，返回其节点值的 层序遍历 。 （即逐层地，从左到右访问所有节点）。
 *
 * 输入：root = [3,9,20,null,null,15,7]
 * 输出：[[3],[9,20],[15,7]]
 * 示例 2：
 *
 * 输入：root = [1]
 * 输出：[[1]]
 * 示例 3：
 *
 * 输入：root = []
 * 输出：[]
 *
 */
public class No102 {
    public static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> res = new ArrayList<>();
        if (root == null) {
            return res;
        }
        Queue<TreeNode> queue = new LinkedList();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> temp = new ArrayList<>();
            while (size > 0) {
                TreeNode node = queue.poll();
                temp.add(node.val);
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
                size--;
            }
            res.add(temp);
        }
        return res;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(3);
        TreeNode treeNode = new TreeNode(20);
        root.right = treeNode;
        root.left = new TreeNode(9);

        treeNode.right = new TreeNode(7);
        treeNode.left = new TreeNode(15);

        List<List<Integer>> lists = levelOrder(root);
        System.out.println(lists);
    }
}
