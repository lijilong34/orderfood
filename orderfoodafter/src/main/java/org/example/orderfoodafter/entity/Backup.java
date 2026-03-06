package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据库备份实体类
 */
@Data
@TableName("backup")
public class Backup {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 备份文件名称
     */
    @TableField("backup_file_name")
    private String backupFileName;
    
    /**
     * 备份文件路径
     */
    @TableField("backup_file_path")
    private String backupFilePath;
    
    /**
     * 备份文件大小（字节）
     */
    @TableField("backup_size")
    private Long backupSize;
    
    /**
     * 备份时间
     */
    @TableField("backup_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime backupTime;
    
    /**
     * 备份类型（manual:手动，auto:自动）
     */
    @TableField("backup_type")
    private String backupType;
    
    /**
     * 备份状态（success:成功，failed:失败，processing:进行中）
     */
    @TableField("backup_status")
    private String backupStatus;
    
    /**
     * 备份描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 操作人ID
     */
    @TableField("operator_id")
    private Long operatorId;
    
    /**
     * 操作人姓名
     */
    @TableField("operator_name")
    private String operatorName;
    
    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}