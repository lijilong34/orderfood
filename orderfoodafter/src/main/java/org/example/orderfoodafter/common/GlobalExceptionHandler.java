package org.example.orderfoodafter.common;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
这是一个全局异常处理
 */
@RestControllerAdvice//拦截所有的@RestController
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public R exceptionHandler(Exception e){
        R r = new R();
        r.setCode(500);
        r.setMessage("出错了，呜呜呜!"+e.getMessage());
        return r;
    }

    // 处理 JSON 解析错误
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
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
