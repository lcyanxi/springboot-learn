package com.lcyanxi.util;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lichang
 * @date 2020/7/13
 */
public class TestDemo {

    public static void main(String[] args) {
        List<Integer> aa = Lists.newArrayList();
        aa.add(3);
        aa.add(12);
        aa.add(1);
        aa.add(4);
        System.out.println(aa.stream().sorted(Integer::compareTo).collect(Collectors.toList()));

        System.out.println(10 % 3);
    }

}
