package com.lcyanxi.springbootworker;

import java.util.Random;

/**
 * @author lichang
 * @date 2020/8/13
 */
public class DemoTest {
    public static void main(String[] args) {
        Random random = new Random();
        int abs = Math.abs(random.nextInt() % 99999999) % 1;
        System.out.println(abs);
    }
}
