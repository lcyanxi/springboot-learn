package com.lcyanxi.service;

import com.lcyanxi.constant.DubboSpiDefault;
import org.apache.dubbo.common.extension.SPI;

/**
 * @author lichang
 * @date 2020/12/10
 */
@SPI(DubboSpiDefault.NAME)
public interface IDubboSpiService {

    void dubboSpiRegisterName();
}
