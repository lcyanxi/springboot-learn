package com.lcyanxi.offer.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 把二叉树打印成多行, 二叉树按层遍列
 * 输入：
 * {8,6,10,5,7,9,11}
 * 返回值：
 * [[8],[6,10],[5,7,9,11]]
 */
public class NC80 {
    public static void main(String[] args) {
        int[] num = {8, 6, 10, 5, 7, 9, 11};
        TreeNode treeNode = TreeNode.buildTree(num);
        ArrayList<ArrayList<Integer>> core = core(treeNode);
        core.forEach(System.out::println);

    }

    public static ArrayList<ArrayList<Integer>> core(TreeNode pRoot) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        if (pRoot == null) {
            return result;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(pRoot);
        while (!queue.isEmpty()) {
            int size = queue.size();
            ArrayList<Integer> list = new ArrayList<>();
            while (size > 0) {
                TreeNode peek = queue.poll();
                if (peek == null) {
                    break;
                }
                list.add(peek.val);
                if (peek.left != null) {
                    queue.add(peek.left);
                }
                if (peek.right != null) {
                    queue.add(peek.right);
                }
                size--;
            }
            result.add(list);
        }
        return result;
    }
}
