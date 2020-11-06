package com.lcyanxi.util;

/**
 * @author lichang
 * @date 2020/9/19
 */
public class DoubleCheckSingleton {
    private static volatile DoubleCheckSingleton singleton;

    public DoubleCheckSingleton() {}

    public static  DoubleCheckSingleton  getInstance(){
        if (singleton == null){
            synchronized (DoubleCheckSingleton.class){
                if (singleton == null){
                    /*
                    memory = allocate(); 1:分配对象的内存空间
                    init(memory); 2.初始化对象
                    instance = memory; 3.设置instance指向刚分配的内存地址
                     */
                    singleton  = new DoubleCheckSingleton();
                }
            }
        }
        return singleton;
    }

    public static void main(String[] args) {
        for (int i = 0 ; i < 10000;i++){
            Thread thread = new Thread(()->{
                DoubleCheckSingleton instance = getInstance();
                System.out.println(instance);
            });
            thread.start();
        }
    }
}
