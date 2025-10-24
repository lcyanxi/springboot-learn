package com.lcyanxi.offer.stack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * 请根据每日 气温 列表，重新生成一个列表。对应位置的输出为：要想观测到更高的气温，至少需要等待的天数。如果气温在这之后都不会升高，请在该位置用 0 来代替。
 *
 * 例如，给定一个列表 temperatures = [73, 74, 75, 71, 69, 72, 76, 73]，你的输出应该是 [1, 1, 4, 2, 1, 1, 0, 0]。
 *
 * 提示：气温 列表长度的范围是 [1, 30000]。每个气温的值的均为华氏度，都是在 [30, 100] 范围内的整数。
 * @author chang.li
 * @date 2025/10/24
 * @version 1.0
 */
public class No739 {
    public static void main(String[] args) {
        int[] arr = {73, 74, 75, 71, 69, 72, 76, 73};
        System.out.println(process(arr));
        System.out.println(process2(arr));
    }

    private static List<Integer> process2(int[] arr) {
        int[] result = new int[arr.length];
        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[stack.peek()]) {
                while (!stack.isEmpty() && arr[stack.peek()] < arr[i]) {
                    Integer pop = stack.pop();
                    result[pop] = i-pop;
                }
                stack.push(i);
            } else {
                stack.push(i);
            }
        }
        return Arrays.stream(result).boxed().collect(Collectors.toList());
    }

    /**
     * 暴力解法：n^2
     */
    private static List<Integer> process(int[] arr) {
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            int length = 0;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] > arr[i]) {
                    length = j - i;
                    break;
                }
            }
            res.add(length);
        }
        return res;
    }

}
