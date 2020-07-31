package com.lcyanxi.limit.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.rpc.RpcContext;

/**
 * @author lichang
 * @date 2020/7/9
 */
@Slf4j
public class LimitUtils {

    /**
     * 默认限流来源名称
     */
    public static final String DEFAULT_RESOURCE_NAME = "default";

    /**
     * interfaceName + ":" + methodName + "(" + parameterTypes + ")"
     * @param interfaceName
     * @param method
     * @param parameterTypes
     * @return
     */
    public static String structureSentinelResourcesName(String interfaceName, String method, Class<?>[] parameterTypes) {
        StringBuilder methodName = new StringBuilder(method);
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
        String result = new StringBuilder().append(interfaceName).append(":").append(methodName).toString();
        return result;
    }


    /**
     * 获取调用方名称
     * @return
     */
    public static String getInvokerName() {
        String invokerName = RpcContext.getContext().getAttachment("application");
        if (StringUtils.isBlank(invokerName)) {
            invokerName = DEFAULT_RESOURCE_NAME;
        }
        return invokerName;
    }


    /**
     * url + ":" + lmitApp
     * 如果lmitApp为空则
     * url + ":" + "default"
     * @param url
     * @param lmitApp
     * @return
     */
    public static String getLimitMapKey(String url, String lmitApp) {
        if (StringUtils.isBlank(lmitApp)) {
            return url + ":" + DEFAULT_RESOURCE_NAME;
        }
        return url + ":" + lmitApp;
    }
}
