package org.example.orderfoodafter.mapper;

import org.example.orderfoodafter.entity.Announcement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公告Mapper接口
 * 用于对公告表进行数据访问操作，提供公告信息的增删改查功能
 * 
 * @author 周子金
 * @date 2025-11-16
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}