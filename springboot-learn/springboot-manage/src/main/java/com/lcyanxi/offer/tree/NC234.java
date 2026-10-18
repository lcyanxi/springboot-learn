package com.lcyanxi.offer.tree;

/**
 * 给定一颗节点数为N的二叉树，求其最小深度。最小深度是指树的根节点到最近叶子节点的最短路径上节点的数量。
 * （注：叶子节点是指没有子节点的节点。）
 * <p>
 * 数据范围：
 * 0<=N<=6000
 * 0<=val<=100
 * <p>
 * 你能使用时间复杂度为O(N)，空间复杂度为O(logN)的解法通过本题吗？
 * 示例1
 * {1,2,3,4,5}
 * 返回值：
 * 2
 */
public class NC234 {

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4, 5};
        TreeNode treeNode = TreeNode.buildTree(nums);
        int run = run(treeNode);
        System.out.println(run);
    }


    public static int run(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int left = run(root.left);
        int right = run(root.right);
        if (root.left == null && root.right != null) {
            return 1 + right;
        }
        if (root.left != null && root.right == null) {
            return 1 + left;
        }
        return 1 + Math.min(left, right);
    }
}
