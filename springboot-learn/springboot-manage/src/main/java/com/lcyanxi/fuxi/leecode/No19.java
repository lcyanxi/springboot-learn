package com.lcyanxi.fuxi.leecode;

/**
 * 19. 删除链表的倒数第 N 个结点
 * 输入：head = [1,2,3,4,5], n = 2
 * 输出：[1,2,3,5]
 * 示例 2：
 *
 * 输入：head = [1], n = 1
 * 输出：[]
 * 示例 3：
 *
 * 输入：head = [1,2], n = 1
 * 输出：[1]
 */
public class No19 {

    public static ListNode removeNthFromEnd(ListNode head, int n) {

        ListNode cur = new ListNode(0);
        cur.next = head;
        ListNode fast = cur;
        ListNode slow =cur;

        while (n--> 0){
            fast =fast.next;
        }
        while (fast != null && fast.next != null){
            fast =fast.next;
            slow = slow.next;
        }

        slow.next = slow.next.next;
        return cur.next;
    }

    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);

        ListNode listNode = removeNthFromEnd(head, 5);
        while (listNode != null){
            System.out.print(listNode.val);
            listNode = listNode.next;
        }
    }
}
