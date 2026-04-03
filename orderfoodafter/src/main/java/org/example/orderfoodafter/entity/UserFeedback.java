package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 用户反馈实体类
 * 用于存储用户的反馈信息，包括反馈内容、类型、处理状态等
 * 
 * @author 李吉隆
 * @date 2025-12-06
 */
@TableName(value = "user_feedback")
public class UserFeedback {
    /**
     * 反馈ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;


    /**
     * 反馈类型（1-投诉建议 2-问题反馈 3-服务评价 4-其他）
     */
    @TableField(value = "type")
    private Byte type;

    /**
     * 反馈内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 处理状态（0-待处理 1-处理中 2-已处理 3-已关闭）
     */
    @TableField(value = "status")
    private Byte status;

    /**
     * 优先级（1-低 2-中 3-高 4-紧急）
     */
    @TableField(value = "priority")
    private Byte priority;


    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 用户名称
     */
    @TableField(exist = false)
    private String username;

    /**
     * 用户手机号
     */
    @TableField(exist = false)
    private String userphone;

        /**
     * 获取 getUsername
     * 
     * @return getUsername
     * @author 李梦瑶
     */
    public String getUsername() {
        return username;
    }
/**
 * setUsername方法
 *
 * @author 李吉隆
 */

    public void setUsername(String username) {
        this.username = username;
    }
/**
 * getUserphone方法
 *
 * @author 李吉隆
 */

    public String getUserphone() {
        return userphone;
    }
/**
 * setUserphone方法
 *
 * @author 李吉隆
 */

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }

    /**
     * 获取反馈ID
     *
     * @return id - 反馈ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置反馈ID
     *
     * @param id 反馈ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户ID
     *
     * @return user_id - 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }


    /**
     * 获取反馈类型（1-投诉建议 2-问题反馈 3-服务评价 4-其他）
     *
     * @return type - 反馈类型
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置反馈类型
     *
     * @param type 反馈类型
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取反馈内容
     *
     * @return content - 反馈内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置反馈内容
     *
     * @param content 反馈内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取处理状态（0-待处理 1-处理中 2-已处理 3-已关闭）
     *
     * @return status - 处理状态
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置处理状态
     *
     * @param status 处理状态
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取优先级（1-低 2-中 3-高 4-紧急）
     *
     * @return priority - 优先级
     */
    public Byte getPriority() {
        return priority;
    }

    /**
     * 设置优先级
     *
     * @param priority 优先级
     */
    public void setPriority(Byte priority) {
        this.priority = priority;
    }


    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取更新时间
     *
     * @return update_time - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}