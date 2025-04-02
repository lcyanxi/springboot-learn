package com.lcyanxi.fuxi.leecode.tree;

/**
 * 110. 平衡二叉树
 * 给定一个二叉树，判断它是否是 平衡二叉树
 * 平衡二叉树：左右子树高度差不能大于 1
 * 输入：root = [3,9,20,null,null,15,7]
 * 输出：true
 * 递归调用
 */
public class No110 {
    public boolean isBalanced(TreeNode root) {
        if (root == null){
            return true;
        }
        return check(root) != -1;
    }
    private int check(TreeNode node){
        if (node == null){
            return 0;
        }
        int left = check(node.left);
        int right = check(node.right);
        if (left == -1 || right == -1 || Math.abs(left-right) >1){
            return -1;
        }
        return Math.max(left,right) +1;
    }
}
