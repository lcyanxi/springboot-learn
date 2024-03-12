package com.lcyanxi.serviceImpl.finish.run;

import com.google.common.collect.Lists;
import com.lcyanxi.enums.FinishSectionEnum;
import com.lcyanxi.finish.*;
import com.lcyanxi.serviceImpl.finish.AbstractFinishPageService;
import com.lcyanxi.serviceImpl.finish.section.FinishSectionHandlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/12/6:48 下午
 */
@Slf4j
@Service
public class RunFinishPageService extends AbstractFinishPageService {
    @Autowired
    private List<FinishSectionHandlerService> handlerServices;

    @Override
    protected FinishPageContext buildContext(FinishPageReq req) {
        return null;
    }

    @Override
    protected List<SectionType> getSectionTypeList(FinishPageContext context) {
        return Lists.newArrayList(FinishSectionEnum.values());
    }

    @Override
    protected List<FinishSectionHandlerService> getSectionTypeHandlerList(FinishPageContext context) {
        return handlerServices;
    }

    @Override
    public TrainingType getTrainingType() {
        return TrainingType.Run;
    }
}
