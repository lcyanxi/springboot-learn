package com.lcyanxi.offer.linkedList;

import lombok.val;

/**
 * 分割链表
 * 给你一个链表的头节点 head 和一个特定值 x ，请你对链表进行分隔，使得所有 小于 x 的节点都出现在 大于或等于 x 的节点之前。
 * 你不需要 保留 每个分区中各节点的初始相对位置。
 * 示例 1：
 * 输入：head = [1,4,3,2,5,2], x = 3
 * 输出：[1,2,2,4,3,5]
 * 示例 2：
 *
 * 输入：head = [2,1], x = 2
 * 输出：[1,2]
 * @author chang.li
 * @date 2025/10/29
 * @version 1.0
 */
public class No0204 {
    public static void main(String[] args) {
        int[] arr = {1, 4, 3, 2, 5, 2};
        Node node = buildNode(arr);
        Node process = process(node, 3);
        while (process != null) {
            System.out.print(process.data);
            process = process.next;
        }
    }

    private static Node process(Node node, int target) {
        Node slow = node;
        Node fast = node;
        while (fast != null) {
            if (fast.data < target) {
                int fastVal = fast.data;
                fast.data = slow.data;
                slow.data = fastVal;
                fast = fast.next;
                slow = slow.next;
            } else {
                while (fast != null && fast.data >= target) {
                    fast = fast.next;
                }
            }
        }
        return node;
    }

    private static Node buildNode(int[] nums) {
        Node temp = new Node(-1, null);
        Node cur = temp;
        for (int i = 0; i < nums.length; i++) {
            cur.next = new Node(nums[i], null);
            cur = cur.next;
        }
        return temp.next;

    }
}
