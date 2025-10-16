package com.lcyanxi.offer.linkedList;

import static com.lcyanxi.offer.linkedList.NodeUtil.buildNode;

/**
 * 题意：删除链表中等于给定值 val 的所有节点。
 *
 * 示例 1： 输入：head = [1,2,6,3,4,5,6], val = 6 输出：[1,2,3,4,5]
 *
 * 示例 2： 输入：head = [], val = 1 输出：[]
 *
 * 示例 3： 输入：head = [7,7,7,7], val = 7 输出：[]
 *
 * @author chang.li
 * @date 2025/10/15
 * @version 1.0
 */
public class No203 {
    public static void main(String[] args) {
        int[] nums = {7,7,7,7};
        Node node = buildNode(nums);
        Node res = process(node, 7);
        while (res != null) {
            System.out.print(res.data);
            res = res.next;
        }
    }

    private static Node process(Node head, int val) {
        Node dummyNode = new Node(-1, null);
        dummyNode.next = head;
        Node cur = dummyNode;
        while (cur != null) {
            while (cur.next != null && cur.next.data == val) {
                cur.next = cur.next.next;
            }
            cur = cur.next;
        }
        return dummyNode.next;
    }
}
