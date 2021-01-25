package com.lcyanxi.jvm.classLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureClassLoader;


/**
 * 加载文件系统中的class文件
 * @author lichang
 * @date 2021/1/25
 */
public class SalaryClassLoader extends SecureClassLoader {
    private String libPath;
    public SalaryClassLoader(String libPath) {
        this.libPath = libPath;
    }
    @Override
    protected Class<?> findClass(String fullClassName) throws ClassNotFoundException {
        String classFilepath = this.getFileName(fullClassName);
        File file = new File(libPath,classFilepath);
        try {
            System.out.println("重新加载类："+file.getPath());
            FileInputStream is = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len = 0;
            try {
                while ((len = is.read()) != -1) {
                    bos.write(len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] data = bos.toByteArray();
            is.close();
            bos.close();
            //重新加载类，老的类会等待GC
            return defineClass(fullClassName,data,0,data.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 自己定义类不成功，就交由父类去加载
        return super.findClass(fullClassName);
    }

    // 获取要加载 的class文件名
    private String getFileName(String name) {
        // TODO Auto-generated method stub
        int index = name.lastIndexOf('.');
        if(index == -1){
            return name+".class";
        }else{
            return name.substring(index+1)+".class";
        }
    }
}
