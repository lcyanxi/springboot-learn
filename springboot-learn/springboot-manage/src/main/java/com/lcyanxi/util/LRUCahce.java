package com.lcyanxi.util;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * @author lichang
 * @date 2020/9/17
 */
public class LRUCahce {
    private Node head;
    private Node tail;
    private int limit;
    Map<Object,Node> map = new HashMap<>();

    public LRUCahce(int limit) {
        this.limit = limit;
    }

    public void put(Object key,Object value){
        Node node = map.get(key);
        if (node == null){
            if (map.size() >= limit){
                map.remove(head.getKey());
                head = head.getNext();
            }
            Node newNode = new Node(key,value);
            if (tail == null &&  head == null){
                head = newNode;
                tail = newNode;
            }else {
                tail.setNext(newNode);
                newNode.setPre(tail);
                node.setNext(null);
                tail = newNode;
            }
            map.put(key,newNode);
        }else {
            if (node == tail){
                return;
            }else if (node == head){
                head = head.getNext();
            }else {
                node.getPre().setNext(node.getNext());
                node.getNext().setPre(node.getPre());
            }

            tail.setNext(node);
            node.setPre(tail);
            node.setNext(null);
            tail = node;
        }
    }

    public Object get(Object key){
        Node node = map.get(key);
        if (node == null){
            return null;
        }
        if (node == tail){
            return node.getValue();
        }
        if (node == head){
            head = head.getNext();
        }else {
            node.getPre().setNext(node.getNext());
            node.getNext().setPre(node.getPre());
        }
        tail.setNext(node);
        node.setPre(tail);
        node.setNext(null);
        tail = node;
        return node.getValue();

    }
}



@Data
class Node{
    private Node pre;
    private Node next;
    private Object key;
    private Object value;

    public Node(Object key,Object value) {
        this.key = key;
        this.value = value;
    }
}