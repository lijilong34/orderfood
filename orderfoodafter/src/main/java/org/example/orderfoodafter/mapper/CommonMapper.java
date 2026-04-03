package org.example.orderfoodafter.mapper;

import java.util.List;
import java.util.Map;

/**
 * 通用Mapper接口
 * 提供通用的数据访问操作，支持自定义SQL查询功能
 * 
 * @author 李吉隆
 * @date 2025-11-17
 */
public interface CommonMapper {
    /**
     * 执行自定义SQL查询
     */
    List<Map<String, Object>> selectMapsBySql(String sql);
}
