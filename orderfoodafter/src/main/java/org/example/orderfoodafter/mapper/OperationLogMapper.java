package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.orderfoodafter.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper接口
 * 用于对操作日志表进行数据访问操作，提供操作日志信息的增删改查功能
 *
 * @author 熊杨博，周子金
 * @date 2026-01-20
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
    

}