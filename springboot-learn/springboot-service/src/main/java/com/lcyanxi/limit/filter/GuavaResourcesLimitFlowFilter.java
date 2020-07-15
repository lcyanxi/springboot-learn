package com.lcyanxi.limit.filter;

import com.google.common.util.concurrent.RateLimiter;
import com.lcyanxi.limit.listener.GuavaDefaultRateLimiterListener;
import com.lcyanxi.limit.util.DubboUtils;
import com.lcyanxi.limit.util.LimitUtils;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

/**
 * @author lichang
 * @date 2020/7/10
 */
@Slf4j
@Activate(group = "provider", order = 10001)
public class GuavaResourcesLimitFlowFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        try {
            Map<String, RateLimiter> urlLimiterMap = GuavaDefaultRateLimiterListener.urlLimiterMap;
            log.info("GuavaResourcesLimitFlowFilter urlLimiterMap:{}",urlLimiterMap);
            String invokerName = LimitUtils.getInvokerName();
            String serviceMethodName = DubboUtils.structureSentinelResourcesName();
            String limitMapKey = LimitUtils.getLimitMapKey(serviceMethodName, invokerName);
            // 判断是否存在接口+定向限流
            RateLimiter rateLimiter = urlLimiterMap.get(limitMapKey);
            if (Objects.isNull(rateLimiter)) {
                // 如果找不到定向限流，则找接口+默认限流
                limitMapKey = LimitUtils.getLimitMapKey(serviceMethodName, LimitUtils.DEFAULT_RESOURCE_NAME);
                rateLimiter = urlLimiterMap.get(limitMapKey);
            }
            // 判断控制器不为空并且判断是否获得令牌
            if (Objects.nonNull(rateLimiter) && !rateLimiter.tryAcquire()) {

                log.error("The request is resources limited, please control the call frequency limitMapKey is {}", limitMapKey);
                return AsyncRpcResult.newDefaultAsyncResult(new RpcException("The request is resources limited, please control the call frequency"), invocation);
            }
            log.debug("The request is not has limiter serviceMethodName is {} , invokerName is {} ,limitMapKey is {}", serviceMethodName, invokerName, limitMapKey);
        } catch (Exception e) {
            log.error("GuavaResourcesLimitFlowFilter error e is ", e);
        }
        return invoker.invoke(invocation);
    }
}
