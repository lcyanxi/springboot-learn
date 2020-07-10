package com.lcyanxi.limit.filter;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcResult;
import com.google.common.util.concurrent.RateLimiter;
import com.lcyanxi.limit.listener.GuavaDefaultRateLimiterListener;
import com.lcyanxi.limit.util.DubboUtils;
import com.lcyanxi.limit.util.LimitUtils;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lichang
 * @date 2020/7/6
 */
@Slf4j
@Activate(group = "provider",order = 10002)
public class GuavaDefaultLimitFlowFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            // 获取默认限流缓存
            Map<String, RateLimiter> defaultLimiterMap = GuavaDefaultRateLimiterListener.defaultLimiterMap;
            // 获取核心限流缓存
            Map<String, RateLimiter> urlLimiterMap = GuavaDefaultRateLimiterListener.urlLimiterMap;

            log.info("GuavaDefaultLimitFlowFilter defaultLimiterMap:{}",defaultLimiterMap);
            log.info("GuavaDefaultLimitFlowFilter urlLimiterMap:{}",urlLimiterMap);
            String invokerName = LimitUtils.getInvokerName();
            String serviceMethodName = DubboUtils.structureSentinelResourcesName();
            String limitMapKey = LimitUtils.getLimitMapKey(serviceMethodName, invokerName);
            // 先从核心限流缓存里面取，查看有没有对应的接口调用方限流，如果有，则直接跳过
            RateLimiter limiter = urlLimiterMap.get(limitMapKey);
            if (Objects.isNull(limiter)) {
                // 从默认限流缓存中取如果没有，则没有设定限流缓存
                limitMapKey = LimitUtils.getLimitMapKey(serviceMethodName, LimitUtils.DEFAULT_RESOURCE_NAME);
                RateLimiter rateLimiter = defaultLimiterMap.get(limitMapKey);
                if (Objects.nonNull(rateLimiter) && !rateLimiter.tryAcquire()) {
                    log.error("The request is default resources limited, please control the call frequency limitMapKey is {}", limitMapKey);
                    return new RpcResult(new RpcException("The request is default resources limited, please control the call frequency"));
                }
            }
        } catch (Exception e) {
            log.error("GuavaDefaultLimitFlowFilter error e is ", e);
        }
        return invoker.invoke(invocation);
    }
}
