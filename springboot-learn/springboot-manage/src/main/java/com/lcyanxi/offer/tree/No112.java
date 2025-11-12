package com.lcyanxi.offer.tree;

import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 路径总和
 * 给你二叉树的根节点 root 和一个表示目标和的整数 targetSum 。判断该树中是否存在 根节点到叶子节点 的路径，这条路径上所有节点值相加等于目标和 targetSum 。如果存在，返回 true ；否则，返回 false 。
 * 叶子节点 是指没有子节点的节点。
 * 示例 1：
 * 输入：root = [5,4,8,11,null,13,4,7,2,null,null,null,1], targetSum = 22
 * 输出：true
 * 解释：等于目标和的根节点到叶节点路径如上图所示。
 * 示例 2：
 *
 *
 * 输入：root = [1,2,3], targetSum = 5
 * 输出：false
 * 解释：树中存在两条根节点到叶子节点的路径：
 * (1 --> 2): 和为 3
 * (1 --> 3): 和为 4
 * 不存在 sum = 5 的根节点到叶子节点的路径。
 * 示例 3：
 *
 * 输入：root = [], targetSum = 0
 * 输出：false
 * 解释：由于树是空的，所以不存在根节点到叶子节点的路径。
 *
 * @author chang.li
 * @date 2025/11/12
 * @version 1.0
 */
public class No112 {
    public static void main(String[] args) {
        Integer[] arr = new Integer[]{5, 4, 8, 11, null, 13, 4, 7, 2, null, null, null, 1};
        TreeNode treeNode = buildTree(arr);
        boolean process = process(treeNode, 22);
        System.out.println(process);
        process = process2(treeNode, 22);
        System.out.println(process);
    }

    /**
     * 迭代法
     */
    private static boolean process2(TreeNode root, int target) {
        if (root == null) {
            return false;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        Queue<Integer> valueQueue = new LinkedList<>();
        queue.add(root);
        valueQueue.add(root.val);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            Integer value = valueQueue.poll();
            if (node.left == null && node.right == null && value == target) {
                return true;
            }
            if (node.left != null) {
                queue.add(node.left);
                valueQueue.add(node.left.val + value);
            }
            if (node.right != null) {
                queue.add(node.right);
                valueQueue.add(node.right.val + value);
            }
        }
        return false;
    }

    private static boolean process(TreeNode root, int target) {
        if (root == null) {
            return false;
        }
        return handle(root, target - root.val);
    }

    /**
     * 叶子节点判断：
     * root.left == null && root.right == null
     */
    private static boolean handle(TreeNode root, int target) {
        if (root.left == null && root.right == null && target == 0) {
            return true;
        }
        if (root.left == null && root.right == null) {
            return false;
        }
        if (root.left != null) {
            target -= root.left.val;
            if (handle(root.left, target)) {
                return true;
            }
            target += root.left.val;
        }

        if (root.right != null) {
            target -= root.right.val;
            if (handle(root.right, target)) {
                return true;
            }
            target += root.right.val;
        }
        return false;
    }

    private static TreeNode buildTree(Integer[] nums) {
        if (nums == null || nums.length == 0) {
            return null;
        }
        TreeNode root = new TreeNode(nums[0]);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        int index = 1;
        while (!queue.isEmpty()) {
            TreeNode poll = queue.poll();
            if (index < nums.length && nums[index] != null) {
                poll.left = new TreeNode(nums[index]);
                queue.add(poll.left);
            }
            index++;
            if (index < nums.length && nums[index] != null) {
                poll.right = new TreeNode(nums[index]);
                queue.add(poll.right);
            }
            index++;
        }
        return root;
    }
}












