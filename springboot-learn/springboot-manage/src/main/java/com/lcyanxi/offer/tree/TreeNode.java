package com.lcyanxi.offer.tree;

/**
 * @author chang.li
 * @date 2025/10/24
 * @version 1.0
 */
public class TreeNode {
    Integer val;
    TreeNode left;
    TreeNode right;

    TreeNode(Integer val) {
        this.val = val;
    }

    TreeNode(Integer val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
