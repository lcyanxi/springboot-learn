package com.lcyanxi.offer.tree;

import java.util.*;

/**
 * 二叉树的前序遍历
 * @author chang.li
 * @date 2025/10/21
 * @version 1.0
 */
public class No144 {
    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        TreeNode node = buildTree(arr);
        print(node);
        List<Integer> process = process(node);
        System.out.println(process);
        System.out.println(process2(node));
    }

    /**
     * 层序遍历
     */
    private static void print(TreeNode root) {
        if (root == null) {
            return;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<TreeNode> list = new ArrayList<>();
            while (size-- > 0) {
                TreeNode poll = queue.poll();
                System.out.print(poll.val);
                if (poll.left != null) {
                    list.add(poll.left);
                }
                if (poll.right != null) {
                    list.add(poll.right);
                }
            }
            System.out.println();
            if (!list.isEmpty()) {
                queue.addAll(list);
            }
        }
    }

    /**
     * 迭代法：前序遍历
     * 遍历顺序： 根左右  入栈顺序： 根右左  （先进后出）
     */
    private static List<Integer> process2(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        Stack<TreeNode> queue = new Stack<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode poll = queue.pop();
            res.add(poll.val);
            if (poll.right != null) {
                queue.add(poll.right);
            }
            if (poll.left != null) {
                queue.add(poll.left);
            }
        }
        return res;
    }

    /**
     * 递归： 前序遍历
     */
    private static List<Integer> process(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        handle(root, res);
        return res;
    }

    private static void handle(TreeNode root, List<Integer> res) {
        if (root == null) {
            return;
        }
        res.add(root.val);
        handle(root.left, res);
        handle(root.right, res);
    }

    /**
     * left: 2 * i +1
     * right: 2 * i +2
     */
    private static TreeNode buildTree(int[] nums) {
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
