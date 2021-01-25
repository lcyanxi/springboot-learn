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


        // java指令可以通过增加-verbose:class -verbose:gc 参数在启动时打印出类加载情况
        // BootStrap Classloader，加载java基础类。这个属性不能在java指令中指定，推断不是由java语言处理。。
        System.out.println("BootStrap ClassLoader加载目录："+System.getProperty("sun.boot.class.path"));

        // Extention Classloader 加载JAVA_HOME/ext下的jar包。 可通过-D java.ext.dirs另行指定目录
        System.out.println("Extention ClassLoader加载目录："+System.getProperty("java.ext.dirs"));

        // AppClassLoader 加载CLASSPATH，应用下的Jar包。可通过-D java.class.path另行指定目录
        System.out.println("AppClassLoader加载目录："+System.getProperty("java.class.path"));

        // 父子关系 AppClassLoader <- ExtClassLoader <- BootStrap Classloader
        ClassLoader cl1 = ClassLoaderDemo.class.getClassLoader();
        System.out.println("cl1 > "+cl1);
        System.out.println("parent of cl1 > "+cl1.getParent());

        // BootStrap Classloader由C++开发，是JVM虚拟机的一部分，本身不是JAVA类。
        System.out.println("grant parent of cl1 > "+cl1.getParent().getParent());
        // String,Int等基础类由BootStrap Classloader加载。
        ClassLoader cl2 = String.class.getClassLoader();
        System.out.println("cl2 > "+ cl2);
        System.out.println(cl1.loadClass("java.util.List").getClassLoader());

    }
}
