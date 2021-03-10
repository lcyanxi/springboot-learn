package com.lcyanxi.jvm.gcMonitor.jconsole;

import java.util.ArrayList;
import java.util.Random;

/**
 * cmd : jconsole命令查看
 * -Xms600m -Xmx600m -XX:SurvivorRatio=8
 * @author lichang
 * @date 2021/2/7
 */
public class HeapInstanceTest {

    byte[] buffer = new byte[new Random().nextInt(1024 * 100)];

    public static void main(String[] args) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<HeapInstanceTest> list = new ArrayList<HeapInstanceTest>();
        while (true) {
            list.add(new HeapInstanceTest());
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
