/*
package com.lcyanxi.serviceImpl;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.lcyanxi.service.ISalaryCalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

*/
/**
 * @author lichang
 * @date 2021/1/25
 *//*

@Slf4j
@Service
public class SalaryCalServiceImpl implements ISalaryCalService {

    @Override
    @Cached(name = "cal", key = "#money", expire = 3600, cacheType = CacheType.BOTH)
    public Double cal(Double money) {
        log.info("salaryCalServiceImpl cal money:{}", money);
        return money * 10;
    }
}
*/
