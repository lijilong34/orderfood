package org.example.orderfoodafter.mapper;

import java.util.List;
import java.util.Map;

public interface CommonMapper {
    /**
     * 执行自定义SQL查询
     */
    List<Map<String, Object>> selectMapsBySql(String sql);
}
