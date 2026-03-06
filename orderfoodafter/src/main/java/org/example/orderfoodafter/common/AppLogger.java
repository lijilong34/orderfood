package org.example.orderfoodafter.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 应用程序日志工具类
 * 提供统一的日志记录接口
 */
@Component
public class AppLogger {
    
    private static final Logger logger = LoggerFactory.getLogger(AppLogger.class);
    
    /**
     * 记录INFO级别日志
     */
    public void info(String message, Object... args) {
        logger.info(message, args);
    }
    
    /**
     * 记录DEBUG级别日志
     */
    public void debug(String message, Object... args) {
        logger.debug(message, args);
    }
    
    /**
     * 记录WARN级别日志
     */
    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }
    
    /**
     * 记录ERROR级别日志
     */
    public void error(String message, Throwable throwable, Object... args) {
        logger.error(message, throwable, args);
    }
    
    /**
     * 记录ERROR级别日志（无异常）
     */
    public void error(String message, Object... args) {
        logger.error(message, args);
    }
    
    /**
     * 记录用户操作日志
     */
    public void userAction(String username, String action, String details) {
        logger.info("用户操作 - 用户: {}, 操作: {}, 详情: {}", username, action, details);
    }
    
    /**
     * 记录系统事件日志
     */
    public void systemEvent(String event, String details) {
        logger.info("系统事件 - 事件: {}, 详情: {}", event, details);
    }
    
    /**
     * 记录性能日志
     */
    public void performance(String operation, long timeMillis) {
        logger.info("性能监控 - 操作: {}, 耗时: {}ms", operation, timeMillis);
    }
}