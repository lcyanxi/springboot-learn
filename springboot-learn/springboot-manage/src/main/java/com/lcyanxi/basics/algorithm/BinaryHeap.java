package com.lcyanxi.basics.algorithm;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * @author : lichang
 * @desc : 小顶堆数据结构
 * @since : 2022/05/01/9:55 下午
 */
public class BinaryHeap {
    private int[] heap;
    private int heapSize;

    public BinaryHeap(int capacity) {
        this.heapSize = 0;
        heap = new int[capacity + 1];
        Arrays.fill(heap, -1);
    }

    private boolean isFull() {
        return heapSize == heap.length;
    }

    private boolean isEmpty() {
        return heapSize == 0;
    }

    private int parentIndex(int index) {
        return (index - 1) / 2;
    }

    private int kthChild(int i, int k) {
        return i * 2 + k;
    }

    private int minChild(int i) {
        int leftChild = i * 2 + 1;
        int rightChild = i * 2 + 2;
        return heap[leftChild] < heap[rightChild] ? leftChild : rightChild;
    }

    private void siftUp(int index) {
        int insertValue = heap[index];
        while (index > 0 && insertValue < heap[parentIndex(index)]) {
            heap[index] = heap[parentIndex(index)];
            index = parentIndex(index);
        }
        heap[index] = insertValue;
    }

    private void siftDown(int index) {
        int child;
        int temp = heap[index];
        while (kthChild(index, 1) < heapSize) {
            child = minChild(index);
            if (child > temp) {
                break;
            }
            heap[index] = heap[child];
            index = child;
        }
        heap[index] = temp;
    }

    public void insert(int data) {
        if (isFull()) {
            throw new NoSuchElementException("heap is full,No space to insert new element");
        }
        heap[heapSize] = data;
        heapSize++;
        // 堆化
        siftUp(heapSize - 1);

    }

    public int delete() {
        if (isEmpty()) {
            throw new NoSuchElementException("heap is empty,No element to delete");
        }
        int index = 0;
        int minElement = heap[index];
        heap[index] = heap[heapSize - 1];
        heapSize--;
        siftDown(index);
        return minElement;
    }

    private void printHeap() {
        for (int i = 0; i < heapSize; i++) {
            System.out.print(heap[i] + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        BinaryHeap heap = new BinaryHeap(10);
        heap.insert(10);
        heap.insert(4);
        heap.insert(9);
        heap.insert(1);
        heap.insert(7);
        heap.insert(5);
        heap.insert(3);

        heap.printHeap();
        for (int i = 0; i < 7; i++) {
            System.out.print(heap.delete() + " ");
        }
        heap.printHeap();
    }
}
