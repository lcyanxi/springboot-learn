package com.lcyanxi.jvm;

/**
 * @author lichang
 * @date 2020/12/19
 */
public class ClassLoaderDemo {
    static {
        System.out.println("classLoaderDemo static is start");
    }
    public static void main(String[] args) throws ClassNotFoundException {

        // 获取系统类加载器
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        System.out.println("获取系统类加载器:" + systemClassLoader);

        // 获取其上层的：扩展类加载器
        ClassLoader extClassLoader = systemClassLoader.getParent();
        System.out.println("扩展类加载器:" + extClassLoader);

        // 试图获取 根加载器
        ClassLoader bootstrapClassLoader = extClassLoader.getParent();
        System.out.println("根加载器:" + bootstrapClassLoader);

        // 获取自定义加载器
        ClassLoader classLoader = ClassLoaderDemo.class.getClassLoader();
        System.out.println("获取自定义加载器" + classLoader);

        // 获取String类型的加载器
        ClassLoader classLoader1 = String.class.getClassLoader();
        System.out.println("获取String类型的加载器:" + classLoader1);

        // 数组的加载器类型与数组的类型一致，引用类型需要类加载器加载
        String[] arrStr = new String[10];
        ClassLoader classLoader2 = arrStr.getClass().getClassLoader();
        System.out.println("引用类型数组加载器:" + classLoader2);

        // 它的加载器为null  意味着它不需要加载  基本数据类型由虚拟机预先定义，
        int[] arr = new int[10];
        ClassLoader classLoader3 = arr.getClass().getClassLoader();
        System.out.println("基本数据类型加载器" + classLoader3);

    }
}
