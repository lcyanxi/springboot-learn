package com.lcyanxi.fuxi.leecode.tree;

import com.lcyanxi.fuxi.leecode.tree.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 94. 二叉树的中序遍历
 *
 *给定一个二叉树的根节点 root ，返回 它的 中序 遍历 。
 * 输入：root = [1,null,2,3]
 * 输出：[1,3,2]
 * 示例 2：
 *
 * 输入：root = []
 * 输出：[]
 * 示例 3：
 *
 * 输入：root = [1]
 * 输出：[1]
 *
 * 二叉树遍历
 * 中序 ： 左根右
 * 先序：根左右
 * 后序：左右根
 *
 *
 */
public class No94 {
    public static  List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        Stack<TreeNode> st = new Stack<>();
        TreeNode cur = root;
        while (cur != null || !st.empty()){
            while (cur != null){
                st.push(cur);
                cur =cur.left;
            }
            TreeNode pop = st.pop();
            res.add(pop.val);
            cur = pop.right;
        }
        return  res;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        TreeNode treeNode = new TreeNode(2);
        root.right = treeNode;
        treeNode.left = new TreeNode(3);
        System.out.println(inorderTraversal(root));
    }
}
