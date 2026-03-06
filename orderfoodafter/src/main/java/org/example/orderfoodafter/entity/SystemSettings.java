package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统设置实体类
 */
@Data
@TableName("system_settings")
public class SystemSettings {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    // 基本设置
    private String systemName;
    private String systemVersion;
    private String language;
    private String timezone;
    private String logoUrl;
    
    // 安全设置
    private String passwordPolicy;
    private Integer loginTimeout;
    private String sslCertificate;
    private String sslCertificatePath;
    private Boolean captchaEnabled;
    private String domainBinding;
    
    // 备份设置
    private Boolean autoBackup;
    private String backupFrequency;
    private LocalDateTime backupTime;
    private Integer retentionDays;
    private String backupPath;
    
    
    
    // 时间戳
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 管理员密码修改相关
    private String adminUsername;
    private String adminPassword;
    private String passwordSalt;
    private LocalDateTime passwordUpdateTime;
}