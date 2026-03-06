package org.example.orderfoodafter.controller;

import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.SystemSettings;
import org.example.orderfoodafter.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 系统设置控制器
 */
@RestController
@RequestMapping("/system-settings")
public class SystemSettingsController {

    @Autowired
    private SystemSettingsService systemSettingsService;
    
    /**
     * 获取系统设置
     */
    @GetMapping("/get")
    public R getSystemSettings() {
        return systemSettingsService.getSystemSettings();
    }
    
    /**
     * 保存基本设置
     */
    @PostMapping("/save-basic")
    public R saveBasicSettings(@RequestBody Map<String, Object> settings) {
        return systemSettingsService.saveBasicSettings(settings);
    }
    
    /**
     * 保存安全设置
     */
    @PostMapping("/save-security")
    public R saveSecuritySettings(@RequestBody Map<String, Object> settings) {
        return systemSettingsService.saveSecuritySettings(settings);
    }
    
    /**
     * 保存备份设置
     */
    @PostMapping("/save-backup")
    public R saveBackupSettings(@RequestBody Map<String, Object> settings) {
        return systemSettingsService.saveBackupSettings(settings);
    }
    
    /**
     * 修改当前登录用户的密码
     */
    @PostMapping("/change-password")
    public R changeAdminPassword(@RequestBody Map<String, String> passwordInfo,
                                 @RequestHeader(value = "Authorization", required = false) String authorization) {
        // 从请求头中获取token
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            passwordInfo.put("token", token);
        }
        return systemSettingsService.changeAdminPassword(passwordInfo);
    }
    
    /**
     * 上传SSL证书
     */
    @PostMapping("/upload-ssl")
    public R uploadSSLCertificate(@RequestParam("file") MultipartFile file,
                                 @RequestParam(value = "uploadPath", required = false) String uploadPath) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return R.error("请选择要上传的SSL证书文件");
            }
            
            // 检查文件类型
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".pem") && !fileName.endsWith(".crt") && !fileName.endsWith(".key"))) {
                return R.error("只支持.pem、.crt、.key格式的SSL证书文件");
            }
            
            // 确定保存路径
            String sslDirPath = uploadPath != null ? uploadPath : "D:/ssl-certificates";
            
            // 确保目录存在
            File sslDir = new File(sslDirPath);
            if (!sslDir.exists()) {
                boolean created = sslDir.mkdirs();
                if (!created) {
                    return R.error("无法创建SSL证书目录，请检查权限");
                }
            }
            
            // 保存文件
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
            String filePath = sslDirPath + File.separator + uniqueFileName;
            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());
            
            // 调用服务保存SSL证书信息
            return systemSettingsService.uploadSSLCertificate(fileName, filePath);
            
        } catch (IOException e) {
            e.printStackTrace();
            return R.error("上传SSL证书时发生IO错误: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("上传SSL证书时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 删除SSL证书
     */
    @DeleteMapping("/remove-ssl")
    public R removeSSLCertificate() {
        return systemSettingsService.removeSSLCertificate();
    }
    
    
    
    /**
     * 重启系统
     */
    @PostMapping("/restart")
    public R restartSystem() {
        return systemSettingsService.restartSystem();
    }
    
    /**
     * 上传系统Logo
     */
    @PostMapping("/upload-logo")
    public R uploadSystemLogo(@RequestParam("file") MultipartFile file,
                              @RequestParam(value = "uploadPath", required = false) String uploadPath) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return R.error("请选择要上传的Logo文件");
            }
            
            // 检查文件类型
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") 
                    && !fileName.endsWith(".png") && !fileName.endsWith(".gif"))) {
                return R.error("只支持.jpg、.jpeg、.png、.gif格式的图片文件");
            }
            
            // 确定保存路径
            String logoDirPath = uploadPath != null ? uploadPath : "D:/system-logos";
            
            // 确保目录存在
            File logoDir = new File(logoDirPath);
            if (!logoDir.exists()) {
                boolean created = logoDir.mkdirs();
                if (!created) {
                    return R.error("无法创建Logo目录，请检查权限");
                }
            }
            
            // 保存文件
            String uniqueFileName = "system_logo_" + UUID.randomUUID().toString() + "_" + fileName;
            String filePath = logoDirPath + File.separator + uniqueFileName;
            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());
            
            // 构造访问URL（实际项目中应该是可访问的URL）
            String logoUrl = "/api/system-logo/" + uniqueFileName;
            
            // 更新系统设置中的Logo URL
            Map<String, Object> logoSetting = new HashMap<>();
            logoSetting.put("logoUrl", logoUrl);
            
            return systemSettingsService.saveBasicSettings(logoSetting);
            
        } catch (IOException e) {
            e.printStackTrace();
            return R.error("上传Logo时发生IO错误: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("上传Logo时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 获取密码强度
     */
    @PostMapping("/password-strength")
    public R calculatePasswordStrength(@RequestBody Map<String, String> request) {
        try {
            String password = request.get("password");
            
            if (password == null || password.isEmpty()) {
                return R.error("密码不能为空");
            }
            
            int strength = 0;
            
            // 长度检测
            if (password.length() >= 8) strength += 25;
            if (password.length() >= 12) strength += 15;
            
            // 字符类型检测
            if (password.matches(".*[a-z].*")) strength += 15;
            if (password.matches(".*[A-Z].*")) strength += 15;
            if (password.matches(".*[0-9].*")) strength += 15;
            if (password.matches(".*[^a-zA-Z0-9].*")) strength += 15;
            
            strength = Math.min(strength, 100);
            
            String strengthText;
            String strengthColor;
            
            if (strength < 40) {
                strengthText = "弱";
                strengthColor = "#f56c6c";
            } else if (strength < 70) {
                strengthText = "中等";
                strengthColor = "#e6a23c";
            } else {
                strengthText = "强";
                strengthColor = "#67c23a";
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("strength", strength);
            result.put("strengthText", strengthText);
            result.put("strengthColor", strengthColor);
            
            return R.ok().addData("passwordStrength", result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("计算密码强度时发生错误: " + e.getMessage());
        }
    }
}