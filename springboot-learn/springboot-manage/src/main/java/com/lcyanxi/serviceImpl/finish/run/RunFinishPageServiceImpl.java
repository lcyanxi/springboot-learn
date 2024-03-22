package com.lcyanxi.serviceImpl.finish.run;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.lcyanxi.enums.FinishSectionEnum;
import com.lcyanxi.finish.FinishPageContext;
import com.lcyanxi.finish.FinishPageReq;
import com.lcyanxi.finish.SectionType;
import com.lcyanxi.finish.TrainingType;
import com.lcyanxi.model.User;
import com.lcyanxi.serviceImpl.finish.AbstractFinishPageService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/12/6:48 下午
 */
@Slf4j
@Service
public class RunFinishPageServiceImpl extends AbstractFinishPageService<FinishPageContext, User> {

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
        return TrainingType.Run;
    }

    @Override
    protected User buildBasicInfo(FinishPageContext context) {
        return User.builder().userId(1234123).userName("kangkang").password("1234").build();
    }
}
