package com.lcyanxi.limit.listener;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.lcyanxi.limit.constent.GuavaRateLimiterKeys;
import static com.lcyanxi.limit.constent.GuavaRateLimiterKeys.INIT_PROJECT_DEFAULT_NAMESPACE;
import com.lcyanxi.limit.dictionary.DictionaryManager;
import com.lcyanxi.limit.util.JsonUtils;
import com.lcyanxi.limit.util.LimitUtils;
import com.lcyanxi.limit.dictionary.ApolloChangeEvent;
import com.lcyanxi.limit.dto.GuavaRateLimiterFlowRuleDto;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.spring.ServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author lichang
 * @date 2020/7/9
 */
@Slf4j
@Component
public class GuavaDefaultRateLimiterListener implements ApplicationListener<ApolloChangeEvent> {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DictionaryManager dictionaryManager;

    /**
     * 特殊配置的的url限速器集合
     */
    public static volatile Map<String, RateLimiter> urlLimiterMap = Maps.newHashMap();

    /**
     * 默认接口限流规则
     */
    public static volatile Map<String, RateLimiter> defaultLimiterMap = Maps.newHashMap();

    /**
     * 核心接口限速器
     */
    public static volatile RateLimiter totalRateLimiter;

    @Override
    public void onApplicationEvent(ApolloChangeEvent event) {
        log.info("init-project event:{}*****************", JSON.toJSONString(event));
        try {
            // is init
            if (INIT_PROJECT_DEFAULT_NAMESPACE.equals(event.getNamespace())) {
                updateRateLimiter();
                log.info("onApplicationEvent defaultLimiterMap:{}",defaultLimiterMap);
                log.info("onApplicationEvent urlLimiterMap:{}",urlLimiterMap);
                return;
            }
            // is update
            boolean hasChange = event.isChanged(GuavaRateLimiterKeys.JWYTH_SYS_GUAVA_RATE_LIMITER_RULE);
            boolean totalHasChange = event.isChanged(GuavaRateLimiterKeys.JWYTH_GUAVA_RATE_LIMITER_TOTAL);
            boolean defaultHasChange = event.isChanged(GuavaRateLimiterKeys.JWYTH_GUAVA_RATE_LIMITER_DEFAULT_KEY);
            if ((!hasChange) && (!totalHasChange) && (!defaultHasChange)) {
                return;
            }
            updateRateLimiter();
        } catch (Exception e) {
            log.error("limiter config is error,e", e);
        }

    }


    public void updateRateLimiter() {
        // 配置url限流
        Map<String, GuavaRateLimiterFlowRuleDto> limiters = fixUrlLimiterMap();
        // 总体限流
        fixTotalRateLimiter();
        // 默认限流规则
        fixDefaultLimit(limiters);

    }

    /**
     * 构造默认限流规则
     *
     * @param limiters
     */
    private void fixDefaultLimit(Map<String, GuavaRateLimiterFlowRuleDto> limiters) {
        String guavaRateLimiterDefaultKey = dictionaryManager.get(GuavaRateLimiterKeys.JWYTH_GUAVA_RATE_LIMITER_DEFAULT_KEY);
        if (StringUtils.isBlank(guavaRateLimiterDefaultKey)) {
            return;
        }
        defaultLimiterMap.clear();
        if (StringUtils.isNumeric(guavaRateLimiterDefaultKey.trim())) {
            Integer qps = Integer.valueOf(guavaRateLimiterDefaultKey.trim());
            Map<String, ServiceBean> map = applicationContext.getBeansOfType(ServiceBean.class);
            List<ServiceBean> serviceBeanList = Lists.newArrayList();
            map.keySet().stream().forEach(s -> serviceBeanList.add(map.get(s)));
            serviceBeanList.stream().forEach(serviceBean -> buildDefaultFlowRules(serviceBean, qps, limiters));
        }
    }

    /**
     * 通过ServiceBean，获取限流接口，配置默认的限流
     *
     * @param serviceBean
     * @param qps
     * @param limiters
     */
    private void buildDefaultFlowRules(ServiceBean serviceBean, Integer qps, Map<String, GuavaRateLimiterFlowRuleDto> limiters) {
        Arrays.stream(serviceBean.getInterfaceClass().getMethods()).forEach(method -> {
            String resourceName = buildResourceName(serviceBean, method);
            String urlLimitDefaultKey = LimitUtils.getLimitMapKey(resourceName, LimitUtils.DEFAULT_RESOURCE_NAME);
            GuavaRateLimiterFlowRuleDto guavaRateLimiterFlowRuleDto = limiters.get(urlLimitDefaultKey);
            if (Objects.isNull(guavaRateLimiterFlowRuleDto)) {
                String limitMapKey = LimitUtils.getLimitMapKey(resourceName, LimitUtils.DEFAULT_RESOURCE_NAME);
                defaultLimiterMap.put(limitMapKey, RateLimiter.create(qps));
            }
        });

    }

    /**
     * 构造资源名称
     *
     * @param serviceBean
     * @param method
     * @return
     */
    private String buildResourceName(ServiceBean serviceBean, Method method) {
        return LimitUtils.structureSentinelResourcesName(serviceBean.getInterface(), method.getName(), method.getParameterTypes());
    }


    /**
     * 构造全局限流
     */
    private void fixTotalRateLimiter() {
        String guavaRateLimiterTotal = dictionaryManager.get(GuavaRateLimiterKeys.JWYTH_GUAVA_RATE_LIMITER_TOTAL);
        if (StringUtils.isBlank(guavaRateLimiterTotal)) {
            return;
        }
        String trim = guavaRateLimiterTotal.trim();
        if (StringUtils.isNumeric(trim)) {
            RateLimiter rateLimiter = RateLimiter.create(Integer.parseInt(trim));
            totalRateLimiter = rateLimiter;
        }
    }

    /**
     * 构造url限流
     */
    private Map<String, GuavaRateLimiterFlowRuleDto> fixUrlLimiterMap() {
        String guavaRateLimiterRule = dictionaryManager.get(GuavaRateLimiterKeys.JWYTH_SYS_GUAVA_RATE_LIMITER_RULE);
        if (StringUtils.isBlank(guavaRateLimiterRule)) {
            return Maps.newHashMap();
        }
        List<GuavaRateLimiterFlowRuleDto> flowRuleDtoList = JsonUtils.toList(guavaRateLimiterRule, GuavaRateLimiterFlowRuleDto.class);
        // 筛选
        Map<String, GuavaRateLimiterFlowRuleDto> limiters = flowRuleDtoList.stream()
                .filter(GuavaRateLimiterFlowRuleDto::hasSuccess)
                .collect(Collectors.toMap(guavaRateLimiterFlowRuleDto -> LimitUtils.getLimitMapKey(guavaRateLimiterFlowRuleDto.getResource(), guavaRateLimiterFlowRuleDto.getLimitApp()), guavaRateLimiterFlowRuleDto -> guavaRateLimiterFlowRuleDto));
        log.debug(" Map<String, GuavaRateLimiterFlowRuleDto> is {}", JsonUtils.toJson(limiters));
        // 清空现有
        urlLimiterMap.clear();
        limiters.keySet().stream().forEach(url -> {
            GuavaRateLimiterFlowRuleDto guavaRateLimiterFlowRuleDto = limiters.get(url);
            Integer qps = guavaRateLimiterFlowRuleDto.getQps();
            urlLimiterMap.put(url, RateLimiter.create(qps));
        });
        return limiters;
    }

}
