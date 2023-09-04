package com.lcyanxi.basics.algorithm.zskipList;

import lombok.Data;

import java.util.Random;

/**
 * @author : lichang
 * @desc : 跳表实现
 * @since : 2023/09/02/5:18 下午
 */
public class ZSkipList<T extends Comparable> {
    /**
     * 跳表最大层数
     */
    private static final int MAX_LEVEL = 32;

    /**
     * 层级随机概率 P
     */
    private static final double P = 0.25;

    /**
     * 头节点 / 尾节点
     */
    private final SkipListNode<T> head;
    private final SkipListNode<T> tail;

    /**
     * 链表长度
     */
    private int length;
    /**
     * 链表层高
     */
    private int level;

    public ZSkipList() {
        head = creatNode(MAX_LEVEL);
        tail = creatNode();
        for (int i = 0; i < MAX_LEVEL; ++i) {
            head.levels[i] = new SkipListLevel();
            head.levels[i].forward = tail;
            head.levels[i].span = 1;
        }
        tail.backward = head;
        level = 1;
    }

    /**
     * 节点插入
     */
    public void insertNode(T t) {
        // 获取头节点
        SkipListNode p = this.head;
        // 从上至下 每一层需要更新的节点
        SkipListNode[] update = new SkipListNode[MAX_LEVEL];
        // 从上至下 每一层经历的距离
        int[] rank = new int[MAX_LEVEL + 1];
        // 找到该节点要插入的位置
        for (int i = level - 1; i >= 0; i--) {
            rank[i] = rank[i + 1];
            while (p.levels[i].forward != tail && t.compareTo(p.levels[i].forward.val) >= 0) {
                p = p.levels[i].forward;
                rank[i] += p.levels[i].span;
            }
            update[i] = p;
        }
        // 创建新节点
        int newLevel = randomLevel();
        SkipListNode newNode = creatNode(t, newLevel);
        p.levels[0].forward.backward = newNode;
        newNode.backward = p;
        // 分配的 level 大于链表最高层数
        if (newLevel > level) {
            for (int i = level; i < newLevel; i++) {
                update[i] = head;
            }
            level = newLevel;
        }
        for (int i = 0; i < newLevel; i++) {
            // 修改每一层抽入位置的前一个和插入节点的 forward
            newNode.levels[i] = new SkipListLevel();
            newNode.levels[i].forward = update[i].levels[i].forward;
            update[i].levels[i].forward = newNode;

            // 修改每一层抽入位置的前一个和插入节点的 span
            newNode.levels[i].span = update[i].levels[i].span - (rank[0] - rank[i]);
            update[i].levels[i].span = rank[0] + 1 - rank[i];
        }

        for (int i = newLevel; i < level; i++) {
            ++update[i].levels[i].span;
        }
        ++this.length;
    }


    private int randomLevel() {
        Random random = new Random();
        int level = 1;
        while (random.nextDouble() < P) {
            level++;
        }
        return Math.min(level, MAX_LEVEL);
    }

    public SkipListNode<T> creatNode() {
        return new SkipListNode<>();
    }

    public SkipListNode<T> creatNode(int level) {
        return new SkipListNode<>(level);
    }

    public SkipListNode<T> creatNode(T t, int level) {
        return new SkipListNode<>(t, level);
    }

    @Data
    static class SkipListNode<T> {
        /**
         * 后指针
         */
        private SkipListNode backward;
        /**
         * 节点保存的值
         */
        private T val;
        /**
         * 层级信息
         */
        private SkipListLevel levels[];

        public SkipListNode(T val, SkipListLevel[] levels) {
            this.val = val;
            this.levels = levels;
        }

        public SkipListNode(SkipListLevel[] levels) {
            this.levels = levels;
        }

        public SkipListNode() {}
    }
    @Data
    static class SkipListLevel {
        /**
         * 前指针
         */
        SkipListNode forward;
        /**
         * 距离下个节点的距离
         */
        int span;
    }

}
