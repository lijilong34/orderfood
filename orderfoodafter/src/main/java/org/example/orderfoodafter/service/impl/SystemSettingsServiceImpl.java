package org.example.orderfoodafter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.common.JwtUtils;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Employee;
import org.example.orderfoodafter.entity.SystemSettings;
import org.example.orderfoodafter.mapper.SystemSettingsMapper;
import org.example.orderfoodafter.service.EmployeeService;
import org.example.orderfoodafter.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统设置服务实现类
 */
@Service
public class SystemSettingsServiceImpl extends ServiceImpl<SystemSettingsMapper, SystemSettings> implements SystemSettingsService {

    @Autowired
    private SystemSettingsMapper systemSettingsMapper;
    
    @Autowired
    private EmployeeService employeeService;
    
    @Override
    public R getSystemSettings() {
        try {
            QueryWrapper<SystemSettings> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("LIMIT 1");
            
            SystemSettings settings = systemSettingsMapper.selectOne(queryWrapper);
            
            if (settings == null) {
                // 如果没有设置记录，创建默认设置
                settings = createDefaultSettings();
                systemSettingsMapper.insert(settings);
            }
            
            return R.ok().addData("settings", settings);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取系统设置失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public R saveBasicSettings(Map<String, Object> settings) {
        try {
            SystemSettings existingSettings = getCurrentSettings();
            
            // 更新基本设置
            existingSettings.setSystemName((String) settings.get("systemName"));
            existingSettings.setSystemVersion((String) settings.get("systemVersion"));
            existingSettings.setLanguage((String) settings.get("language"));
            existingSettings.setTimezone((String) settings.get("timezone"));
            existingSettings.setLogoUrl((String) settings.get("logoUrl"));
            existingSettings.setUpdateTime(LocalDateTime.now());
            
            int result = systemSettingsMapper.updateById(existingSettings);
            
            if (result > 0) {
                R response = R.ok();
                response.setMessage("基本设置保存成功");
                return response;
            } else {
                return R.error("基本设置保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("保存基本设置时发生错误: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public R saveSecuritySettings(Map<String, Object> settings) {
        try {
            SystemSettings existingSettings = getCurrentSettings();
            
            // 更新安全设置
            existingSettings.setPasswordPolicy((String) settings.get("passwordPolicy"));
            existingSettings.setLoginTimeout((Integer) settings.get("loginTimeout"));
            existingSettings.setCaptchaEnabled((Boolean) settings.get("captchaEnabled"));
            existingSettings.setDomainBinding((String) settings.get("domainBinding"));
            existingSettings.setUpdateTime(LocalDateTime.now());
            
            int result = systemSettingsMapper.updateById(existingSettings);
            
            if (result > 0) {
                R response = R.ok();
                response.setMessage("安全设置保存成功");
                return response;
            } else {
                return R.error("安全设置保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("保存安全设置时发生错误: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public R saveBackupSettings(Map<String, Object> settings) {
        try {
            SystemSettings existingSettings = getCurrentSettings();
            
            // 更新备份设置
            existingSettings.setAutoBackup((Boolean) settings.get("autoBackup"));
            existingSettings.setBackupFrequency((String) settings.get("backupFrequency"));
            // 处理备份时间转换
            Object backupTimeObj = settings.get("backupTime");
            if (backupTimeObj != null) {
                if (backupTimeObj instanceof LocalDateTime) {
                    existingSettings.setBackupTime((LocalDateTime) backupTimeObj);
                } else if (backupTimeObj instanceof String) {
                    // 如果是字符串格式，尝试解析
                    String timeStr = (String) backupTimeObj;
                    if (!timeStr.isEmpty()) {
                        try {
                            // 解析时间格式，如 "02:00:00"
                            String[] timeParts = timeStr.split(":");
                            if (timeParts.length == 3) {
                                int hour = Integer.parseInt(timeParts[0]);
                                int minute = Integer.parseInt(timeParts[1]);
                                int second = Integer.parseInt(timeParts[2]);
                                existingSettings.setBackupTime(LocalDateTime.of(
                                    LocalDateTime.now().toLocalDate(), 
                                    java.time.LocalTime.of(hour, minute, second)
                                ));
                            }
                        } catch (Exception e) {
                            System.err.println("时间格式解析失败: " + timeStr);
                            // 设置默认时间
                            existingSettings.setBackupTime(LocalDateTime.of(
                                LocalDateTime.now().toLocalDate(), 
                                java.time.LocalTime.of(2, 0, 0)
                            ));
                        }
                    }
                }
            }
            existingSettings.setRetentionDays((Integer) settings.get("retentionDays"));
            existingSettings.setBackupPath((String) settings.get("backupPath"));
            existingSettings.setUpdateTime(LocalDateTime.now());
            
            int result = systemSettingsMapper.updateById(existingSettings);
            
            if (result > 0) {
                R response = R.ok();
                response.setMessage("备份设置保存成功");
                return response;
            } else {
                return R.error("备份设置保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("保存备份设置时发生错误: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public R changeAdminPassword(Map<String, String> passwordInfo) {
        try {
            String currentPassword = passwordInfo.get("currentPassword");
            String newPassword = passwordInfo.get("newPassword");
            String token = passwordInfo.get("token");
            
            // 从token中获取当前登录用户的employeeId
            Long employeeId = null;
            if (token != null && !token.isEmpty()) {
                try {
                    JwtUtils jwtUtils = new JwtUtils();
                    employeeId = Long.parseLong(jwtUtils.extractUserId(token));
                } catch (Exception e) {
                    return R.error("无效的token，请重新登录");
                }
            } else {
                return R.error("缺少token信息，请重新登录");
            }
            
            // 根据employeeId查询当前登录员工的详细信息
            Employee employee = employeeService.getById(employeeId);
            if (employee == null) {
                return R.error("当前登录员工信息不存在");
            }
            
            // 验证当前登录员工输入的密码是否正确
            if (!currentPassword.equals(employee.getPassword())) {
                return R.error("当前密码不正确");
            }
            
            // 更新当前登录员工的密码
            employee.setPassword(newPassword);
            employee.setUpdateTime(new Date());
            
            // 执行更新操作
            boolean result = employeeService.updateById(employee);
            
            if (result) {
                R response = R.ok();
                response.setMessage("密码修改成功");
                return response;
            } else {
                return R.error("密码修改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("修改密码时发生错误: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public R uploadSSLCertificate(String certificateName, String certificatePath) {
        try {
            SystemSettings existingSettings = getCurrentSettings();
            
            // 更新SSL证书信息
            existingSettings.setSslCertificate(certificateName);
            existingSettings.setSslCertificatePath(certificatePath);
            existingSettings.setUpdateTime(LocalDateTime.now());
            
            int result = systemSettingsMapper.updateById(existingSettings);
            
            if (result > 0) {
                R response = R.ok();
                response.setMessage("密码修改成功");
                return response;
            } else {
                return R.error("SSL证书上传失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("上传SSL证书时发生错误: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public R removeSSLCertificate() {
        try {
            SystemSettings existingSettings = getCurrentSettings();
            
            // 检查SSL证书文件是否存在
            if (existingSettings.getSslCertificatePath() != null) {
                File certFile = new File(existingSettings.getSslCertificatePath());
                if (certFile.exists()) {
                    certFile.delete();
                }
            }
            
            // 清除SSL证书信息
            existingSettings.setSslCertificate(null);
            existingSettings.setSslCertificatePath(null);
            existingSettings.setUpdateTime(LocalDateTime.now());
            
            int result = systemSettingsMapper.updateById(existingSettings);
            
            if (result > 0) {
                R response = R.ok();
                response.setMessage("SSL证书已删除");
                return response;
            } else {
                return R.error("SSL证书删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("删除SSL证书时发生错误: " + e.getMessage());
        }
    }
    
    
    
    @Override
    public R restartSystem() {
        try {
            // 在实际生产环境中，这里应该调用系统重启命令或服务重启API
            // 这里只是模拟重启操作
            R response = R.ok();
            response.setMessage("系统重启指令已发送，系统将在30秒后重启");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("系统重启失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前系统设置
     */
    private SystemSettings getCurrentSettings() {
        QueryWrapper<SystemSettings> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("update_time");
        queryWrapper.last("LIMIT 1");
        
        SystemSettings settings = systemSettingsMapper.selectOne(queryWrapper);
        
        if (settings == null) {
            settings = createDefaultSettings();
            systemSettingsMapper.insert(settings);
        }
        
        return settings;
    }
    
    /**
     * 创建默认系统设置
     */
    private SystemSettings createDefaultSettings() {
        SystemSettings settings = new SystemSettings();
        
        // 基本设置
        settings.setSystemName("订餐管理系统");
        settings.setSystemVersion("v2.0.0");
        settings.setLanguage("zh-CN");
        settings.setTimezone("Asia/Shanghai");
        
        // 安全设置
        settings.setPasswordPolicy("medium");
        settings.setLoginTimeout(30);
        settings.setCaptchaEnabled(true);
        
        // 备份设置
        settings.setAutoBackup(true);
        settings.setBackupFrequency("daily");
        settings.setBackupTime(LocalDateTime.of(2000, 1, 1, 2, 0, 0));
        settings.setRetentionDays(30);
        settings.setBackupPath("/backup");
        
        // 默认管理员账号
        settings.setAdminUsername("admin");
        settings.setAdminPassword("123456");
        
        // 时间戳
        settings.setCreateTime(LocalDateTime.now());
        settings.setUpdateTime(LocalDateTime.now());
        
        return settings;
    }
}