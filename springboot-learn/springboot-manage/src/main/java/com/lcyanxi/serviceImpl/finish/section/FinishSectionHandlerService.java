package com.lcyanxi.serviceImpl.finish.section;

import com.lcyanxi.finish.FinishPageContext;
import com.lcyanxi.finish.ISection;
import com.lcyanxi.finish.SectionType;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/11/6:31 下午
 */
public interface FinishSectionHandlerService {
    SectionType getSectionType();

    ISection doBuildSection(FinishPageContext context);
}
