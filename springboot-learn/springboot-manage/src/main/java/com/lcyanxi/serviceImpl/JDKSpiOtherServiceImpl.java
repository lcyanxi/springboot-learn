package com.lcyanxi.serviceImpl;

import com.lcyanxi.service.IJDKSpiService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lichang
 * @date 2020/12/10
 */
@Slf4j
public class JDKSpiOtherServiceImpl implements IJDKSpiService {

    @Override
    public void spiRegisterName() {

        log.info("testSpiOtherServiceImpl is implements method start");
    }
}
