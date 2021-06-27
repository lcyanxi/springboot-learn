package com.lcyanxi.basics.jvm;

/**
 * @author lichang
 * @date 2020/9/19
 * -Xms:123M   默认情况初始内存占用物理电脑内存大小/64
 * -Xmx:1820M  默认最大堆最大内存占用物理电脑内存大小/4
 *
 */
public class HeapSpaceInitial {
    public static void main(String[] args) {
        // 返回Java虚拟机中的堆内存总量
        long initialMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        // 返回Java虚拟机试图使用的最大堆内存
        long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        System.out.println("-Xms:" + initialMemory + "M");
        System.out.println("-Xmx:" + maxMemory + "M");
//
//        try{
//            Thread.sleep(1000000);
//        }catch (Exception e){
//
//        }
    }
}
