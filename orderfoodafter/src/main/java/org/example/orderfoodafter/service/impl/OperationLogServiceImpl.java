package org.example.orderfoodafter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.entity.OperationLog;
import org.example.orderfoodafter.mapper.OperationLogMapper;
import org.example.orderfoodafter.service.OperationLogService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public int recordOperationLog(OperationLog operationLog) {
        return this.baseMapper.insert(operationLog);
    }

    @Override
    public Map<String, Object> getOperationLogList(Map<String, Object> params, int pageNum, int pageSize) {
        QueryWrapper<OperationLog> queryWrapper = new QueryWrapper<>();
        
        // 添加查询条件
        if (params.containsKey("username") && params.get("username") != null && !params.get("username").toString().isEmpty()) {
            queryWrapper.like("username", params.get("username").toString());
        }
        
        if (params.containsKey("operationType") && params.get("operationType") != null && !params.get("operationType").toString().isEmpty()) {
            queryWrapper.eq("operation_type", params.get("operationType").toString());
        }
        
        if (params.containsKey("result") && params.get("result") != null && !params.get("result").toString().isEmpty()) {
            queryWrapper.eq("result", params.get("result").toString());
        }
        
        if (params.containsKey("startTime") && params.get("startTime") != null && !params.get("startTime").toString().isEmpty()) {
            queryWrapper.ge("operation_time", params.get("startTime").toString());
        }
        
        if (params.containsKey("endTime") && params.get("endTime") != null && !params.get("endTime").toString().isEmpty()) {
            queryWrapper.le("operation_time", params.get("endTime").toString());
        }
        
        queryWrapper.orderByDesc("operation_time");
        
        // 使用 PageHelper 进行分页
        PageHelper.startPage(pageNum, pageSize);
        List<OperationLog> logList = this.list(queryWrapper);
        PageInfo<OperationLog> pageInfo = new PageInfo<>(logList);
        
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("rows", pageInfo.getList());
        resultMap.put("total", pageInfo.getTotal());
        resultMap.put("pageNum", pageInfo.getPageNum());
        resultMap.put("pageSize", pageInfo.getPageSize());
        resultMap.put("pages", pageInfo.getPages());
        return resultMap;
    }

    @Override
    public OperationLog getOperationLogById(Long logId) {
        return this.getById(logId);
    }

    @Override
    public int deleteOperationLog(Long logId) {
        return this.baseMapper.deleteById(logId);
    }

    @Override
    public int batchDeleteOperationLog(List<Long> logIds) {
        return this.baseMapper.deleteBatchIds(logIds);
    }

    @Override
    public int deleteOperationLogByTimeRange(String startTime, String endTime) {
        QueryWrapper<OperationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("operation_time", startTime, endTime);
        return this.baseMapper.delete(queryWrapper);
    }
}