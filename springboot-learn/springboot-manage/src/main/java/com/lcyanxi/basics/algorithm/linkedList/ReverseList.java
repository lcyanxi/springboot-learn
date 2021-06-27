package com.lcyanxi.basics.algorithm.linkedList;


/**
 * 反转链表:
 * 定义一个函数，输入一个链表的头节点，反转该链表并输出反转后链表的头节点。
 * eg:
 * 输入: 1->2->3->4->5->NULL
 * 输出: 5->4->3->2->1->NULL
 * @author lichang
 * @date 2020/11/19
 */
public class ReverseList {
    public static void main(String[] args) {

        ListNode head = null;
        for (int i = 5; i >0 ;i--){
            ListNode temp = new ListNode(i);
            temp.next = head;
            head = temp;
        }
        ListNode node = reverseList(head);
        while (node != null){
            System.out.print(node.val + "->");
            node = node.next;
        }

        System.out.println();
    }
    private static ListNode reverseList(ListNode head){
        ListNode pre = null;
        ListNode cur = head;
        while (cur != null){
            ListNode temp = cur.next;
            cur.next = pre;
            pre = cur;
            cur = temp;
        }
        return pre;
    }
}


