package com.lcyanxi.serviceImpl.finish.cyc;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.lcyanxi.enums.FinishSectionEnum;
import com.lcyanxi.finish.*;
import com.lcyanxi.serviceImpl.finish.AbstractFinishPageService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/12/6:48 下午
 */
@Slf4j
@Service
public class CycFinishPageServiceImpl extends AbstractFinishPageService<FinishPageContext, BasicInfo> {

    @Override
    protected FinishPageContext buildContext(FinishPageReq req) {
        return super.buildCommonContext(req);
    }

    @Override
    protected List<SectionType> getSectionTypeList(FinishPageContext context) {
        return Lists.newArrayList(FinishSectionEnum.values());
    }

    @Override
    public TrainingType getTrainingType() {
        return TrainingType.Cyc;
    }

    @Override
    protected BasicInfo buildBasicInfo(FinishPageContext context) {
        return BasicInfo.builder().userName(TrainingType.Cyc.getDesc()).trainingType(TrainingType.Cyc.getType())
                .gender("X").userId(context.getUserId()).build();
    }
}
