package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.BrowseHistory;
import org.example.orderfoodafter.mapper.BrowseHistoryMapper;
import org.example.orderfoodafter.service.BrowseHistoryService;

/**
 * 浏览历史Service实现类
 * 实现浏览历史相关的业务逻辑处理功能
 *
 * @author 赵康乐
 * @date 2025-11-25
 */
@Service
public class BrowseHistoryServiceImpl extends ServiceImpl<BrowseHistoryMapper, BrowseHistory> implements BrowseHistoryService{
/**
 * selecthistorybyuserId方法
 *
 * @author 李梦瑶
 */

    @Override
    public List<BrowseHistory> selecthistorybyuserId(QueryWrapper queryWrapper) {
        List<BrowseHistory> browseHistoryList = baseMapper.selecthistorybyuserId(queryWrapper);
        return browseHistoryList;
    }
}
