package com.lcyanxi.serviceImpl.finish;

import java.util.List;

import com.lcyanxi.finish.FinishPageContext;
import com.lcyanxi.finish.FinishPageReq;
import com.lcyanxi.finish.SectionType;
import com.lcyanxi.serviceImpl.finish.section.FinishSectionHandlerService;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/11/6:21 下午
 */
public abstract class AbstractFinishPageService implements FinishPageService {


    // 由子类构造上下文参数
    protected abstract FinishPageContext buildContext(FinishPageReq req);


    protected abstract List<SectionType> getSectionTypeList(FinishPageContext context);

    protected abstract List<FinishSectionHandlerService> getSectionTypeHandlerList(FinishPageContext context);

}
