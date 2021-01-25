package com.lcyanxi.serviceImpl;

import com.lcyanxi.service.ISalaryCalService;

/**
 * @author lichang
 * @date 2021/1/25
 */
public class SalaryCalServiceImpl implements ISalaryCalService {

    @Override
    public Double cal(Double money) {
        System.out.println("Original Service");
        return money;
    }
}
