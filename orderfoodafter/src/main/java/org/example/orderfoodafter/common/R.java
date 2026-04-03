package org.example.orderfoodafter.common;

import java.util.HashMap;
import java.util.Map;


/**
 * 统一响应结果类
 * 封装API接口的统一返回格式，包含状态码、消息和数据
 * 
 * @author 李吉隆
 * @date 2026-03-18
 */
public class R {
    private int code=200;//状态码
    private String message="操作成功";//返回信息
    private Map<String, Object> data=new HashMap<>();

    /**
     * 获取消息
     * @return 消息
     * @author 李吉隆
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置消息
     * @param message 消息
     * @author 李吉隆
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取状态码
     * @return 状态码
     * @author 李吉隆
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置状态码
     * @param code 状态码
     * @author 李吉隆
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 添加数据
     * @param key 键
     * @param value 值
     * @return 响应对象
     * @author 李吉隆
     */
    public R addData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    /**
     * 获取数据
     * @return 数据
     * @author 李吉隆
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * 设置数据
     * @param data 数据
     * @author 李吉隆
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /**
     * 成功响应
     * @return 成功响应对象
     * @author 李吉隆
     */
    public static R ok() {
        return new R();
    }

    /**
     * 失败响应
     * @param message 错误消息
     * @return 失败响应对象
     * @author 李吉隆
     */
    public static R error(String message) {
        R r = new R();
        r.setCode(500);
        r.setMessage(message);
        return r;
    }

    /**
     * 自定义状态码的失败响应
     * @param message 错误消息
     * @return 失败响应对象
     * @author 李吉隆
     */
    public static R fail(String message) {
        return error(message);
    }
}
