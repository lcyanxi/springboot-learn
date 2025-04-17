package com.lcyanxi.fuxi.leecode.linkedList;

import com.lcyanxi.fuxi.leecode.linkedList.ListNode;

/**
 * 21. 合并两个有序链表
 *
 * 将两个升序链表合并为一个新的 升序 链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。
 *
 * 输入：l1 = [1,2,4], l2 = [1,3,4]
 * 输出：[1,1,2,3,4,4]
 * 示例 2：
 *
 * 输入：l1 = [], l2 = []
 * 输出：[]
 * 示例 3：
 *
 * 输入：l1 = [], l2 = [0]
 * 输出：[0]
 */
public class No21 {
    public static ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummyHead = new ListNode();
        ListNode cur = dummyHead;
        ListNode cur1 = list1;
        ListNode cur2 = list2;
        while (cur1 != null && cur2 != null){
            int val = cur1.val;
            if (cur1.val < cur2.val){
                cur1 = cur1.next;
            }else {
                val =cur2.val;
                cur2 = cur2.next;
            }
            cur.next = new ListNode(val);
            cur = cur.next;
        }
        cur.next = cur1 == null? cur2 : cur1;
        return dummyHead.next;
    }

    public static void main(String[] args) {
        ListNode list1 = new ListNode(1);
        list1.next = new ListNode(2);
        list1.next.next = new ListNode(4);

        ListNode list2 = new ListNode(1);
        list2.next = new ListNode(3);
        list2.next.next = new ListNode(4);

        ListNode listNode = mergeTwoLists(list1, list2);
        while (listNode != null){
            System.out.print(listNode.val);
            listNode = listNode.next;
        }

    }
}
