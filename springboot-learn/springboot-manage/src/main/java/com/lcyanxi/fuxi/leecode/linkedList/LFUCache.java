package com.lcyanxi.fuxi.leecode.linkedList;

import java.util.HashMap;
import java.util.Map;

public class LFUCache {
    public class Node {
        int key;
        int value;
        int frequency;
        Node next;
        Node prev;

        public Node(int key, int value, int frequency) {
            this.key = key;
            this.value = value;
            this.frequency = frequency;
        }

        public Node() {
        }
    }


    private Map<Integer, Node> dataMap;
    private Map<Integer, Node[]> frequencyMap;
    private int capacity;
    private int minFreq;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        dataMap = new HashMap<>();
        frequencyMap = new HashMap<>();
        minFreq = -1;
    }


    public int get(int key) {
        if (!dataMap.containsKey(key)) {
            return -1;
        }
        Node node = dataMap.get(key);
        addFreq(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (dataMap.containsKey(key)) {
            Node node = dataMap.get(key);
            node.value = value;
            // 增加频率
            addFreq(node);
        } else {
            Node node = new Node(key, value, 1);
            if (dataMap.size() > this.capacity) {
                // 删除最小频率
                delMinFreq();
            }
            dataMap.put(key, node);
            // 插入频率
            insert(node);
            minFreq = 1;
        }
    }

    private void delMinFreq() {
        Node[] nodes = frequencyMap.get(minFreq);
        // 删除尾节点
        Node tailNode = nodes[1];
        Node headNode = nodes[0];
        Node next = headNode.next;

        next.prev.next = next.next;
        next.next.prev = next.prev;

        dataMap.remove(next.key);
        if (headNode.next == tailNode) {
            minFreq = 1;
        }

    }

    private void addFreq(Node node) {
        // 删除频率
        removeFreq(node);
        node.frequency++;
        // 新增频率
        insert(node);

    }

    private void removeFreq(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        Node[] nodes = frequencyMap.get(node.frequency);
        if (node.frequency == minFreq && nodes[0].next == nodes[1]) {
            minFreq++;
        }
    }

    private void insert(Node node) {
        Node tailNode;
        if (!frequencyMap.containsKey(node.frequency)) {
            Node head = new Node();
            Node tail = new Node();
            head.next = tail;
            tail.prev = head;
            frequencyMap.put(node.frequency, new Node[]{head, tail});
            tailNode = tail;
        } else {
            tailNode = frequencyMap.get(node.frequency)[1];
        }
        Node prev = tailNode.prev;
        prev.next = node;
        node.prev = prev;
        node.next = tailNode;
        tailNode.prev = node;
    }

    public static void main(String[] args) {
        // [[2], [1, 1], [2, 2], [1], [3, 3], [2], [3], [4, 4], [1], [3], [4]]

        LFUCache cache = new LFUCache(2);
        cache.get(2);
        cache.put(1,1);
        cache.put(2,2);
        cache.get(1);
        cache.put(3,3);
        System.out.println(cache.get(2));
        cache.get(3);
        cache.put(4,4);
        cache.get(1);
        cache.get(3);
        cache.get(4);

    }
}
