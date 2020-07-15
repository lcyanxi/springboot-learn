package com.lcyanxi.limit.filter;


import com.google.common.util.concurrent.RateLimiter;
import com.lcyanxi.limit.listener.GuavaDefaultRateLimiterListener;
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
@Activate(group = "provider", order = 10000)
public class GuavaTotalLimitFlowFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            RateLimiter totalRateLimiter = GuavaDefaultRateLimiterListener.totalRateLimiter;
            log.info("GuavaTotalLimitFlowFilter totalRateLimiter:{}",totalRateLimiter);
            if (Objects.nonNull(totalRateLimiter) && !totalRateLimiter.tryAcquire()) {
                log.error("The request is total limited, please control the call frequency limitMapKey is {}", totalRateLimiter);
                return AsyncRpcResult.newDefaultAsyncResult(new RpcException("The request is total limited, please control the call frequency"), invocation);
            }
        } catch (Exception e) {
            log.error("GuavaTotalLimitFlowFilter error e is ", e);
        }
        return invoker.invoke(invocation);
    }
}
