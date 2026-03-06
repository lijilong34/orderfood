package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 * 记录系统和用户的操作日志
 */
@TableName("operation_log")
public class OperationLog {
    /**
     * 日志ID
     */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;
    
    /**
     * 操作人ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 操作人名称
     */
    @TableField("username")
    private String username;
    
    /**
     * 操作类型（如：登录、新增、修改、删除、查询等）
     */
    @TableField("operation_type")
    private String operationType;
    
    /**
     * 操作内容（详细描述操作）
     */
    @TableField("operation_content")
    private String operationContent;
    
    /**
     * 操作IP地址
     */
    @TableField("ip_address")
    private String ipAddress;
    
    /**
     * 操作结果（成功/失败）
     */
    @TableField("result")
    private String result;
    
    /**
     * 错误信息（如果操作失败）
     */
    @TableField("error_message")
    private String errorMessage;
    
    /**
     * 操作时间
     */
    @TableField("operation_time")
    private LocalDateTime operationTime;
    
    /**
     * 请求URL
     */
    @TableField("request_url")
    private String requestUrl;
    
    /**
     * 请求方法（GET、POST等）
     */
    @TableField("request_method")
    private String requestMethod;
    
    /**
     * 响应时间（毫秒）
     */
    @TableField("response_time")
    private Long responseTime;

    // Getter and Setter methods
    
    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationContent() {
        return operationContent;
    }

    public void setOperationContent(String operationContent) {
        this.operationContent = operationContent;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }
}