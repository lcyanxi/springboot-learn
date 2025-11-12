package com.lcyanxi.offer.tree;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 合并二叉树
 *
 * 给你两棵二叉树： root1 和 root2 。
 * 想象一下，当你将其中一棵覆盖到另一棵之上时，两棵树上的一些节点将会重叠（而另一些不会）。你需要将这两棵树合并成一棵新二叉树。合并的规则是：如果两个节点重叠，那么将这两个节点的值相加作为合并后节点的新值；否则，不为 null 的节点将直接作为新二叉树的节点。
 * 返回合并后的二叉树。
 *
 * 注意: 合并过程必须从两个树的根节点开始。
 * 示例 1：
 * 输入：root1 = [1,3,2,5], root2 = [2,1,3,null,4,null,7]
 * 输出：[3,4,5,5,4,null,7]
 * 示例 2：
 *
 * 输入：root1 = [1], root2 = [1,2]
 * 输出：[2,2]
 * @author chang.li
 * @date 2025/11/7
 * @version 1.0
 */
public class No617 {
    public static void main(String[] args) {
        Integer[] arr = {1,3,2,5};
        Integer[] arr1 = {2,1,3,null,4,null,7};
        TreeNode treeNode = process(buildTree(arr), buildTree(arr1));
        while (treeNode != null) {
            System.out.print(treeNode.val);
            treeNode = treeNode.left;
        }
    }

    private static TreeNode buildTree(Integer[] arr) {
        TreeNode root = new TreeNode(arr[0]);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        int index = 1;
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (index < arr.length && arr[index] != null) {
                node.left = new TreeNode(arr[index]);
                queue.add(root.left);
            }
            index++;
            if (index < arr.length && arr[index] != null) {
                node.right = new TreeNode(arr[index]);
                queue.add(root.right);
            }
            index++;
        }
        return root;
    }

    private static TreeNode process(TreeNode root1, TreeNode root2) {
        if (root1 == null) {
            return root2;
        }
        if (root2 == null) {
            return root1;
        }
        root1.val = root1.val + root2.val;
        root1.right = process(root2.right, root1.right);
        root1.left = process(root2.left, root1.left);
        return root1;

    }

    private static TreeNode process2(TreeNode root1, TreeNode root2) {
        if (root1 == null) {
            return root2;
        }
        if (root2 == null) {
            return root1;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root1);
        queue.add(root2);
        while (!queue.isEmpty()) {
            TreeNode node1 = queue.poll();
            TreeNode node2 = queue.poll();
            node1.val = node2.val + node1.val;
            if (node2.left != null && node1.left != null) {
                queue.add(node1.left);
                queue.add(node2.left);
            } else if (node1.left == null && node2.left != null) {
                node1.left = node2.left;
            }

            if (node2.right != null && node1.right != null) {
                queue.add(node1.right);
                queue.add(node2.right);
            } else if (node1.right == null && node2.right != null) {
                node1.right = node2.right;
            }
        }
        return root1;
    }
}
