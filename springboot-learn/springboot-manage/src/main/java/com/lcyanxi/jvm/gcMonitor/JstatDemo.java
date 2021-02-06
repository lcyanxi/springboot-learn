package com.lcyanxi.jvm.gcMonitor;

import java.util.ArrayList;

/**
 * @author lichang
 * @date 2021/2/6
 * -Xms60m -Xmx60m -XX:SurvivorRatio=8
 */
public class JstatDemo {
    public static void main(String[] args) {
        ArrayList<byte[]> list = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            byte[] arr = new byte[1024 * 100];//100KB
            list.add(arr);
            try {
                Thread.sleep(120);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
