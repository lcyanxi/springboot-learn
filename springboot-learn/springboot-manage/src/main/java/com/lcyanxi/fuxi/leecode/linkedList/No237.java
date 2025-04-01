package com.lcyanxi.fuxi.leecode.linkedList;

/**
 * 237. 删除链表中的节点
 *
 * 输入：head = [4,5,1,9], node = 5
 * 输出：[4,1,9]
 *
 * 输入：head = [4,5,1,9], node = 1
 * 输出：[4,5,9]
 *
 * 思路： 魔法替换
 */
public class No237 {
    public void deleteNode(ListNode node) {
        node.val = node.next.val;
        node.next = node.next.next;
    }
}
