package com.lcyanxi.offer.stack;

import java.util.*;

/**
 * 前 K 个高频元素
 * 给定一个非空的整数数组，返回其中出现频率前 k 高的元素。
 * 输入: nums = [1,1,1,2,2,3], k = 2
 * 输出: [1,2]
 */
public class No347 {
    public static void main(String[] args) {
        int[] nums = new int[]{1, 1, 1, 2, 2, 3};
        List<Integer> process = process(nums, 2);
        System.out.println(process);

    }

    private static List<Integer> process(int[] nums, int topN) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }
        PriorityQueue<int[]> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p[1]));
        map.forEach((k, v) -> {
            int[] data = new int[]{k, v};
            if (queue.size() < topN) {
                queue.add(data);
            } else {
                int[] peek = queue.peek();
                if (peek != null && v > queue.peek()[1]) {
                    queue.poll();
                    queue.add(data);
                }
            }
        });
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < topN; i++) {
            result.add(queue.poll()[0]);
        }
        return result;
    }
}
