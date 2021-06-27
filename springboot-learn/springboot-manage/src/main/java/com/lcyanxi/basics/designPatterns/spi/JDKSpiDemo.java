package com.lcyanxi.basics.designPatterns.spi;

import com.lcyanxi.service.IJDKSpiService;
import java.util.ServiceLoader;

/**
 * JDK SPI 模拟
 * 原理：就是约定一个目录，根据接口名去那个目录找到文件，文件解析得到实现类的全限定名，然后循环加载实现类和创建其实例。
 * 缺点：会把所有的实现类都加载出来，即使你不用
 * @author lichang
 * @date 2020/12/10
 */
public class JDKSpiDemo {
    public static void main(String[] args) {
        ServiceLoader<IJDKSpiService> load = ServiceLoader.load(IJDKSpiService.class);
        for (IJDKSpiService next : load) {
            next.spiRegisterName();
        }
    }
}
