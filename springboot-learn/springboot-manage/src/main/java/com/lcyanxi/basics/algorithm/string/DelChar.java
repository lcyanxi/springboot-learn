package com.lcyanxi.basics.algorithm.string;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 删字符：
 * 删除字符串总出现次数最少的字符
 * 若多个字符串个数一样  则都删除
 * 例如： 输入：str = "abcdd"  输出 ："dd"
 *             str = "aabbccdd" 输出 ： ""
 * @author lichang
 * @date 2020/11/17
 */
public class DelChar {
    public static void main(String[] args) {
        System.out.println(delChar("aabbdedd"));
    }
    private static String delChar(String str){
        if (null == str || str.length() == 0){
            return "";
        }
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0 ; i < str.length() ; i ++){
            char charAt = str.charAt(i);
            map.put(charAt,map.getOrDefault(charAt,0) + 1);
        }
        Collection<Integer> integers1 = map.values();
        StringBuilder builder = new StringBuilder();
        Integer min = Collections.min(integers1);
        for (int i = 0 ; i < str.length() ; i ++){
            if (!map.get(str.charAt(i)).equals(min)){
                builder.append(str.charAt(i));
            }
        }
        return builder.toString();
    }
}
