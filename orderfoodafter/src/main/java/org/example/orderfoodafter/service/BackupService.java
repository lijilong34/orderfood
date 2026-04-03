package org.example.orderfoodafter.service;

import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Backup;

/**
 * 备份Service接口
 * 提供备份相关的业务逻辑处理功能，包括备份信息的增删改查等操作
 *
 * @author 周子金
 * @date 2026-02-25
 */
public interface BackupService {
    
    /**
     * 获取备份列表
     * @param currentPage 当前页码
     * @param pageSize 每页大小
     * @return 备份列表
     */
    R getBackupList(Integer currentPage, Integer pageSize);
    
    /**
     * 保存备份记录到数据库
     * @param backup 备份实体
     * @return 保存结果
     */
    R saveBackupRecord(Backup backup);
}