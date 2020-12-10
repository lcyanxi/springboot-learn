package com.lcyanxi.designPatterns.spi;

import com.lcyanxi.service.IDubboSpiService;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.ExtensionLoader;

/**
 * @author lichang
 * @date 2020/12/10
 */
@Slf4j
public class DubboSpiDemo {
    public static void main(String[] args) {
        ExtensionLoader<IDubboSpiService> extensionLoader = ExtensionLoader.getExtensionLoader(IDubboSpiService.class);

        log.info("dubboSpi enable support extension");
        // 可以支持的实现
        Set<String> supportedExtensions = extensionLoader.getSupportedExtensions();
        supportedExtensions.forEach(((s -> extensionLoader.getExtension(s).dubboSpiRegisterName())));

        log.info("dubboSpi default extension");
        // 默认的实现
        IDubboSpiService defaultExtension = extensionLoader.getDefaultExtension();
        defaultExtension.dubboSpiRegisterName();

        log.info("dubboSpi find by  extension name");
        // 获取指定实现
        IDubboSpiService otherServiceImpl = extensionLoader.getExtension("dubboSpiOtherServiceImpl");
        otherServiceImpl.dubboSpiRegisterName();

    }
}
