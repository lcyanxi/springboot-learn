package com.lcyanxi.offer.linkedList;

/**
 * 给定一个链表的头节点  head ，返回链表开始入环的第一个节点。 如果链表无环，则返回 null。
 *
 * 如果链表中有某个节点，可以通过连续跟踪 next 指针再次到达，则链表中存在环。 为了表示给定链表中的环，评测系统内部使用整数 pos 来表示链表尾连接到链表中的位置（索引从 0 开始）。如果 pos 是 -1，则在该链表中没有环。注意：pos 不作为参数进行传递，仅仅是为了标识链表的实际情况。
 *
 * 不允许修改 链表。
 * 示例 1：
 * 输入：head = [3,2,0,-4], pos = 1
 * 输出：返回索引为 1 的链表节点
 * 解释：链表中有一个环，其尾部连接到第二个节点。
 * @author chang.li
 * @date 2025/10/22
 * @version 1.0
 */
public class No142 {
    public static void main(String[] args) {
        int[] arr = {-21, 10, 17, 8, 4, 26, 5, 35, 33, -7, -16, 27, -12, 6, 29, -12, 5, 9, 20, 14, 14, 2, 13, -24, 21, 23, -21, 5};
        Node node = NodeUtil.buildNode(arr);
        Node process = process(node);
        if (process != null){
            System.out.println(process.data);
        }
    }

    /**
     * 快慢指针： 快指针走两步 慢指针走一步 如果相遇说明有环
     * 相遇之后：慢指针回到起点， 两个指针一起一步一步走 相遇的点就是入口
     */
    private static Node process(Node head) {
        if (head == null) {
            return head;
        }
        Node slow = head;
        Node fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                Node newCur = head;
                while (newCur != fast) {
                    newCur = newCur.next;
                    fast = fast.next;
                }
                return newCur;
            }
        }
        return null;
    }

}
