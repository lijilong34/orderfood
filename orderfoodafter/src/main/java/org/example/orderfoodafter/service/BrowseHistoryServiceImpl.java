package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.BrowseHistory;
import org.example.orderfoodafter.mapper.BrowseHistoryMapper;
import org.example.orderfoodafter.service.BrowseHistoryService;
@Service
public class BrowseHistoryServiceImpl extends ServiceImpl<BrowseHistoryMapper, BrowseHistory> implements BrowseHistoryService{

    @Override
    public List<BrowseHistory> selecthistorybyuserId(QueryWrapper queryWrapper) {
        List<BrowseHistory> browseHistoryList = baseMapper.selecthistorybyuserId(queryWrapper);
        return browseHistoryList;
    }
}
