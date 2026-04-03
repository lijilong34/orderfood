package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.entity.BrowseHistory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 浏览历史Service接口
 * 提供浏览历史相关的业务逻辑处理功能，包括浏览历史信息的增删改查等操作
 *
 * @author 赵康乐
 * @date 2025-11-24
 */
public interface BrowseHistoryService extends IService<BrowseHistory>{


    List<BrowseHistory> selecthistorybyuserId(QueryWrapper queryWrapper);
}
