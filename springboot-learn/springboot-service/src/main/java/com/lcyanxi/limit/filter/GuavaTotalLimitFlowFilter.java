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
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lichang
 * @date 2020/7/10
 */
@Slf4j
@Activate(group = "provider", order = 10000)
public class GuavaTotalLimitFlowFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            RateLimiter totalRateLimiter = GuavaDefaultRateLimiterListener.totalRateLimiter;
            log.info("GuavaTotalLimitFlowFilter totalRateLimiter:{}",totalRateLimiter);
            if (Objects.nonNull(totalRateLimiter) && !totalRateLimiter.tryAcquire()) {
                log.error("The request is total limited, please control the call frequency limitMapKey is {}", totalRateLimiter);
                return new RpcResult(new RpcException("The request is total limited, please control the call frequency"));
            }
        } catch (Exception e) {
            log.error("GuavaTotalLimitFlowFilter error e is ", e);
        }
        return invoker.invoke(invocation);
    }
}
