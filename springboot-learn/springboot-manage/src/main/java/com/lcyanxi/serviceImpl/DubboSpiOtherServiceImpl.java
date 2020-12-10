package com.lcyanxi.serviceImpl;

import com.lcyanxi.service.IDubboSpiService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lichang
 * @date 2020/12/10
 */
@Slf4j
public class DubboSpiOtherServiceImpl implements IDubboSpiService {
    @Override
    public void dubboSpiRegisterName() {
        log.info("dubboSpiOtherServiceImpl is implements method start");
    }
}
