package com.lcyanxi.limit;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lichang
 * @date 2020/7/6
 */
@Slf4j
@Activate(group = "provider",order = 1000)
public class GuavaDefaultLimitFlowFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Result rs = null;
        try {
            String clientIp = RpcContext.getContext().getRemoteHost();
            rs = invoker.invoke(invocation);
            log.info("GuavaDefaultLimitFlowFilter 远程地址ip:{} ", clientIp);
        }catch (Exception e) {
            log.error("GuavaResourcesLimitFlowFilter error e is ", e);
            // TODO: handle exception
        }
        return rs;
    }
}
