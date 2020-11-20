package com.lcyanxi.algorithm.linkedList;

import javax.xml.soap.Node;

/**
 * 删除链表的节点
 * 给定单向链表的头指针和一个要删除的节点的值，定义一个函数删除该节点。
 * 返回删除后的链表的头节点。
 * eg:
 * 输入: head = [4,5,1,9], val = 5
 * 输出: [4,1,9]
 * 解释: 给定你链表中值为 5 的第二个节点，那么在调用了你的函数之后，该链表应变为 4 -> 1 -> 9.
 * @author lichang
 * @date 2020/11/20
 */
public class DeleteNode {
    public static void main(String[] args) {
        ListNode head = null;
        for (int i = 5; i >0 ;i--){
            ListNode temp = new ListNode(i);
            temp.next = head;
            head = temp;
        }
        System.out.println(deleteNode(head,4));

    }
    private static ListNode deleteNode(ListNode head,int key){
        //如果要删除的是头结点，直接返回头结点的下一个结点即可
        if (key == head.val){
             return  head.next;
        }
        ListNode cur = head;
        //找到要删除结点的上一个结点
        while (cur.next != null && cur.next.val != key){
           cur = cur.next;
        }
        cur.next = cur.next.next;
        return head;
    }
}
