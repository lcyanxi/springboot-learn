package com.lcyanxi.offer.dp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 打家劫舍 III
 * 小偷又发现了一个新的可行窃的地区。这个地区只有一个入口，我们称之为 root 。
 *
 * 除了 root 之外，每栋房子有且只有一个“父“房子与之相连。一番侦察之后，聪明的小偷意识到“这个地方的所有房屋的排列类似于一棵二叉树”。 如果 两个直接相连的房子在同一天晚上被打劫 ，房屋将自动报警。
 *
 * 给定二叉树的 root 。返回 在不触动警报的情况下 ，小偷能够盗取的最高金额 。
 * 示例 1:
 * 输入: root = [3,2,3,null,3,null,1]
 * 输出: 7
 * 解释: 小偷一晚能够盗取的最高金额 3 + 3 + 1 = 7
 * 示例 2:
 * 输入: root = [3,4,5,1,3,null,1]
 * 输出: 9
 * 解释: 小偷一晚能够盗取的最高金额 4 + 5 = 9
 * @author chang.li
 * @date 2025/10/28
 * @version 1.0
 */
public class No337 {
    public static void main(String[] args) {
        int[] arr = {2, 2, 3, 0, 3, 0};
        TreeNode treeNode = buildTree(arr);
        System.out.println(process2(treeNode));
        System.out.println(process(treeNode));
    }

    private static TreeNode buildTree(int[] nums) {
        /**
         * left = 2*i + 1
         * right = 2*i + 2
         */
        TreeNode[] treeNodes = new TreeNode[nums.length];
        for (int i = 0; i < nums.length; i++) {
            treeNodes[i] = new TreeNode(nums[i]);
        }
        for (int i = 0; i < nums.length; i++) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            if (left < nums.length) {
                treeNodes[i].left = treeNodes[left];
            }
            if (right < nums.length) {
                treeNodes[i].right = treeNodes[right];
            }
        }
        return treeNodes[0];
    }

    private static Integer process2(TreeNode root) {
        int[] ints = handle2(root);
        return Math.max(ints[0], ints[1]);
    }

    /**
     * 当前节点选择不偷：当前节点能偷到的最大钱数 = 左孩子能偷到的钱(包括偷和不偷取最大) + 右孩子能偷到的钱
     * 当前节点选择偷：当前节点能偷到的最大钱数 = 左孩子选择自己不偷时能得到的钱 + 右孩子选择不偷时能得到的钱 + 当前节点的钱数
     */
    private static int[] handle2(TreeNode root) {
        // 当前节点状态： 0 偷  1 不偷
        int[] dp = new int[2];
        if (root == null) {
            return dp;
        }
        int[] left = handle2(root.left);
        int[] right = handle2(root.right);
        // 偷当前节点
        dp[0] = left[1] + right[1] + root.val;
        // 不偷
        dp[1] = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
        return dp;
    }


    /**
     * 解决不了这种 case
     *       2
     *      /  \
     *     1    3
     *     \
     *      4
     *
     */
    private static Integer process(TreeNode root) {
        List<Integer> nums = handle(root);
        if (nums.isEmpty()) {
            return 0;
        }
        if (nums.size() == 1) {
            return nums.get(0);
        }
        if (nums.size() == 2) {
            return Math.max(nums.get(0), nums.get(1));
        }
        int[] dp = new int[nums.size()];
        dp[0] = nums.get(0);
        dp[1] = Math.max(nums.get(0), nums.get(1));
        int max = dp[1];
        for (int i = 2; i < nums.size(); i++) {
            dp[i] = Math.max(dp[i - 2] + nums.get(i), dp[i - 1]);
            max = Math.max(max, dp[i]);
        }
        return max;
    }

    private static List<Integer> handle(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        if (root == null) {
            return res;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            int sum = 0;
            while (size-- > 0) {
                TreeNode node = queue.poll();
                if (node == null) {
                    break;
                }
                sum += node.val;
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
            }
            res.add(sum);
        }
        return res;
    }
}
