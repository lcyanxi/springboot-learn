package com.lcyanxi.offer.linkedList;

/**
 * 给你一个链表，删除链表的倒数第 n 个结点，并且返回链表的头结点。
 * <p>
 * 进阶：你能尝试使用一趟扫描实现吗？
 * 输入：head = [1,2,3,4,5], n = 2 输出：[1,2,3,5]
 * <p>
 * 示例 2：
 * <p>
 * 输入：head = [1], n = 1 输出：[]
 * <p>
 * 示例 3：
 * <p>
 * 输入：head = [1,2], n = 1 输出：[1]
 *
 * @author chang.li
 * @version 1.0
 * @date 2025/10/16
 */
public class No19 {
    public static void main(String[] args) {
        Node node = NodeUtil.buildNode(new int[]{1, 2, 3, 4, 5});
        Node node1 = process(node, 10);
        while (node1 != null) {
            System.out.print(node1.data);
            node1 = node1.next;
        }
    }

    private static Node process(Node head, int n) {
        Node dumyNode = new Node();
        dumyNode.next = head;
        Node fast = dumyNode;
        Node slow = dumyNode;
        for (int i = 0; i <= n; i++) {
            fast = fast.next;
        }
        while (fast != null) {
            fast = fast.next;
            slow = slow.next;
        }
        if (slow.next != null) {
            slow.next = slow.next.next;
        }
        return dumyNode.next;
    }
}
