package com.lcyanxi.jvm.classLoader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureClassLoader;

/**
 * 加载jar包中的class文件
 * @author lichang
 * @date 2021/1/25
 */
public class SalaryJARLoader extends SecureClassLoader {

    private String jarPath;

    public SalaryJARLoader(String jarPath) {
        this.jarPath = jarPath;
    }


    @Override
    public Class<?> loadClass(String name,boolean resolve) throws ClassNotFoundException {
//        if (name.startsWith("com.lcyanxi")){
//            return this.findClass(name);
//        }else {
//            return super.loadClass(name);
//        }
        // 解决硬编码问题
        // 把双亲委派机制反过来，先到子类加载器中加载，加载不到再去父类加载器中加载。
        Class<?> c = null;
        synchronized (getClassLoadingLock(name)) {
            c = findLoadedClass(name);
            // 优先从本地加载
            if (c == null){
                c = findClass(name);
                if (c == null){
                    // 本地加载不到再走双亲委派机制
                    c = super.loadClass(name,resolve);
                }
            }
        }
        return c;
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String classFilepath = name.replace('.', '/').concat(".class");
        try {
            //访问jar包的url
            URL jarURL = new URL("jar:file:/" + jarPath + "!/" + classFilepath);
            System.out.println("重新加载类："+jarURL);
            InputStream is = jarURL.openStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len = 0;
            while ((len = is.read()) != -1) {
                bos.write(len);
            }
            byte[] data = bos.toByteArray();
            is.close();
            bos.close();
            return defineClass(name, data, 0, data.length);
        } catch (Exception e) {
            System.out.println("文件正在替换");
            e.printStackTrace();
        }
        // 自己定义类不成功，就交由父类去加载
        return super.findClass(name);
    }

}
