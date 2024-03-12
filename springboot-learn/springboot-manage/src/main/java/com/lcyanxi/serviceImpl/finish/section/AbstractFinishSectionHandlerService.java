package com.lcyanxi.serviceImpl.finish.section;

import com.lcyanxi.finish.FinishPageContext;
import com.lcyanxi.finish.ISection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/12/10:02 上午
 */
public abstract class AbstractFinishSectionHandlerService<T extends ISection> implements FinishSectionHandlerService {
    protected abstract boolean canCreate(FinishPageContext context);

    protected abstract T doCreate(FinishPageContext context);

    protected Map<String, String> getTracks(FinishPageContext context, ISection section) {
        Map<String, String> tracks = new HashMap<>();
        tracks.put("card_type", "test");
        return tracks;
    }

    @Override
    public T doBuildSection(FinishPageContext context) {
        boolean canCreate = canCreate(context);
        if (!canCreate) {
            return null;
        }
        T section = doCreate(context);

        return section;
    }
}
