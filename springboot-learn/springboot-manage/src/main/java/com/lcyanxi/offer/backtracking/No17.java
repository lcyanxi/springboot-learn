package com.lcyanxi.offer.backtracking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 给定一个仅包含数字 2-9 的字符串，返回所有它能表示的字母组合。
 * <p>
 * 给出数字到字母的映射如下（与电话按键相同）。注意 1 不对应任何字母。
 * 示例:
 * <p>
 * 输入："23"
 * 输出：["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"].
 *
 * @author chang.li
 * @version 1.0
 * @date 2025/9/30
 */
public class No17 {

    final static Map<Integer, String> map = new HashMap<Integer, String>() {{
        put(1, "");
        put(0, "");
        put(2, "abc");
        put(3, "def");
        put(4, "ghi");
        put(5, "jkl");
        put(6, "mno");
        put(7, "pqrs");
        put(8, "tuv");
        put(9, "wxyz");
    }};


    public static void main(String[] args) {
        System.out.println(letterCombinations("2"));
    }

    public static List<String> letterCombinations(String digits) {
        List<String> res = new ArrayList<>();
        if (digits == null || digits.isEmpty()) {
            return res;
        }
        StringBuilder builder = new StringBuilder();
        backtrack(res, builder, digits, 0);
        return res;
    }

    private static void backtrack(List<String> res, StringBuilder builder, String str, int index) {
        if (index == str.length()) {
            res.add(builder.toString());
            return;
        }
        String subStr = map.get(str.charAt(index) - '0');
        for (int i = 0; i < subStr.length(); i++) {
            builder.append(subStr.charAt(i));
            backtrack(res, builder, str, index + 1);
            builder.deleteCharAt(builder.length() - 1);
        }
    }
}
