package com.lcyanxi.basics.jvm.gcMonitor;

import java.util.ArrayList;

/**
 * @author lichang
 * @date 2021/2/6
 * java -XX:+PrintFlagsInitial  查看所有JVM启动的初始值
 * java -XX:+PrintFlagsFinal 查看所有JVM最终值
 * java -XX:+PrintFlagsCommandLineFlags 查看那些被用户或者JVM设置过的详细XX参数的名称和值
 */
public class JinfoDemo {
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
