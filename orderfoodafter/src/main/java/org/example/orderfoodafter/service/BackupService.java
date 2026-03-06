package org.example.orderfoodafter.service;

import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Backup;

/**
 * 备份服务接口
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