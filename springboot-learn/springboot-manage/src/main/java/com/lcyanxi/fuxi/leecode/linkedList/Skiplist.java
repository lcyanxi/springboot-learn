package com.lcyanxi.fuxi.leecode.linkedList;

import java.util.Arrays;
import java.util.Random;

public class Skiplist {
    static final int MAX_LEVEL = 32;
    static final double factor = 0.25;

    public class SkipListNode {
        int val;
        // 一个节点可能存在多个层级
        SkipListNode[] forward;

        public SkipListNode(int val, int maxLevel) {
            this.val = val;
            this.forward = new SkipListNode[maxLevel];
        }
    }

    private SkipListNode head;
    private int level;
    private Random random;

    public Skiplist() {
        random = new Random();
        level = 0;
        head = new SkipListNode(-1, MAX_LEVEL);
    }

    /**
     * 查找
     */
    public boolean search(int target) {
        SkipListNode cur = head;
        for (int i = level - 1; i >= 0; i--) {
            while (cur.forward[i] != null && target < cur.forward[i].val) {
                cur = cur.forward[i];
            }
            if (cur.forward[i] != null && cur.forward[i].val == target) {
                return true;
            }
        }
        return false;
    }

    /**
     * 新增
     */
    public void add(int num) {
        // 查看 key 是否存在
        boolean search = search(num);
        if (search) {
            return;
        }
        SkipListNode[] update = new SkipListNode[MAX_LEVEL];
        Arrays.fill(update, head);
        // 找到要插入 key 的前一个节点
        SkipListNode cur = head;
        for (int i = level - 1; i >= 0; i--) {
            while (cur.forward[i] != null && num < cur.forward[i].val) {
                cur = cur.forward[i];
            }
            update[i] = cur;
        }
        // 产生随机层数
        int lv = getLevel();
        level = Math.max(lv, level);
        SkipListNode newNode = new SkipListNode(num, lv);
        for (int i = 0; i < lv; i++) {
            newNode.forward[i] = update[i].forward[i];
            update[i].forward[i] = newNode;
        }
    }

    /**
     * 删除
     */
    public boolean erase(int num) {
        // 查找当前节点是否存在
        SkipListNode cur = head;
        SkipListNode[] update = new SkipListNode[MAX_LEVEL];
        for (int i = level - 1; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].val > num) {
                cur = cur.forward[i];
            }
            update[i] = cur;
        }
        cur = cur.forward[0];
        if (cur == null || cur.val != num) {
            return false;
        }

        for (int i = 0; i < level; i++) {
            if (update[i].forward[i] != cur) {
                break;
            }
            update[i].forward[i] = cur.forward[i];
        }
        if (level > 1 && head.forward[level - 1] == null) {
            level--;
        }
        return true;
    }

    private int getLevel() {
        int level = 1;
        while (level < MAX_LEVEL && random.nextDouble() < factor) {
            level++;
        }
        return level;
    }

    public static void main(String[] args) {
        Skiplist skiplist = new Skiplist();
        skiplist.add(1);
        skiplist.add(2);
        skiplist.add(3);
        System.out.println(skiplist.search(0));   // 返回 false
        skiplist.add(4);
        System.out.println(skiplist.search(1));   // 返回 true
        System.out.println(skiplist.erase(0));    // 返回 false，0 不在跳表中
        System.out.println(skiplist.erase(1));    // 返回 true
        System.out.println(skiplist.search(1));   // 返回 false，1 已被擦除
    }
}
