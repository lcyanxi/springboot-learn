package com.lcyanxi.fuxi.leecode.tree;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2025/03/31/11:06 下午
 * 101. 对称二叉树
 * 给你一个二叉树的根节点 root ， 检查它是否轴对称。
 *      1
 *   2      2
 * 3  4    4  3
 *
 *
 * 输入：root = [1,2,2,3,4,4,3]
 * 输出：true
 *
 * 思路： 递归， 判断两个节点  Node1.left == Node2.right && Node1.right == Node2.left
 */
public class No101 {
    public static boolean isSymmetric(TreeNode root) {
        if (root == null){
            return true;
        }
        return check(root.left,root.right);
    }

    private static boolean check(TreeNode left , TreeNode right){
        if (left == null && right == null){
            return true;
        }
        if (left == null && right !=null ){
            return false;
        }
        if (left != null && right == null){
            return false;
        }
        if (left.val != right.val){
            return false;
        }
        return check(left.left,right.right) && check(left.right,right.left);
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        TreeNode left = new TreeNode(2);
        TreeNode right = new TreeNode(2);
        left.left = new TreeNode(3);
        left.right = new TreeNode(4);
        right.left = new TreeNode(4);
        right.right = new TreeNode(3);
        root.right = right;
        root.left =left;

        System.out.println(isSymmetric(root));
    }
}
