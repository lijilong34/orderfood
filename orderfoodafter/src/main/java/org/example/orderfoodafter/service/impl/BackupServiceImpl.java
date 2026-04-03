package org.example.orderfoodafter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Backup;
import org.example.orderfoodafter.mapper.BackupMapper;
import org.example.orderfoodafter.service.BackupService;
import org.springframework.stereotype.Service;

/**
 * 备份Service实现类
 * 实现备份相关的业务逻辑处理功能
 * 
 * @author 李吉隆
 * @date 2026-03-01
 */
@Service
public class BackupServiceImpl extends ServiceImpl<BackupMapper, Backup> implements BackupService {
/**
 * getBackupList方法
 *
 * @author 李吉隆
 */

    @Override
    public R getBackupList(Integer currentPage, Integer pageSize) {
        try {
            Page<Backup> page = new Page<>(currentPage, pageSize);
            QueryWrapper<Backup> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("create_time");
            
            Page<Backup> resultPage = this.page(page, queryWrapper);
            
            return R.ok()
                    .addData("list", resultPage.getRecords())
                    .addData("total", resultPage.getTotal())
                    .addData("currentPage", currentPage)
                    .addData("pageSize", pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取备份列表失败");
        }
    }
/**
 * saveBackupRecord方法
 *
 * @author 李吉隆
 */

    @Override
    public R saveBackupRecord(Backup backup) {
        try {
            boolean result = this.save(backup);
            if (result) {
                return R.ok().addData("message", "备份记录保存成功");
            } else {
                return R.error("备份记录保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("保存备份记录失败");
        }
    }
}