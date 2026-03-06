package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.entity.BrowseHistory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BrowseHistoryService extends IService<BrowseHistory>{


    List<BrowseHistory> selecthistorybyuserId(QueryWrapper queryWrapper);
}
