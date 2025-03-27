package com.lcyanxi.fuxi.designPattern.chain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class MyHandlerExecutionChain {
    private final Object handler;

    @Nullable
    private List<MyHandlerInterceptor> interceptorList;

    @Nullable
    private MyHandlerInterceptor[] interceptors;

    private int interceptorIndex = -1;


    public MyHandlerExecutionChain(Object handler) {
        MyHandlerExecutionChain originalChain = (MyHandlerExecutionChain) handler;
        this.handler = originalChain.getHandler();
        this.interceptorList = new ArrayList<>();
    }


    public void addInterceptor(MyHandlerInterceptor interceptor) {
        initInterceptorList().add(interceptor);
    }

    boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MyHandlerInterceptor[] interceptors = getInterceptors();
        if (!ObjectUtils.isEmpty(interceptors)) {
            for (int i = 0; i < interceptors.length; i++) {
                MyHandlerInterceptor interceptor = interceptors[i];
                if (!interceptor.preHandle(request, response, this.handler)) {
                    triggerAfterCompletion(request, response, null);
                    return false;
                }
            }
        }
        return true;
    }

    void applyPostHandle(HttpServletRequest request, HttpServletResponse response, ModelAndView mv) throws Exception {
        MyHandlerInterceptor[] interceptors = getInterceptors();
        if (!ObjectUtils.isEmpty(interceptors)) {
            for (int i = interceptors.length - 1; i >= 0; i--) {
                MyHandlerInterceptor interceptor = interceptors[i];
                interceptor.postHandle(request, response, this.handler, mv);
            }
        }
    }

    void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, Exception ex) throws Exception {
        MyHandlerInterceptor[] interceptors = getInterceptors();
        if (!ObjectUtils.isEmpty(interceptors)) {
            for (int i = this.interceptorIndex; i >= 0; i--) {
                MyHandlerInterceptor interceptor = interceptors[i];
                try {
                    interceptor.afterCompletion(request, response, this.handler, ex);
                } catch (Throwable ex2) {
                    log.error("HandlerInterceptor.afterCompletion threw exception", ex2);
                }
            }
        }
    }

    private List<MyHandlerInterceptor> initInterceptorList() {
        if (this.interceptorList == null) {
            this.interceptorList = new ArrayList<>();
            if (this.interceptors != null) {
                // An interceptor array specified through the constructor
                CollectionUtils.mergeArrayIntoCollection(this.interceptors, this.interceptorList);
            }
        }
        this.interceptors = null;
        return this.interceptorList;
    }

    public MyHandlerInterceptor[] getInterceptors() {
        if (this.interceptors == null && this.interceptorList != null) {
            this.interceptors = this.interceptorList.toArray(new MyHandlerInterceptor[0]);
        }
        return this.interceptors;
    }
}
