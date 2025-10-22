package com.lcyanxi.offer.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author chang.li
 * @date 2025/10/22
 * @version 1.0
 */
public class Nolcr046 {
    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        TreeNode node = buildTree(arr);
        print(node);
        System.out.println(process(node));
    }

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

    private static List<Integer> process(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> temp = new ArrayList<>();
            while (size-- > 0) {
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
            }
            if (!temp.isEmpty()) {
                res.add(temp.get(temp.size() - 1));
            }
        }
        return res;
    }

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            this.val = x;
        }

        TreeNode(int x, TreeNode left, TreeNode right) {
            this.val = x;
            this.left = left;
            this.right = right;
        }
    }

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
