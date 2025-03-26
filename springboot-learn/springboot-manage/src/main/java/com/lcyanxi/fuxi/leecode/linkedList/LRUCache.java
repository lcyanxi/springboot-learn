package com.lcyanxi.fuxi.leecode.linkedList;

import java.util.HashMap;
import java.util.Map;

/**
 * 146. LRU 缓存
 * <p>
 * 请你设计并实现一个满足  LRU (最近最少使用) 缓存 约束的数据结构。
 * 实现 LRUCache 类：
 * LRUCache(int capacity) 以 正整数 作为容量 capacity 初始化 LRU 缓存
 * int get(int key) 如果关键字 key 存在于缓存中，则返回关键字的值，否则返回 -1 。
 * void put(int key, int value) 如果关键字 key 已经存在，则变更其数据值 value ；如果不存在，则向缓存中插入该组 key-value 。如果插入操作导致关键字数量超过 capacity ，则应该 逐出 最久未使用的关键字。
 * 函数 get 和 put 必须以 O(1) 的平均时间复杂度运行。
 * <p>
 *
 * ["LRUCache", "put", "put", "get", "put", "get", "put", "get", "get", "get"]
 * [[2], [1, 1], [2, 2], [1], [3, 3], [2], [4, 4], [1], [3], [4]]
 * 输出
 * [null, null, null, 1, null, -1, null, -1, 3, 4]
 * 2 3 5 tail
 */
public class LRUCache {
    private int capacity;
    private Map<Integer, DoubleListNode> map = new HashMap<>();
    private DoubleListNode head;
    private DoubleListNode tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        head = new DoubleListNode(-1, -1);
        tail = new DoubleListNode(-1, -1);
        head.next = tail;
        tail.pre = head;
    }


    public int get(int key) {
        if (!map.containsKey(key)) {
            return -1;
        }
        DoubleListNode doubleListNode = map.get(key);
        // 删除
        remove(doubleListNode);
        // 插入
        insert(doubleListNode);
        return doubleListNode.value;
    }

    public void put(int key, int value) {
        DoubleListNode newNode = new DoubleListNode(key,value);
        if (map.containsKey(key)){
            DoubleListNode doubleListNode = map.get(key);
            remove(doubleListNode);
            doubleListNode.value = value;
            insert(doubleListNode);
            map.put(key,newNode);
        }else {
            if (capacity <= map.size()) {
                DoubleListNode next = tail.pre;
                map.remove(next.key);
                remove(next);
                insert(newNode);
                map.put(key, newNode);
            } else {
                map.put(key, newNode);
                insert(newNode);
            }
        }
    }

    private void remove(DoubleListNode doubleListNode) {
        doubleListNode.pre.next = doubleListNode.next;
        doubleListNode.next.pre = doubleListNode.pre;
    }

    private void insert(DoubleListNode doubleListNode) {
        doubleListNode.next = head.next;
        head.next.pre = doubleListNode;
        head.next = doubleListNode;
        doubleListNode.pre = head;
    }

    public class DoubleListNode {
        int key;

        int value;

        DoubleListNode pre;
        DoubleListNode next;

        public DoubleListNode(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }


    public static void main(String[] args) {
        LRUCache lruCache = new LRUCache(3);

        lruCache.put(1,1);
        lruCache.put(2,2);
        lruCache.put(3,3);

        lruCache.put(4,4);
        lruCache.get(2);

        DoubleListNode head1 = lruCache.head;
        while (head1 != null){
            System.out.print(head1.value);
            head1 = head1.next;
        }

    }

}
