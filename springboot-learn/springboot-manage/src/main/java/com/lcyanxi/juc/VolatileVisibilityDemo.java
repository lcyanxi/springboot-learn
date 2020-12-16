package com.lcyanxi.juc;

/**
 * @author lichang 模拟测试JMM模型  理解volatile的可见性
 * @date 2020/9/19
 */
public class VolatileVisibilityDemo {
    private boolean flag = false;

    public static void main(String[] args) {
        VolatileVisibilityDemo demo = new VolatileVisibilityDemo();
        Thread thread1 = new Thread(demo::loadData,"thread1");

        thread1.start();;
        try{
            Thread.sleep(1000);
        }catch (Exception e){

        }
        Thread thread2 = new Thread(demo::refresh,"thread2");
        thread2.start();
    }

    public  void loadData(){
        while (!flag){

        }
        String name = Thread.currentThread().getName();
        System.out.println("线程" + name + "loadData excute end ");
    }

    public   void refresh(){
        flag = true;
        String name = Thread.currentThread().getName();
        System.out.println("线程" + name + "refresh data success");
    }
}
