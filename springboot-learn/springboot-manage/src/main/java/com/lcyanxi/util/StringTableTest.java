package com.lcyanxi.util;

/**
 * @author lichang
 * @date 2020/11/8
 */
public class StringTableTest {
    public static void main(String[] args) {
        String s = new String("1");  // 在常量池中已经有了
        s.intern(); // 将该对象放入到常量池。但是调用此方法没有太多的区别，因为已经存在了1
        String s2 = "1";
        System.out.println(s == s2); // false

        String s3 = new String("1") + new String("1");
        s3.intern();
        String s4 = "11";
        System.out.println(s3 == s4); // true

    }
}
