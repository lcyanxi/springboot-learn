package com.lcyanxi.basics.jvm.gcMonitor;

import java.util.ArrayList;

/**
 * -Xms60m -Xmx60m -XX:SurvivorRatio=8
 * @author lichang
 * @date 2021/2/7
 * 在发生OOM时自动导处dump文件 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/Users/Desktop/a.hprof
 */
public class JmapDemo {

    public static void main(String[] args) {
        ArrayList<byte[]> list = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            byte[] arr = new byte[1024 * 100];//100KB
            list.add(arr);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
