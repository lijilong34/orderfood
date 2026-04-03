package org.example.orderfoodafter.common;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一处理系统中的各种异常，提供友好的错误信息响应
 * 
 * @author 李吉隆
 * @date 2025-11-19
 */
@RestControllerAdvice//拦截所有的@RestController
public class GlobalExceptionHandler {
/**
 * exceptionHandler方法
 *
 * @author 李吉隆
 */
    
    @ExceptionHandler(Exception.class)
    public R exceptionHandler(Exception e){
        // 检查是否是SSE相关的异常，如果是则不处理
        if (e.getMessage() != null && 
            (e.getMessage().contains("ServletOutputStream failed to flush") ||
             e.getMessage().contains("ClientAbortException") ||
             e.getMessage().contains("已建立的连接") ||
             e instanceof ClientAbortException)) {
            // SSE连接中断，不需要处理，直接抛出
                /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
            throw new RuntimeException(e);
        }
        
            /**
     * R
     * 
     * @author 李吉隆
     */
        R r = new R();
        r.setCode(500);
        r.setMessage("出错了，呜呜呜!"+e.getMessage());
        return r;
    }

    // 处理 JSON 解析错误
/**
 * handleHttpMessageNotReadable方法
 *
 * @author 李吉隆
 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
            /**
     * R
     * 
     * @author 李吉隆
     */
        R r = new R();
        r.setCode(400);
        
        Throwable cause = e.getCause();
        String errorMessage = "请求数据格式错误";
        
        if (cause instanceof JsonParseException) {
            errorMessage = "JSON 格式错误: " + cause.getMessage();
        } else if (cause instanceof JsonMappingException) {
            errorMessage = "JSON 映射错误: " + cause.getMessage();
        } else if (cause instanceof MismatchedInputException) {
            MismatchedInputException mie = (MismatchedInputException) cause;
            errorMessage = "数据类型不匹配: " + mie.getMessage();
        }
        
        r.setMessage(errorMessage);
        return r;
    }
}
