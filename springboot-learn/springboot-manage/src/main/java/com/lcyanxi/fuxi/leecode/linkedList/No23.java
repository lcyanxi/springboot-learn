package com.lcyanxi.fuxi.leecode.linkedList;

import java.util.List;

/**
 * 23. 合并 K 个升序链表
 * 给你一个链表数组，每个链表都已经按升序排列。
 * <p>
 * 请你将所有链表合并到一个升序链表中，返回合并后的链表。
 * <p>
 * 输入：lists = [
 *                [1,4,5],
 *                [1,3,4],
 *                [2,6]
 *                ]
 * 输出：[1,1,2,3,4,4,5,6]
 * <p>
 * 输入：lists = []
 * 输出：[]
 */
public class No23 {

    /**
     * 合并两个有序链表
     */
    public static ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }
        ListNode res = null;
        for (ListNode listNode : lists) {
            res = mergelist(listNode, res);
        }
        return res;
    }

    private static ListNode mergelist(ListNode node1, ListNode node2) {
        ListNode dummyHead = new ListNode(0);
        ListNode cur = dummyHead;
        while (node1 != null && node2 != null) {
            ListNode temp = null;
            if (node1.val < node2.val) {
                temp = node1;
                node1 = node1.next;
            } else {
                temp = node2;
                node2 = node2.next;
            }
            cur.next = temp;
            cur = cur.next;
        }
        cur.next = node1 == null ? node2 : node1;
        return dummyHead.next;
    }

    /**
     * 两两合并， 思想跟快排一样
     */
    public static ListNode mergeKLists2(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }
        return merge(lists, 0, lists.length - 1);
    }

    private static ListNode merge(ListNode[] lists, int left, int right) {
        if (left == right) {
            return lists[left];
        }
        int mid = (left + right) / 2;
        ListNode leftNode = merge(lists, left, mid);
        ListNode rightNode = merge(lists, mid + 1, right);
        return mergelist(leftNode, rightNode);
    }


    public static void main(String[] args) {
        ListNode node1 = new ListNode(1);
        node1.next = new ListNode(2);

    }

}
