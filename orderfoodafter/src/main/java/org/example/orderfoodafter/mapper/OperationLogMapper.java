package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.orderfoodafter.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper
 * 提供日志的数据库操作方法
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
    

}