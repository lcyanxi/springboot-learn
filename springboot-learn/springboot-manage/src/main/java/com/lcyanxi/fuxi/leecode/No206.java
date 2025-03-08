package com.lcyanxi.fuxi.leecode;

/**
 * 206. 反转链表
 * 给你单链表的头节点 head ，请你反转链表，并返回反转后的链表。
 *
 * 输入：head = [1,2,3,4,5]
 * 输出：[5,4,3,2,1]
 *
 * 输入：head = [1,2]
 * 输出：[2,1]
 *
 * 头插法
 */
public class No206 {

    public static ListNode reverseList(ListNode head) {
        if (head == null || head.next == null){
            return head;
        }
        ListNode newHead = new ListNode(0);
        while (head != null){
            ListNode temp  = new ListNode(head.val);
            head = head.next;
            if (newHead.next != null){
                ListNode item = newHead.next;
                newHead.next =temp;
                temp.next = item;
            }else {
                newHead.next = temp;
            }
        }
        return newHead.next;
    }

    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);

        ListNode listNode = reverseList(head);
        while (listNode != null){
            System.out.print(listNode.val);
            listNode = listNode.next;
        }
    }
}
