package com.lcyanxi.serviceImpl.finish.section.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.lcyanxi.enums.FinishSectionEnum;
import com.lcyanxi.finish.FinishPageContext;
import com.lcyanxi.finish.ISection;
import com.lcyanxi.finish.SectionType;
import com.lcyanxi.finish.SectionWithItems;
import com.lcyanxi.finish.section.MapSectionDto;
import com.lcyanxi.serviceImpl.finish.section.AbstractFinishSectionHandlerService;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/12/6:19 下午
 */
@Service
public class FinishMapServiceImpl extends AbstractFinishSectionHandlerService<ISection> {
    @Override
    protected boolean canCreate(FinishPageContext context) {
        return true;
    }

    @Override
    protected ISection doCreate(FinishPageContext context) {
        SectionWithItems<MapSectionDto> withItems = new SectionWithItems<>();
        List<MapSectionDto> list = Lists.newArrayList();
        list.add(MapSectionDto.builder().url("www.baidu.com").build());
        list.add(MapSectionDto.builder().url("www.github.com").build());
        withItems.setItems(list);
        return withItems;
    }

    @Override
    public SectionType getSectionType() {
        return FinishSectionEnum.MAP;
    }
}
