package com.lcyanxi.basics.jvm.gcMonitor;

/**
 * 演示死锁问题,通过jstack查看死锁信息
 * @author lichang
 * @date 2021/2/7
 */
public class JstackThreadDeadLockDemo {

    public static void main(String[] args) {

        StringBuilder s1 = new StringBuilder();
        StringBuilder s2 = new StringBuilder();

        new Thread(() -> {
            synchronized (s1){
                s1.append("a");
                s2.append("1");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (s2){
                    s1.append("b");
                    s2.append("2");

                    System.out.println(s1);
                    System.out.println(s2);
                }
            }
        }).start();


        new Thread(() -> {
            synchronized (s2){
                s1.append("c");
                s2.append("3");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (s1){
                    s1.append("d");
                    s2.append("4");
                    System.out.println(s1);
                    System.out.println(s2);
                }
            }
        }).start();

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        new Thread(() -> {
//            Map<Thread, StackTraceElement[]> all = Thread.getAllStackTraces();//追踪当前进程中的所有的线程
//            Set<Map.Entry<Thread, StackTraceElement[]>> entries = all.entrySet();
//            for(Map.Entry<Thread, StackTraceElement[]> en : entries){
//                Thread t = en.getKey();
//                StackTraceElement[] v = en.getValue();
//                System.out.println("【Thread name is :" + t.getName() + "】");
//                for(StackTraceElement s : v){
//                    System.out.println("\t" + s.toString());
//                }
//            }
//        }).start();
    }
}
