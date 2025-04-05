package com.lcyanxi.fuxi.singleton;

public class DoubleSingleton {
    private static volatile DoubleSingleton instance;

    private DoubleSingleton(){

    }
    public static DoubleSingleton getInstance(){
        if (instance == null){
            synchronized (DoubleSingleton.class){
                if (instance == null){
                    // 指令重排
                    instance = new DoubleSingleton();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        DoubleSingleton doubleSingleton = new DoubleSingleton();
        System.out.println(doubleSingleton);
    }
}
