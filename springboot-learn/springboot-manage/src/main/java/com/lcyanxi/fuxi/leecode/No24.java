package com.lcyanxi.fuxi.leecode;

import java.util.List;

/**
 * 24. 两两交换链表中的节点
 * 给你一个链表，两两交换其中相邻的节点，并返回交换后链表的头节点。你必须在不修改节点内部的值的情况下完成本题（即，只能进行节点交换）。
 * <p>
 * 示例 1：
 * 输入：head = [1,2,3,4]
 * 输出：[2,1,4,3]
 * <p>
 * 示例 2：
 * <p>
 * 输入：head = []
 * 输出：[]
 * <p>
 * 示例 3：
 * <p>
 * 输入：head = [1]
 * 输出：[1]
 * <p>
 * -1 1 2 3 4
 * -1 2 1 3 4
 */
public class No24 {

    public static ListNode swapPairs(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode rest = new ListNode(-1);
        rest.next = head;

        ListNode cur = rest;
        while (cur.next != null && cur.next.next != null) {
            ListNode node1 = cur.next;
            ListNode node2 = cur.next.next;

            cur.next = node2;
            node1.next = node2.next;
            node2.next = node1;

            cur = node1;
        }
        return rest.next;
    }

    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);

        ListNode rest = swapPairs(head);
        while (rest != null) {
            System.out.print(rest.val);
            rest =rest.next;
        }

    }
}
