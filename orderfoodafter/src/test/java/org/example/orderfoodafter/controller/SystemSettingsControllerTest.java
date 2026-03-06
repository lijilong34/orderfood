package org.example.orderfoodafter.controller;

import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.service.SystemSettingsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

/**
 * 系统设置控制器测试 - 使用Mockito模拟服务层
 */
@ExtendWith(MockitoExtension.class)
public class SystemSettingsControllerTest {

    @Mock
    private SystemSettingsService systemSettingsService;

    @InjectMocks
    private SystemSettingsController systemSettingsController;

    @Test
    public void testGetSystemSettings() {
        R mockResult = R.ok().addData("settings", new HashMap<>());
        when(systemSettingsService.getSystemSettings()).thenReturn(mockResult);
        
        R result = systemSettingsController.getSystemSettings();
        assertEquals(200, result.getCode());
        System.out.println("获取系统设置测试通过");
    }

    @Test
    public void testSaveBasicSettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("systemName", "测试系统");
        settings.put("language", "zh-CN");
        settings.put("timezone", "Asia/Shanghai");
        
        R mockResult = R.ok();
        mockResult.setMessage("保存成功");
        when(systemSettingsService.saveBasicSettings(anyMap())).thenReturn(mockResult);
        
        R result = systemSettingsController.saveBasicSettings(settings);
        assertEquals(200, result.getCode());
        System.out.println("保存基本设置测试通过");
    }

    @Test
    public void testSaveSecuritySettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("passwordPolicy", "medium");
        settings.put("loginTimeout", 30);
        settings.put("captchaEnabled", true);
        settings.put("domainBinding", "example.com");
        
        R mockResult = R.ok();
        mockResult.setMessage("保存成功");
        when(systemSettingsService.saveSecuritySettings(anyMap())).thenReturn(mockResult);
        
        R result = systemSettingsController.saveSecuritySettings(settings);
        assertEquals(200, result.getCode());
        System.out.println("保存安全设置测试通过");
    }

    @Test
    public void testSaveBackupSettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("autoBackup", true);
        settings.put("backupFrequency", "daily");
        settings.put("retentionDays", 30);
        settings.put("backupPath", "/backup");
        
        R mockResult = R.ok();
        mockResult.setMessage("保存成功");
        when(systemSettingsService.saveBackupSettings(anyMap())).thenReturn(mockResult);
        
        R result = systemSettingsController.saveBackupSettings(settings);
        assertEquals(200, result.getCode());
        System.out.println("保存备份设置测试通过");
    }

    @Test
    public void testChangeAdminPassword() {
        Map<String, String> passwordInfo = new HashMap<>();
        passwordInfo.put("currentPassword", "123456");
        passwordInfo.put("newPassword", "newpassword123");
        passwordInfo.put("token", "test-token-123");
        
        R mockResult = R.ok();
        mockResult.setMessage("密码修改成功");
        when(systemSettingsService.changeAdminPassword(anyMap())).thenReturn(mockResult);
        
        R result = systemSettingsController.changeAdminPassword(passwordInfo, "Bearer test-token-123");
        assertEquals(200, result.getCode());
        System.out.println("修改管理员密码测试通过");
    }

    @Test
    public void testRestartSystem() {
        R mockResult = R.ok();
        mockResult.setMessage("系统将在30秒后重启");
        when(systemSettingsService.restartSystem()).thenReturn(mockResult);
        
        R result = systemSettingsController.restartSystem();
        assertEquals(200, result.getCode());
        System.out.println("重启系统测试通过");
    }
}