package com.lcyanxi.springbootmanage;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


/**
 * @author lichang
 * Date: 2021/09/23/2:53 下午
 */
@Slf4j
public class DemoTest {

    public static void main(String[] args) {

        ThreadFactory threadFactory =
                new ThreadFactoryBuilder().setNameFormat("DemoTest-%d").build();
        ExecutorService executorService = new ThreadPoolExecutor(2 * Runtime.getRuntime().availableProcessors() + 1,
                2 * Runtime.getRuntime().availableProcessors() + 1, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue(100),
                threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());

        List<String> SECTION_TYPE_LIST = Lists.newArrayList("aa","bb","cc");
        Map<String, Future<String>> sectionFutureMap = new HashMap<>();
        for (String sectionType : SECTION_TYPE_LIST) {
            Future<String> sectionFuture = executorService.submit(() -> {
                 log.info("task key:{} is start",sectionType);
                if (sectionType.equals("aa")) {
                    Thread.sleep(10000);
                    log.error("Shadow route detail section service is null，section type: {}", sectionType);
                    return null;
                }

                return sectionType;
            });
            sectionFutureMap.put(sectionType, sectionFuture);
        }
        Map<String, String> sectionMap = Maps.newHashMap();
        for (Map.Entry<String, Future<String>> entry : sectionFutureMap.entrySet()) {
            String key = entry.getKey();
            Future<String> future = entry.getValue();
            try {
                String value = future.get();
                log.info("result key:{},value:{}",key,value);
                sectionMap.put(key,value);
            }catch (Exception e){
                log.error("=========");
            }
        }

        log.error("sectionMap :{}",sectionMap);
    }
}
