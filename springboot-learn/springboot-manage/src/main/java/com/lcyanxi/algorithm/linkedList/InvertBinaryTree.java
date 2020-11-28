package com.lcyanxi.algorithm.linkedList;

/**
 * 翻转二叉树
 * 描述：翻转一棵二叉树。
 * eg:
 * 输入：
 *     4
 *    /   \
 *   2     7
 *  / \   / \
 * 1   3 6   9
 * 输出：
 *      4
 *    /   \
 *   7     2
 *  / \   / \
 * 9   6 3   1
 * @author lichang
 * @date 2020/11/28
 */
public class InvertBinaryTree {
    public static void main(String[] args) {
        int[] arr = {4,2,7,1,3,6,9};
        TreeNode node = null;
        for (int e : arr){
            node = buildBinaryTree(node,e);
        }
        System.out.println(node);

        invertBinaryTree(node);
        System.out.println(node);

    }
    private static TreeNode buildBinaryTree(TreeNode node,int data){
        if (node == null){
            return new TreeNode(data);
        }
        TreeNode current = node;
        TreeNode parent;
        while (true){
            parent = current;
            if (current.val > data){
                current = current.left;
                if (current == null){
                    parent.left = new TreeNode(data);
                    break;
                }
            }else {
                current = current.right;
                if (current == null){
                    parent.right = new TreeNode(data);
                    break;
                }
            }
        }
        return node;
    }

    private static void invertBinaryTree(TreeNode tree){
        if (tree == null){
            return;
        }
        TreeNode temp = tree.left;
        tree.left = tree.right;
        tree.right = temp;
        invertBinaryTree(tree.left);
        invertBinaryTree(tree.right);
    }
}
