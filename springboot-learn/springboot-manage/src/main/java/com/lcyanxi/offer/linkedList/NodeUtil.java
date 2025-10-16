package com.lcyanxi.offer.linkedList;

/**
 * @author chang.li
 * @date 2025/10/16
 * @version 1.0
 */
public class NodeUtil {
    public static Node buildNode(int[] nums) {
        Node tail = null;
        for (int i = nums.length - 1; i >= 0; i--) {
            Node node = new Node(nums[i], null);
            node.next = tail;
            tail = node;
        }
        return tail;
    }
}
