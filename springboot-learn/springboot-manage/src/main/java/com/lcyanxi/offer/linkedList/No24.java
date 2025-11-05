package com.lcyanxi.offer.linkedList;

/**
 * 两两交换链表中的节点
 * 给你一个链表，两两交换其中相邻的节点，并返回交换后链表的头节点。你必须在不修改节点内部的值的情况下完成本题（即，只能进行节点交换）。
 * 示例 1：
 * 输入：head = [1,2,3,4]
 * 输出：[2,1,4,3]
 * 示例 2：
 *
 * 输入：head = []
 * 输出：[]
 * 示例 3：
 *
 * 输入：head = [1]
 * 输出：[1]
 * @author chang.li
 * @date 2025/11/5
 * @version 1.0
 */
public class No24 {
    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4};
        Node node = NodeUtil.buildNode(arr);
        Node process = process(node);
        while (process != null) {
            System.out.print(process.data);
            process = process.next;
        }
    }

    /**
     * 1,2,3,4
     */
    private static Node process(Node head) {
        Node dummy = new Node();
        dummy.next = head;
        Node cur = dummy;
        while (cur.next != null && cur.next.next != null) {
            Node next1 = cur.next;
            Node next2 = cur.next.next;
            Node next3 = cur.next.next.next;
            cur.next = next2;
            next2.next = next1;
            next1.next = next3;
            cur = cur.next.next;
        }
        return dummy.next;
    }

}
