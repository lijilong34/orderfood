package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.orderfoodafter.entity.SystemSettings;

/**
 * 系统设置Mapper接口
 * 用于对系统设置表进行数据访问操作，提供系统设置信息的增删改查功能
 * 
 * @author 李吉隆
 * @date 2026-02-20
 */
@Mapper
public interface SystemSettingsMapper extends BaseMapper<SystemSettings> {
}