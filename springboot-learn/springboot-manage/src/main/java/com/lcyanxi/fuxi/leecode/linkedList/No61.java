package com.lcyanxi.fuxi.leecode.linkedList;

import com.lcyanxi.fuxi.leecode.linkedList.ListNode;

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
    public static ListNode rotateRight2(ListNode head, int k) {
        if (head == null || head.next == null || k == 0) {
            return head;
        }
        ListNode dummy = new ListNode();

        return dummy.next;
    }



    public static ListNode rotateRight(ListNode head, int k) {
        if (head == null || head.next == null || k == 0) {
            return head;
        }
        ListNode dummy = new ListNode();
        ListNode cur = head;
        ListNode fast = head;
        int len = 1;
        while (fast.next != null) {
            fast = fast.next;
            len++;
        }
        k = (len - k % len) - 1;
        fast.next = cur;
        while (k > 0) {
            cur = cur.next;
            k--;
        }
        dummy.next = cur.next;
        cur.next = null;
        return dummy.next;
    }

    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);

        ListNode listNode = rotateRight(head, 2);
        while (listNode != null) {
            System.out.print(listNode.val);
            listNode = listNode.next;
        }
    }
}
