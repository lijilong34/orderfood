package org.example.orderfoodafter.service;

import org.example.orderfoodafter.entity.OperationLog;

import java.util.List;
import java.util.Map;

/**
 * 操作日志Service接口
 * 提供操作日志相关的业务逻辑处理功能，包括操作日志信息的增删改查等操作
 *
 * @author 熊杨博
 * @date 2026-01-20
 */
public interface OperationLogService {
    
    /**
     * 记录操作日志
     * @param operationLog 操作日志对象
     * @return 影响行数
     */
    int recordOperationLog(OperationLog operationLog);
    
    /**
     * 分页查询操作日志列表
     * @param params 查询条件
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 操作日志列表和总数
     */
    Map<String, Object> getOperationLogList(Map<String, Object> params, int pageNum, int pageSize);
    
    /**
     * 根据日志ID查询操作日志
     * @param logId 日志ID
     * @return 操作日志对象
     */
    OperationLog getOperationLogById(Long logId);
    
    /**
     * 删除操作日志
     * @param logId 日志ID
     * @return 影响行数
     */
    int deleteOperationLog(Long logId);
    
    /**
     * 批量删除操作日志
     * @param logIds 日志ID列表
     * @return 影响行数
     */
    int batchDeleteOperationLog(List<Long> logIds);
    
    /**
     * 根据时间范围删除操作日志
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 影响行数
     */
    int deleteOperationLogByTimeRange(String startTime, String endTime);
}