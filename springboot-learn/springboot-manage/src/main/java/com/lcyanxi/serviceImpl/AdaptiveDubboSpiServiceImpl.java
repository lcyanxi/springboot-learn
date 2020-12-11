package com.lcyanxi.serviceImpl;

import com.lcyanxi.service.IDubboSpiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.ExtensionLoader;

/**
 * @author lichang
 * @date 2020/12/11
 */
@Slf4j
@Adaptive
public class AdaptiveDubboSpiServiceImpl implements IDubboSpiService {

    private static volatile  String DEFAULT_SERVICE;

    public static void setDefaultService(String service){
        DEFAULT_SERVICE = service;
    }

    @Override
    public void dubboSpiRegisterName() {
        ExtensionLoader<IDubboSpiService> extensionLoader = ExtensionLoader.getExtensionLoader(IDubboSpiService.class);
        IDubboSpiService spiService;
        log.info("adaptiveDubboSpiServiceImpl DEFAULT_SERVICE :[{}]",DEFAULT_SERVICE);
        if (DEFAULT_SERVICE != null && DEFAULT_SERVICE.length() > 0){
            spiService = extensionLoader.getExtension(DEFAULT_SERVICE);
        }else {
            spiService = extensionLoader.getDefaultExtension();
        }
        log.info("adaptiveDubboSpiServiceImpl is implements method start");
        spiService.dubboSpiRegisterName();
    }
}
