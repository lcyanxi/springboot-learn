package com.lcyanxi.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author lichang
 * Date: 2021/06/22/7:46 下午
 */
@Slf4j
public class RequestLogInterceptor extends HandlerInterceptorAdapter {

    /**
     * Thread local container for request attributes which are to be printed
     */
    public static class RequestLogger {
        private static final String ATTR_SEPARATOR = "||";

        private static final ThreadLocal<Map<String, String>> requestAttrs =
                new ThreadLocal<Map<String, String>>() {
                    @Override
                    protected Map<String, String> initialValue() {
                        return new LinkedHashMap<>();
                    }
                };

        public static void reset() {
            if (requestAttrs.get().size() > 0) {
                requestAttrs.get().clear();
            }
        }

        public static void setAttribute(String key, String value) {
            requestAttrs.get().put(key, value);
        }

        public static String getAttribute(String key) {
            return requestAttrs.get().get(key);
        }

        public static String formatRequestLog() {
            Map<String, String> attrs = requestAttrs.get();
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, String> keyValue : attrs.entrySet()) {
                builder.append(keyValue.getKey()).append("=").append(keyValue.getValue())
                        .append(ATTR_SEPARATOR);
            }
            return builder.toString();
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // reset the request logger before the business code
        RequestLogger.reset();

        // add the uri and parameters into the request logger by default
        RequestLogger.setAttribute("method", request.getMethod());
        RequestLogger.setAttribute("uri", request.getRequestURI());
        RequestLogger.setAttribute("ua", request.getHeader("User-Agent"));
        RequestLogger.setAttribute("startTime", String.valueOf(System.currentTimeMillis()));

        if (request.getMethod().equals("POST")){
            try{
                RequestWrapper requestWrapper = new RequestWrapper(request);
                RequestLogger.setAttribute("data", requestWrapper.getBody());
            }catch (Exception e){
                log.error("requestLogInterceptor is exception",e);
            }
        }else {
            Enumeration paramIter = request.getParameterNames();
            while (paramIter.hasMoreElements()) {
                String paramKey = String.valueOf(paramIter.nextElement());
                String paramValue = request.getParameter(paramKey);
                RequestLogger.setAttribute(paramKey, paramValue);
            }
        }

        return true;
    }

    // use afterComletion instead of postHandle in case exception happens
    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Object currentUid = request.getAttribute("currentUid");
        if (currentUid != null) {
            RequestLogger.setAttribute("currentUid", String.valueOf(currentUid));
        }

        long finishTime = System.currentTimeMillis();
        RequestLogger.setAttribute("finishTime", String.valueOf(finishTime));
        // calculate request cost time
        String startTime = RequestLogger.getAttribute("startTime");
        if (!StringUtils.isBlank(startTime)) {
            long cost = finishTime - Long.parseLong(startTime);
            RequestLogger.setAttribute("requestCost", String.valueOf(cost));
        }

        Exception exception = ex != null ?
                ex : (Exception) request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE);
        if (exception != null) {
            RequestLogInterceptor.RequestLogger.setAttribute("errorCode", String.valueOf(500));
            RequestLogInterceptor.RequestLogger.setAttribute("errorMsg",
                    exception.getClass().getName() + ": " + exception.getMessage());

        }
        log.info(RequestLogger.formatRequestLog());
    }

}
