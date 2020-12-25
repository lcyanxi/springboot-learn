package com.lcyanxi.dubboFilter;

import com.lcyanxi.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;

/**
 * @author lichang
 * @date 2020/12/25
 */
@Slf4j
@Activate(group = Constants.PROVIDER)
public class LogTraceProviderFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        //上游如果手动设置了consumer的应用名进attachment，则取出来打印
        String clientIp = RpcContext.getContext().getRemoteHost();//这次请求来自哪个ip
        String application = RpcContext.getContext().getAttachment(Constants.APPLICATION);
        String from = clientIp;
        if (!StringUtils.isEmpty(application)) {
            from = application + "(" + clientIp + ")";
        }
        log.warn("[Trace]From [{}], [{}].[{}]() param:[{}]",
                from, invoker.getInterface(), invocation.getMethodName(), invocation.getArguments());
        return invoker.invoke(invocation);
    }
}
