package com.lcyanxi.serviceImpl.finish;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lcyanxi.finish.TrainingType;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/10/11:24 上午
 */
@Service
public class FinishPageServiceFactory {
    @Resource
    private List<FinishPageService> finishPageServiceList;
    private Map<TrainingType, FinishPageService> finishPageServiceMap;

    @PostConstruct
    public void init() {
        finishPageServiceMap = finishPageServiceList.stream()
                .collect(Collectors.toMap(FinishPageService::getTrainingType, Function.identity()));
    }

    public FinishPageService getPageServiceByTrainingType(TrainingType trainingType) {
        return finishPageServiceMap.get(trainingType);
    }
}
