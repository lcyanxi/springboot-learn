package com.lcyanxi.algorithm.string;

import com.google.common.collect.Maps;
import java.util.Map;

/**
 * 最长不含重复字符的子字符串
 * 描述：请从字符串中找出一个最长的不包含重复字符的子字符串，计算该最长子字符串的长度。
 * 输入: "abcabcbb"
 * 输出: 3
 * 解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。
 * @author lichang
 * @date 2020/11/24
 */
public class LengthOfLongestSubstring {
    public static void main(String[] args) {
        String str = "abcabcbb";
        System.out.println(lengthOfLongestSubstring(str));
        System.out.println(lengthOfLongestSubstring2(str));
        System.out.println(lengthOfLongestSubstring3(str));
    }
    private static int lengthOfLongestSubstring(String string){
        int length = 0;
        for (int i = 0; i < string.length(); i ++){
            char temp = string.charAt(i);
            Map<Character,Integer> map = Maps.newHashMap();
            map.put(temp,1);
            for (int j = i + 1; j < string.length(); j++){
                if(temp == string.charAt(j) || map.containsKey(string.charAt(j))){
                    break;
                }
                map.put(string.charAt(j),1);
            }
            length = Math.max(map.size(),length);
        }
        return length;
    }

    private  static  int lengthOfLongestSubstring2(String s) {
        Map<Character, Integer> dic = Maps.newHashMap();
        int res = 0, tmp = 0;
        for(int j = 0; j < s.length(); j++) {
            int i = dic.getOrDefault(s.charAt(j), -1); // 获取索引 i
            dic.put(s.charAt(j), j); // 更新哈希表
            tmp = tmp < j - i ? tmp + 1 : j - i; // dp[j - 1] -> dp[j]
            res = Math.max(res, tmp); // max(dp[j - 1], dp[j])
        }
        return res;
    }

    private static int lengthOfLongestSubstring3(String s){
        Map<Character, Integer> dic = Maps.newHashMap();
        int maxLength = 0;
        int left = 0;
        for (int i = 0; i < s.length(); i++){
            if (dic.containsKey(s.charAt(i))){
                left = Math.max(left,dic.get(s.charAt(i))+1 );
            }
            dic.put(s.charAt(i),i);
            maxLength = Math.max(maxLength,i-left +1);
        }
        return maxLength;
    }

}
