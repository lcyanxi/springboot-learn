package com.lcyanxi.serviceImpl.finish;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lcyanxi.finish.*;
import com.lcyanxi.serviceImpl.finish.section.FinishSectionHandlerService;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/11/6:21 下午
 */
public abstract class AbstractFinishPageService<C extends FinishPageContext, D> implements FinishPageService {
    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 6, 3,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(3),
            new ThreadFactoryBuilder().setNameFormat("finish_page_%d").build());

    // 由子类构造上下文参数
    protected abstract FinishPageContext buildContext(FinishPageReq req);


    protected abstract List<SectionType> getSectionTypeList(FinishPageContext context);

    protected abstract List<FinishSectionHandlerService> getSectionTypeHandlerList(FinishPageContext context);

    // 由子类构建 meta 信息
    protected D buildBasicInfo(FinishPageContext context) {
        return null;
    }

    @Override
    public FinishPageRsp<D> getLogPage(FinishPageReq request) {
        // 构建上下文参数
        FinishPageContext context = buildContext(request);
        if (context == null) {
            return null;
        }
        // 多线程获取 sections 数据
        List<ISection> sections = fetchSection(context);
        // 拼装返回结果
        FinishPageRsp<D> view = new FinishPageRsp<>();
        view.setSections(sections);
        view.setBasicInfo(buildBasicInfo(context));
        return view;
    }

    private List<ISection> fetchSection(FinishPageContext context) {
        // 获取要那些 section 类型
        List<SectionType> sectionTypeList = getSectionTypeList(context);
        if (CollectionUtils.isEmpty(sectionTypeList)) {
            return new ArrayList<>();
        }
        // 获取每个 section 类型具体的执行器
        List<FinishSectionHandlerService> sectionTypeHandlerList = getSectionTypeHandlerList(context);
        Set<String> sectionTypes = sectionTypeList.stream().map(SectionType::getType).collect(Collectors.toSet());

        List<Future<ISection>> list = sectionTypeHandlerList.stream()
                .filter(item -> item != null && sectionTypes.contains(item.getSectionType().getType()))
                .map(processor -> threadPool.submit(() -> processor.doBuildSection(context)))
                .collect(Collectors.toList());

        List<ISection> cards = Lists.newArrayList();
        list.forEach(future -> {
            try {
                ISection iSection = future.get(1000, TimeUnit.MILLISECONDS);
                if (Objects.isNull(iSection)) {
                    return;
                }
                cards.add(iSection);
            } catch (Exception e) {}
        });
        return cards.stream().sorted(Comparator.comparing(ISection::getIndex)).collect(Collectors.toList());
    }

    protected FinishPageContext buildCommonContext(FinishPageReq request) {
        return FinishPageContext.builder().logId(request.getLogId()).userId(request.getUserId())
                .creatAt(System.currentTimeMillis()).build();
    }

    protected BasicInfo buildCommonBasicInfo(FinishPageContext context){
        return BasicInfo.builder().gender("男").userId(context.getUserId()).userName("达康书记").build();
    }
}
