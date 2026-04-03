package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 * 用于记录系统和用户的操作日志，包括操作类型、操作内容、操作时间等
 * 
 * @author 李吉隆
 * @date 2026-01-21
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
    
        /**
     * 获取 getLogId
     * 
     * @return getLogId
     * @author 周子金
     */
    public Long getLogId() {
        return logId;
    }
/**
 * setLogId方法
 *
 * @author 李吉隆
 */

    public void setLogId(Long logId) {
        this.logId = logId;
    }
/**
 * getUserId方法
 *
 * @author 李吉隆
 */

    public Long getUserId() {
        return userId;
    }
/**
 * setUserId方法
 *
 * @author 李吉隆
 */

    public void setUserId(Long userId) {
        this.userId = userId;
    }
/**
 * getUsername方法
 *
 * @author 李吉隆
 */

    public String getUsername() {
        return username;
    }
/**
 * setUsername方法
 *
 * @author 李吉隆
 */

    public void setUsername(String username) {
        this.username = username;
    }
/**
 * getOperationType方法
 *
 * @author 李吉隆
 */

    public String getOperationType() {
        return operationType;
    }
/**
 * setOperationType方法
 *
 * @author 李吉隆
 */

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
/**
 * getOperationContent方法
 *
 * @author 李吉隆
 */

    public String getOperationContent() {
        return operationContent;
    }
/**
 * setOperationContent方法
 *
 * @author 李吉隆
 */

    public void setOperationContent(String operationContent) {
        this.operationContent = operationContent;
    }
/**
 * getIpAddress方法
 *
 * @author 李吉隆
 */

    public String getIpAddress() {
        return ipAddress;
    }
/**
 * setIpAddress方法
 *
 * @author 李吉隆
 */

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
/**
 * getResult方法
 *
 * @author 李吉隆
 */

    public String getResult() {
        return result;
    }
/**
 * setResult方法
 *
 * @author 李吉隆
 */

    public void setResult(String result) {
        this.result = result;
    }
/**
 * getErrorMessage方法
 *
 * @author 李吉隆
 */

    public String getErrorMessage() {
        return errorMessage;
    }
/**
 * setErrorMessage方法
 *
 * @author 李吉隆
 */

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
/**
 * getOperationTime方法
 *
 * @author 李吉隆
 */

    public LocalDateTime getOperationTime() {
        return operationTime;
    }
/**
 * setOperationTime方法
 *
 * @author 李吉隆
 */

    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }
/**
 * getRequestUrl方法
 *
 * @author 李吉隆
 */

    public String getRequestUrl() {
        return requestUrl;
    }
/**
 * setRequestUrl方法
 *
 * @author 李吉隆
 */

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }
/**
 * getRequestMethod方法
 *
 * @author 李吉隆
 */

    public String getRequestMethod() {
        return requestMethod;
    }
/**
 * setRequestMethod方法
 *
 * @author 李吉隆
 */

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }
/**
 * getResponseTime方法
 *
 * @author 李吉隆
 */

    public Long getResponseTime() {
        return responseTime;
    }
/**
 * setResponseTime方法
 *
 * @author 李吉隆
 */

    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }
}