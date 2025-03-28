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
        return -1;
    }

    public void put(int key, int value) {
        if (dataMap.containsKey(key)){
            Node node = dataMap.get(key);
            node.value = value;
            // 增加频率
            addFreq(node);
        }else {
            Node node = new Node(key,value,1);
            if (dataMap.size() > this.capacity){
              // 删除最小频率
                delMinFreq();
            }
            dataMap.put(key,node);
            // 插入频率
            insert(node);
            minFreq = 1;
        }
    }

    private void delMinFreq(){
        Node[] nodes = frequencyMap.get(minFreq);
        // 删除尾节点

    }

    private void addFreq(Node node){
        // 删除频率
        removeFreq(node);
        // 新增频率
        insert(node);

    }

    private void removeFreq(Node node){

    }

    private void insert(Node node){

    }

}
