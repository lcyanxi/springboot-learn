package com.lcyanxi.serviceImpl.finish;

import com.lcyanxi.finish.FinishPageReq;
import com.lcyanxi.finish.FinishPageRsp;
import com.lcyanxi.finish.TrainingType;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/10/11:27 上午
 */
public interface FinishPageService {
    TrainingType getTrainingType();

    FinishPageRsp getLogPage(FinishPageReq request);
}
