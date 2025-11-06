package com.lcyanxi.offer.linkedList;

/**
 * 61. 旋转链表
 * <p>
 * 给你一个链表的头节点 head ，旋转链表，将链表每个节点向右移动 k 个位置。
 * <p>
 * 输入：head = [1,2,3,4,5], k = 2
 * 输出：[4,5,1,2,3]
 * <p>
 * 输入：head = [0,1,2], k = 4
 * 输出：[2,0,1]
 * <p>
 * 双指针 + 闭合环
 */
public class No61 {
    public static void main(String[] args) {
        int[] arr = {0,1,2};
        Node node = NodeUtil.buildNode(arr);
        Node process = process(node, 4);
        while (process != null) {
            System.out.print(process.data);
            process = process.next;
        }

    }

    private static Node process(Node head, int target) {
        Node cur = head;
        while (cur.next != null) {
            cur = cur.next;
        }
        cur.next = head;

        while (target-- >=0) {
            cur = cur.next;
        }
        head = cur.next;
        cur.next = null;
        return head;
    }

}
