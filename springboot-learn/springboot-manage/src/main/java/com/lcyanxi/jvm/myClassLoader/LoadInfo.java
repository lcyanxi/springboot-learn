package com.lcyanxi.jvm.myClassLoader;

import com.lcyanxi.service.ICountService;
import lombok.Data;

/**
 * 封装加载类的信息
 * @author lichang
 * @date 2020/12/22
 */
@Data
public class LoadInfo {
    /** 自定义的类加载器 */
    private MyClassLoader myClassLoader;

    /** 记录要加载的类的时间戳-->加载的时间 */
    private long loadTime;

    /** 需要被热加载的类 */
    private ICountService manager;

    public LoadInfo(MyClassLoader myClassLoader, long loadTime) {
        this.myClassLoader = myClassLoader;
        this.loadTime = loadTime;
        }
}
