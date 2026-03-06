package org.example.orderfoodafter.config;

import org.example.orderfoodafter.interceptor.OperationLogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置类
 * 注册系统拦截器
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private OperationLogInterceptor operationLogInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册操作日志拦截器
        registry.addInterceptor(operationLogInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除静态资源和登录接口
                .excludePathPatterns(
                        "/static/**",
                        "/login",
                        "/api/login",
                        "/admin/login",
                        "/ForgetPassword/**"
                );
    }
}