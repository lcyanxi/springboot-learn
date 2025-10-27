package com.lcyanxi.offer.tree;

/**
 * 对称二叉树
 * 给你一个二叉树的根节点 root ， 检查它是否轴对称。
 * 示例 1：
 * 输入：root = [1,2,2,3,4,4,3]
 * 输出：true
 * 示例 2：
 *
 * 输入：root = [1,2,2,null,3,null,3]
 * 输出：false
 * 提示：
 * 树中节点数目在范围 [1, 1000] 内
 * -100 <= Node.val <= 100
 *
 * @author chang.li
 * @date 2025/10/27
 * @version 1.0
 */
public class No101 {
    public static void main(String[] args) {

    }

    /**
     * 递归
     * left.left == right.right
     * left.right == right.left
     */
    private boolean process(TreeNode root) {
        if (root == null) {
            return true;
        }
        return compare(root.left, root.right);
    }

    private static boolean compare(TreeNode left, TreeNode right) {
        if (left == null && right == null) {
            return true;
        } else if (left == null && right != null) {
            return false;
        } else if (left != null && right == null) {
            return false;
        } else if (left.val != right.val) {
            return false;
        }

        boolean out = compare(left.left, right.right);
        boolean in = compare(left.right, right.left);

        return out && in;
    }
}













