package com.lcyanxi.jvm.classLoader;

import com.lcyanxi.service.ISalaryCalService;
import com.lcyanxi.service.SalaryCaler;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author lichang
 * @date 2021/1/25
 */
public class SalaryCalDemo {
    // jar 文件地址
    private static  final String localClassPath = "//Users/koolearn/Documents/ideaData/springboot-learn/springboot-learn/springboot-api/target/springboot-api-0.0.1-SNAPSHOT.jar";
    private static  final String classPath = "//Users/koolearn/Documents/ideaData/springboot-study/springboot-api/target/springboot-api-0.0.1-SNAPSHOT.jar";

    // 薪资计算类
    private static final String className = "com.lcyanxi.service.SalaryCaler";

    // 薪资计算方法
    private static final String classMethod = "cal";


    public static void main(String[] args) throws Exception {
        Double salary = 15000.00;
        Double money ;
        while(true) {
            System.out.println("原本的Money:" + calSalary(salary, localClassPath));
            System.out.println("实际到手Money:" + calSalary(salary,classPath));
            Thread.sleep(5000);
        }

    }

    //计算薪水
    private static Double calSalary1(Double salary) throws Exception {
        /*
         * 运行时加载外部jar。加载一次就不能再更新。jar包删掉也不行。
         * 运行时加载。不能热更新
         */
        URL url = new URL(classPath);
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[] {url});
        Class<?> objClass = urlClassLoader.loadClass(className);
        Object obj = objClass.newInstance();
        return (Double)objClass.getMethod(classMethod, Double.class).invoke(obj, salary);
    }

    // 计算薪水
    private static Double calSalary2(Double salary) throws Exception {
        System.out.println("****************");
        /*
         *  运行时加载文件系统中的class文件。每次运行都重新加载
         */
//         SalaryClassLoader classloader = new SalaryClassLoader(classPath);
//         Class<?> objClass = classloader.loadClass(className);
//         Object obj = objClass.newInstance();
//         return (Double)objClass.getMethod(classMethod, Double.class).invoke(obj, salary);

        /*
         *  运行时加载jar包中的class文件。实现热加载。
         */
        SalaryJARLoader classloader = new SalaryJARLoader(classPath);
        Class<?> objClass = classloader.loadClass(className);
        Object obj = objClass.newInstance();
        return (Double) objClass.getMethod(classMethod, Double.class).invoke(obj, salary);
    }


    // 多版本共存
    private static Double calSalary3(Double salary) throws Exception {
        /*
         *  运行时加载jar包中的class文件。实现热加载。
         */
        SalaryJARLoader classloader = new SalaryJARLoader(classPath);
        Class<?> objClass = classloader.loadClass(className);
        /**
         * com.lcyanxi.service.SalaryCaler cannot be cast to com.lcyanxi.service.SalaryCaler 报错
         */
        //SalaryCaler obj = (SalaryCaler) objClass.newInstance();
        Object obj = objClass.newInstance();
        return (Double) objClass.getMethod(classMethod, Double.class).invoke(obj, salary);
    }

    // 多版本共存
    private static Double calSalary(Double salary,String classPath) throws Exception {
        /*
         *  运行时加载jar包中的class文件。实现热加载。
         */
        SalaryJARLoader classloader = new SalaryJARLoader(classPath);
        Class<?> objClass = classloader.loadClass(className);
        /**
         * com.lcyanxi.service.SalaryCaler cannot be cast to com.lcyanxi.service.SalaryCaler 报错
         */
        //SalaryCaler obj = (SalaryCaler) objClass.newInstance();
        Object obj = objClass.newInstance();
        return (Double) objClass.getMethod(classMethod, Double.class).invoke(obj, salary);
    }

    // 添加SPI实现两套机制
    private static ISalaryCalService getOriginalService(String jarPath) throws Exception {
        // 这里一定要new一个classLoader来，不可以重复使用。
        // 因为同一个classloader不可以多次去重新加载service实现类，会报错的。
        SalaryJARLoader myclassloader = new SalaryJARLoader(jarPath);
        Iterator<ISalaryCalService> iter = ServiceLoader.load(ISalaryCalService.class, myclassloader).iterator();
        if (iter.hasNext()) {
            // 只要一个子类
            return iter.next();
        } else {
            throw new ClassNotFoundException("缺少SPI的实现类");
        }


        //上面是比较简单的用法，常用的是下面这种方法，减少上下文的切换。
//		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
//		try {
//			Thread.currentThread().setContextClassLoader(classloader);
//			Iterator<SalaryCalService> iter = ServiceLoader.load(SalaryCalService.class).iterator();
//			if(iter.hasNext()) {
//				//只要一个子类
//				return iter.next();
//			}else {
//				throw new ClassNotFoundException("缺少SPI的实现类");
//			}
//		}finally {
//			Thread.currentThread().setContextClassLoader(classloader);
//		}

    }
}
