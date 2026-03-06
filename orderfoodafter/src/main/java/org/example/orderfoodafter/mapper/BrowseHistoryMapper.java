package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.BrowseHistory;

import java.util.List;

public interface BrowseHistoryMapper extends BaseMapper<BrowseHistory> {
    List<BrowseHistory> selecthistorybyuserId(@Param("ew") QueryWrapper queryWrapper);
}