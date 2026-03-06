package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.SystemSettings;

import java.util.Map;

/**
 * 系统设置服务接口
 */
public interface SystemSettingsService extends IService<SystemSettings> {
    
    /**
     * 获取系统设置
     */
    R getSystemSettings();
    
    /**
     * 保存基本设置
     */
    R saveBasicSettings(Map<String, Object> settings);
    
    /**
     * 保存安全设置
     */
    R saveSecuritySettings(Map<String, Object> settings);
    
    /**
     * 保存备份设置
     */
    R saveBackupSettings(Map<String, Object> settings);
    
    /**
     * 修改管理员密码
     */
    R changeAdminPassword(Map<String, String> passwordInfo);
    
    /**
     * 上传SSL证书
     */
    R uploadSSLCertificate(String certificateName, String certificatePath);
    
    /**
     * 删除SSL证书
     */
    R removeSSLCertificate();
    
    
    
    /**
     * 重启系统
     */
    R restartSystem();
}