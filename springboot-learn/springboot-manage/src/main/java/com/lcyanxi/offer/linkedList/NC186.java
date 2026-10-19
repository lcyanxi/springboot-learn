package com.lcyanxi.offer.linkedList;

/**
 * NC186 两两交换链表的节点
 * 给你一个链表，请你两两交换相邻节点，你需要真正交换节点本身，而不是修改节点的值。
 * <p>
 * 两两交换示例：
 * 链表    ：1->2->3->4
 * 交换后 ：2->1->4->3
 */
public class NC186 {
    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4};
        Node node = NodeUtil.buildNode(nums);
        Node node1 = swapLinkedPair(node);
        Node cur = node1;
        while (cur != null) {
            System.out.print(cur.data);
            cur = cur.next;
        }
    }

    public static Node swapLinkedPair(Node head) {
        Node dumyNode = new Node();
        dumyNode.next = head;
        Node cur = dumyNode;
        while (cur.next != null && cur.next.next != null) {
            Node temp1 = cur.next;
            Node temp2 = cur.next.next.next;

            cur.next = cur.next.next;
            cur.next.next = temp1;
            temp1.next = temp2;
            cur = cur.next.next;
        }
        return dumyNode.next;
    }
}
