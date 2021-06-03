package com.lcyanxi.demo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lcyanxi on 2021/5/26
 * 不重复的最长子串
 */

public class StrSubStrDemo {
    public static void main(String[] args) {

    }

    private String process(String str){
        StringBuilder builder = new StringBuilder();
        Integer maxLength = 0;
        Map<Character,Integer> indexMap = new HashMap<>();
        for (int i =0;i < str.length() - 1; i++){
            if (indexMap.containsKey(str.charAt(i))){
                int index = i - indexMap.get(str.charAt(i));
            }else {
                maxLength ++;
            }
        }

        return null;
    }
}
