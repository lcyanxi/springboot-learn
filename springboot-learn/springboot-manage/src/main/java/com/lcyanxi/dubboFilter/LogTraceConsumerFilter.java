package com.lcyanxi.dubboFilter;

import com.lcyanxi.constant.Constants;
import lombok.extern.slf4j.Slf4j;
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
@Activate(group = Constants.CONSUMER)
public class LogTraceConsumerFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        //手动设置consumer的应用名进attachment
        String application = invoker.getUrl().getParameter(Constants.APPLICATION);
        if (application != null) {
            RpcContext.getContext().setAttachment(Constants.APPLICATION, application);
        }

        Result result = null;
        String serverIp = null;
        long startTime = System.currentTimeMillis();
        try {
            result = invoker.invoke(invocation);
            serverIp = RpcContext.getContext().getRemoteHost();//这次返回结果是哪个ip
            return result;
        } finally {
            Throwable throwable = (result == null) ? null : result.getException();
            Object resultObj = (result == null) ? null : result.getValue();
            long costTime = System.currentTimeMillis() - startTime;
            log.info("[TRACE] Call [{}], [{}].{}()] param:[{}], return:[{}], exception:[{}], cost:[{} ms]!",
                    serverIp, invoker.getInterface(), invocation.getMethodName(), invocation.getArguments(), resultObj, throwable, costTime);
        }
    }
}
