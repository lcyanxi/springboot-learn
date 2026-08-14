package com.lcyanxi.offer.dp;

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
        int[] arr = {3,2,3,0,3,0,1};
        TreeNode treeNode = buildTree(arr);
        Integer integer = process2(treeNode);
        System.out.println(integer);
    }

    private static Integer process2(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int[] core = core(root);
        return Math.max(core[0], core[1]);
    }

    private static int[] core(TreeNode root){
        if (root == null) {
            return new int[]{0,0};
        }
        // dp[0] 偷当前节点  dp[1] 不偷当前节点
        int[] left = core(root.left);
        int[] right = core(root.right);
        int val = root.val + left[1] + right[1];
        int val2 = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
        return new int[]{val,val2};
    }

    private static TreeNode buildTree(int[] nums) {
        TreeNode[] treeNodes = new TreeNode[nums.length];
        for (int i = 0; i < nums.length; i++) {
            treeNodes[i] = new TreeNode(nums[i]);
        }
        // left = 2 * i + 1
        // right = 2 * i + 2
        for (int i = 0; i < treeNodes.length; i++) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            if (left < nums.length ) {
                treeNodes[i].left = treeNodes[left];
            }
            if (right < nums.length ) {
                treeNodes[i].right = treeNodes[right];
            }
        }
        return treeNodes[0];
    }

}
