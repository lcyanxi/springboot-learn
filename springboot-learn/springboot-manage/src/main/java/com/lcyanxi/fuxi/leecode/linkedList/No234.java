package com.lcyanxi.fuxi.leecode.linkedList;

import java.util.ArrayList;
import java.util.List;

/**
 * 234. 回文链表
 *
 * 给你一个单链表的头节点 head ，请你判断该链表是否为回文链表。如果是，返回 true ；否则，返回 false 。
 * 输入：head = [1,2,3,4,3,2,1]
 * 输出：true
 *
 * 思路： 后半部分链表反转
 */
public class No234 {
    public static boolean isPalindrome(ListNode head) {
        if (head == null || head.next == null) {
            return true;
        }
        ListNode cur = head;
        List<Integer> list = new ArrayList<>();
        while (cur != null) {
            list.add(cur.val);
            cur = cur.next;
        }
        int left = 0;
        int right = list.size() - 1;
        while (left < right) {
            if (list.get(left++).equals(list.get(right--))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 思路： 反转后半部分链表
     */
    public static boolean isPalindrome2(ListNode head){
        if (head == null || head.next == null) {
            return true;
        }
        ListNode fast = head;
        ListNode slow = head;
        while (fast != null && fast.next != null){
            fast = fast.next.next;
            slow = slow.next;
        }
        // 找到中间节点
        if (fast == null){
            slow  = slow.next;
        }
        // 反转链表
        reverse(slow);
        fast = head;
        while (slow != null){
            if (slow.val != fast.val){
                return false;
            }
            slow = slow.next;
            fast = fast.next;
        }
        return true;

    }

    private static void reverse(ListNode head){
        ListNode pre = null;
        ListNode curr = head;
        while (curr != null){
            ListNode next = curr.next;
            curr.next = pre;
            pre = curr;
            curr = next;
        }
    }

    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(2);
        head.next.next.next = new ListNode(1);
        boolean palindrome = isPalindrome2(head);
        System.out.println(palindrome);
    }
}
