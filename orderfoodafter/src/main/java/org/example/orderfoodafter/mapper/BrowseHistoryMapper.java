package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.BrowseHistory;

import java.util.List;

/**
 * 浏览历史Mapper接口
 * 用于对浏览历史表进行数据访问操作，提供浏览历史信息的增删改查功能
 *
 * @author 赵康乐
 * @date 2025-11-24
 */
public interface BrowseHistoryMapper extends BaseMapper<BrowseHistory> {
    List<BrowseHistory> selecthistorybyuserId(@Param("ew") QueryWrapper queryWrapper);
}