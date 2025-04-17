package com.lcyanxi.fuxi.leecode.linkedList;

import com.lcyanxi.fuxi.leecode.linkedList.ListNode;

/**
 * 141. 环形链表
 *
 * 给你一个链表的头节点 head ，判断链表中是否有环。
 *
 * 如果链表中有某个节点，可以通过连续跟踪 next 指针再次到达，则链表中存在环。 为了表示给定链表中的环，评测系统内部使用整数 pos 来表示链表尾连接到链表中的位置（索引从 0 开始）。注意：pos 不作为参数进行传递 。仅仅是为了标识链表的实际情况。
 *
 * 如果链表中存在环 ，则返回 true 。 否则，返回 false 。
 *
 * 输入：head = [3,2,0,-4], pos = 1
 * 输出：true
 * 解释：链表中有一个环，其尾部连接到第二个节点。
 *
 * 输入：head = [1,2], pos = 0
 * 输出：true
 * 解释：链表中有一个环，其尾部连接到第一个节点。
 *
 * 输入：head = [1], pos = -1
 * 输出：false
 * 解释：链表中没有环。
 *
 * 思路：双指针： 快指针走两步 慢指针走一步 如果相遇就是环形
 *
 */
public class No141 {
    public static boolean hasCycle(ListNode head) {
        if (head == null){
            return false;
        }
        ListNode  fast = head;
        ListNode slow = head;
        while (fast != null && fast.next != null){
            fast= fast.next.next;
            slow = slow.next;
            if (fast == slow){
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        ListNode head = new ListNode(3);
        head.next = new ListNode(2);
        head.next.next = new ListNode(0);
        head.next.next.next = new ListNode(-4);
        head.next.next.next.next = head.next;

        System.out.println(hasCycle(head));
    }
}
