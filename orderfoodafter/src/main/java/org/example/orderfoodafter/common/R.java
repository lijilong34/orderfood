package org.example.orderfoodafter.common;

import java.util.HashMap;
import java.util.Map;


/*
统一返回对象
 */
public class R {
    private int code=200;//状态码
    private String message="操作成功";//返回信息

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private Map<String, Object> data=new HashMap<>();
    public R addData(String key, Object value) {
       this.data.put(key, value);
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    // 成功响应
    public static R ok() {
        return new R();
    }

    // 失败响应
    public static R error(String message) {
        R r = new R();
        r.setCode(500);
        r.setMessage(message);
        return r;
    }

    // 自定义状态码的失败响应
    public static R fail(String message) {
        return error(message);
    }
}
