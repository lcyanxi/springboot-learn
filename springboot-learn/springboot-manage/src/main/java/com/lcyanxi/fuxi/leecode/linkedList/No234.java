package com.lcyanxi.fuxi.leecode.linkedList;

import java.util.ArrayList;
import java.util.List;

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
        while (fast.next != null && fast.next.next !=  null){
            fast = fast.next.next;
            slow = slow.next;
        }

        return false;

    }


    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(2);
        head.next.next.next = new ListNode(1);
        boolean palindrome = isPalindrome(head);
        System.out.println(palindrome);
    }
}
