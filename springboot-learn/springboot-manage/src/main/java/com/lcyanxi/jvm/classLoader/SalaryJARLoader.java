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
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.startsWith("com.lcyanxi")){
            return this.findClass(name);
        }else {
            return super.loadClass(name);
        }
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
        }
        // 自己定义类不成功，就交由父类去加载
        return super.findClass(name);
    }

}
