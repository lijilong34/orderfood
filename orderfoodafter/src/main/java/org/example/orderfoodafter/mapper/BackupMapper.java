package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.orderfoodafter.entity.Backup;

/**
 * 备份Mapper接口
 * 用于对备份表进行数据访问操作，提供备份信息的增删改查功能
 *
 * @author 周子金
 * @date 2026-02-25
 */
@Mapper
public interface BackupMapper extends BaseMapper<Backup> {
}