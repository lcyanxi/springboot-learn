package com.lcyanxi.serviceImpl.finish.section.impl;

import com.google.common.collect.Lists;
import com.lcyanxi.enums.FinishSectionEnum;
import com.lcyanxi.finish.FinishPageContext;
import com.lcyanxi.finish.ISection;
import com.lcyanxi.finish.SectionType;
import com.lcyanxi.finish.SectionWithItems;
import com.lcyanxi.finish.section.RunDataSectionDto;
import com.lcyanxi.serviceImpl.finish.section.AbstractFinishSectionHandlerService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/12/6:19 下午
 */
@Service
public class FinishDataServiceImpl extends AbstractFinishSectionHandlerService<ISection> {
    @Override
    protected boolean canCreate(FinishPageContext context) {
        return false;
    }

    @Override
    protected ISection doCreate(FinishPageContext context) {
        SectionWithItems<RunDataSectionDto> withItems = new SectionWithItems<>();
        List<RunDataSectionDto> list = Lists.newArrayList();
        list.add(RunDataSectionDto.builder().duration(100).distance(20).build());
        list.add(RunDataSectionDto.builder().duration(90).distance(30).build());
        withItems.setItems(list);
        return withItems;
    }

    @Override
    public SectionType getSectionType() {
        return FinishSectionEnum.RUN_DATA;
    }
}
