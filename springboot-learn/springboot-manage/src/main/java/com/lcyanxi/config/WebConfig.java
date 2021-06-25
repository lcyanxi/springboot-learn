package com.lcyanxi.config;

import com.lcyanxi.filter.ChannelFilter;
import com.lcyanxi.filter.RequestLogInterceptor;
import com.lcyanxi.jwt.JWTInterceptor;
import org.springframework.aop.SpringProxy;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author lichang
 * Date: 2021/06/22/11:43 下午
 */
@Configuration
public class WebConfig implements WebMvcConfigurer,SpringProxy {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new JWTInterceptor())
                .addPathPatterns("/user/*")
                .excludePathPatterns("/login");
                registry.addInterceptor(new RequestLogInterceptor());
    }

    @Bean
    public FilterRegistrationBean timeFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        ChannelFilter myFilter = new ChannelFilter();
        registrationBean.setFilter(myFilter);
        return registrationBean;
    }
}
