package com.lcyanxi.offer.linkedList;

/**
 * 给你一个链表，删除链表的倒数第 n 个结点，并且返回链表的头结点。
 *
 * 进阶：你能尝试使用一趟扫描实现吗？
 * 输入：head = [1,2,3,4,5], n = 2 输出：[1,2,3,5]
 *
 * 示例 2：
 *
 * 输入：head = [1], n = 1 输出：[]
 *
 * 示例 3：
 *
 * 输入：head = [1,2], n = 1 输出：[1]
 * @author chang.li
 * @date 2025/10/16
 * @version 1.0
 */
public class No19 {
    public static void main(String[] args) {
        Node node = NodeUtil.buildNode(new int[]{1,2,3,4,5});
        Node node1 = process2(node, 2);
        while (node1 != null) {
            System.out.print(node1.data);
            node1 = node1.next;
        }
    }

    /**
     * 双指针： fast 快指针 和 slow 慢指针
     */
    private static Node process2(Node head, int n) {
        Node dummyNode = new Node(-1, null);
        dummyNode.next = head;
        Node slow = dummyNode;
        Node fast = dummyNode;
        while (n > 0) {
            fast = fast.next;
            n--;
        }
        while (fast.next != null) {
            fast = fast.next;
            slow = slow.next;
        }
        if (slow.next != null) {
            slow.next = slow.next.next;
        }
        return dummyNode.next;
    }


    private static Node process(Node head, int n) {
        int length = 0;
        Node cur = head;
        while (cur != null) {
            length++;
            cur = cur.next;
        }
        Node dummyNode = new Node(-1, null);
        dummyNode.next = head;
        cur = dummyNode;
        int loop = length - n;
        while (loop > 0) {
            cur = cur.next;
            loop--;
        }
        if (cur.next != null) {
            cur.next = cur.next.next;
        }
        return dummyNode.next;
    }
}
