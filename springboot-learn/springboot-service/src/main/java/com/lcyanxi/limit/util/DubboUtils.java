package com.lcyanxi.limit.util;

import com.alibaba.dubbo.rpc.RpcContext;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lichang
 * @date 2020/7/9
 */
public class DubboUtils {

    public static String getInvokerName() {
        String invokerName = RpcContext.getContext().getAttachment("application");
        if (StringUtils.isBlank(invokerName)) {
            invokerName = "noName";
        }
        return invokerName;
    }

    /**
     * structure Sentinel ResourcesName
     *
     * @return {result = interfaceName+'：'+MethodName+'('+parameterType1+','+parameterType2+...+')'}
     */
    public static String structureSentinelResourcesName() {
        RpcContext context = RpcContext.getContext();
        String application = context.getUrl().getParameter("interface");
        StringBuilder methodName = new StringBuilder(context.getMethodName());
        Class<?>[] parameterTypes = context.getParameterTypes();
        methodName.append("(");
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (i == parameterTypes.length - 1) {
                methodName.append(parameterType.getName());
            } else {
                methodName.append(parameterType.getName()).append(",");
            }
        }
        methodName.append(")");
        String result = new StringBuilder().append(application).append(":").append(methodName).toString();
        return result;
    }
}
