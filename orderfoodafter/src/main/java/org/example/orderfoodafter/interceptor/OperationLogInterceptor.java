package org.example.orderfoodafter.interceptor;

import org.example.orderfoodafter.entity.OperationLog;
import org.example.orderfoodafter.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Enumeration;

/**
 * 操作日志拦截器
 * 自动记录系统操作日志
 */
@Component
public class OperationLogInterceptor implements HandlerInterceptor {

    @Autowired
    private OperationLogService operationLogService;

    // 请求开始时间
    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 请求处理前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 记录请求开始时间
        startTime.set(System.currentTimeMillis());
        return true;
    }

    /**
     * 请求处理后，视图渲染前
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 这里可以记录请求处理后的信息
    }

    /**
     * 请求完成后，视图渲染后
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 计算响应时间
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime.get();
        
        // 构建操作日志
        OperationLog operationLog = new OperationLog();
        
        // 设置请求信息
        operationLog.setRequestUrl(request.getRequestURI());
        operationLog.setRequestMethod(request.getMethod());
        operationLog.setIpAddress(getClientIpAddress(request));
        operationLog.setResponseTime(responseTime);
        
        // 设置操作时间
        operationLog.setOperationTime(LocalDateTime.now());
        
        // 获取操作人信息（根据实际情况从Session或Token中获取）
        // 这里暂时设置为默认值，实际项目中需要根据用户认证情况获取
        operationLog.setUserId(1L);
        operationLog.setUsername("admin");
        
        // 设置操作类型和内容（根据实际情况解析）
        operationLog.setOperationType(getOperationType(request));
        operationLog.setOperationContent(getOperationContent(request));
        
        // 设置操作结果
        if (ex == null) {
            operationLog.setResult("成功");
        } else {
            operationLog.setResult("失败");
            operationLog.setErrorMessage(ex.getMessage());
        }
        
        // 异步记录日志，避免影响响应
        new Thread(() -> {
            try {
                operationLogService.recordOperationLog(operationLog);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        // 清除ThreadLocal
        startTime.remove();
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 获取操作类型
     */
    private String getOperationType(HttpServletRequest request) {
        String method = request.getMethod();
        switch (method) {
            case "GET":
                return "查询";
            case "POST":
                return "新增";
            case "PUT":
                return "修改";
            case "DELETE":
                return "删除";
            default:
                return "其他";
        }
    }

    /**
     * 获取操作内容
     */
    private String getOperationContent(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        
        // 根据URI和方法构建操作内容
        StringBuilder content = new StringBuilder();
        content.append("用户");
        content.append("通过");
        content.append(method);
        content.append("方法访问了");
        content.append(uri);
        content.append("接口");
        
        // 添加请求参数（仅记录关键参数，避免敏感信息）
        Enumeration<String> paramNames = request.getParameterNames();
        if (paramNames.hasMoreElements()) {
            content.append("，参数：");
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                // 跳过敏感参数
                if ("password".equals(paramName) || "token".equals(paramName)) {
                    continue;
                }
                String paramValue = request.getParameter(paramName);
                content.append(paramName).append("=")
                      .append(paramValue).append(", ");
            }
            // 移除最后一个逗号和空格
            content.setLength(content.length() - 2);
        }
        
        return content.toString();
    }
}