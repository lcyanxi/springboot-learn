package com.lcyanxi.basics.algorithm.linkedList;

/**
 * @author : lichang
 * @desc : 单链表的排序: 给定一个节点数为 n 的无序单链表，对其按升序排序
 *       输入：[1,3,2,4,5]
 *       返回值：{1,2,3,4,5}
 * @since : 2024/04/24/4:16 下午
 */
public class LinkedListSort {
    public static void main(String[] args) {
        ListNode n1 = new ListNode(1);
        ListNode n2 = new ListNode(3);
        ListNode n3 = new ListNode(2);
        ListNode n4 = new ListNode(4);
        ListNode n5 = new ListNode(5);
        ListNode n6 = new ListNode(0);
        n1.next = n2;
        n2.next = n3;
        n3.next = n4;
        n4.next = n5;
        n5.next = n6;

        ListNode listNode = sortInList(n1);
        while (listNode != null) {
            System.out.print(listNode.val);
            listNode = listNode.next;
        }
    }

    /**
     * step 1：首先判断链表为空或者只有一个元素，直接就是有序的。
     * step
     * 2：准备三个指针，快指针right每次走两步，慢指针mid每次走一步，前序指针left每次跟在mid前一个位置。三个指针遍历链表，当快指针到达链表尾部的时候，慢指针mid刚好走了链表的一半，正好是中间位置。
     * step 3：从left位置将链表断开，刚好分成两个子问题开始递归。
     * step 4：将子问题得到的链表合并，参考合并两个有序链表。
     */
    public static ListNode sortInList(ListNode head) {
        // write code here
        if (head == null || head.next == null) {
            return head;
        }
        // 找到中间节点，将链表一分为二
        ListNode left = head;
        ListNode mid = head.next;
        ListNode right = head.next.next;
        while (right != null && right.next != null) {
            left = left.next;
            mid = mid.next;
            right = right.next.next;
        }
        // 切断
        left.next = null;
        return merge(sortInList(head), sortInList(mid));
    }

    private static ListNode merge(ListNode head1, ListNode head2) {
        if (head1 == null) {
            return head2;
        }
        if (head2 == null) {
            return head1;
        }
        // 新建一个头 Node
        ListNode newNode = new ListNode(0);
        ListNode cur = newNode;
        while (head1 != null && head2 != null) {
            if (head1.val < head2.val) {
                cur.next = head1;
                head1 = head1.next;
            } else {
                cur.next = head2;
                head2 = head2.next;
            }
            cur = cur.next;
        }
        if (head1 != null) {
            cur.next = head1;
        } else if (head2 != null) {
            cur.next = head2;
        }
        return newNode.next;
    }
}
