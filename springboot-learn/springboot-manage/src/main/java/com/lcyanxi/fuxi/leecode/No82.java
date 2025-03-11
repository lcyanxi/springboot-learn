package com.lcyanxi.fuxi.leecode;

/**
 * 82. 删除排序链表中的重复元素 II
 * 给定一个已排序的链表的头 head ， 删除原始链表中所有重复数字的节点，只留下不同的数字 。返回 已排序的链表 。
 *
 * 输入：head = [1,2,3,3,4,4,5]
 * 输出：[1,2,5]
 *
 * 输入：head = [1,1,1,2,3]
 * 输出：[2,3]
 *
 */
public class No82 {
    public static ListNode deleteDuplicates(ListNode head) {
        if (head == null || head.next == null){
            return head;
        }
        ListNode newHead = new ListNode(-1);
        newHead.next = head;
        ListNode cur = newHead;
        while (cur.next != null && cur.next.next != null){
            int v = cur.next.next.val;
            if (cur.next.val == v){
                //循环删除当前next节点
                while (cur.next != null && cur.next.val == v) {
                    cur.next = cur.next.next;
                }

            }else {
                cur = cur.next;
            }
        }
        return newHead.next;
    }

    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2) ;
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(3);
        head.next.next.next.next = new ListNode(4);
        head.next.next.next.next.next = new ListNode(4);
        head.next.next.next.next.next.next = new ListNode(5);
        ListNode listNode = deleteDuplicates(head);
        while (listNode != null){
            System.out.print(listNode.val);
            listNode = listNode.next;
        }
    }

}
