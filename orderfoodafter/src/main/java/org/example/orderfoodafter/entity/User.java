package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * 用户表
 */
@TableName(value = "`user`")
public class User {
    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 手机号(登录账号)
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 密码(加密存储)
     */
    @TableField(value = "`password`")
    private String password;

    /**
     * 昵称
     */
    @TableField(value = "nickname")
    private String nickname;

    /**
     * 头像URL
     */
    @TableField(value = "avatar")
    private String avatar;

    /**
     * 性别(0-未知 1-男 2-女)
     */
    @TableField(value = "gender")
    private Byte gender;

    /**
     * 状态(0-禁用 1-正常)
     */
    @TableField(value = "`status`")
    private Byte status;

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
     * 账户余额
     */
    @TableField(value = "balance")
    private java.math.BigDecimal balance;

    /**
     * 成长值
     */
    @TableField(value = "growth_value")
    private Integer growthValue;

    /**
     * 获取用户ID
     *
     * @return id - 用户ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置用户ID
     *
     * @param id 用户ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取手机号(登录账号)
     *
     * @return phone - 手机号(登录账号)
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置手机号(登录账号)
     *
     * @param phone 手机号(登录账号)
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取密码(加密存储)
     *
     * @return password - 密码(加密存储)
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码(加密存储)
     *
     * @param password 密码(加密存储)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取昵称
     *
     * @return nickname - 昵称
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置昵称
     *
     * @param nickname 昵称
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 获取头像URL
     *
     * @return avatar - 头像URL
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 设置头像URL
     *
     * @param avatar 头像URL
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 获取性别(0-未知 1-男 2-女)
     *
     * @return gender - 性别(0-未知 1-男 2-女)
     */
    public Byte getGender() {
        return gender;
    }

    /**
     * 设置性别(0-未知 1-男 2-女)
     *
     * @param gender 性别(0-未知 1-男 2-女)
     */
    public void setGender(Byte gender) {
        this.gender = gender;
    }

    /**
     * 获取状态(0-禁用 1-正常)
     *
     * @return status - 状态(0-禁用 1-正常)
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置状态(0-禁用 1-正常)
     *
     * @param status 状态(0-禁用 1-正常)
     */
    public void setStatus(Byte status) {
        this.status = status;
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

    /**
     * 获取账户余额
     *
     * @return balance - 账户余额
     */
    public java.math.BigDecimal getBalance() {
        return balance;
    }

    /**
     * 设置账户余额
     *
     * @param balance 账户余额
     */
    public void setBalance(java.math.BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * 获取成长值
     *
     * @return growth_value - 成长值
     */
    public Integer getGrowthValue() {
        return growthValue;
    }

    /**
     * 设置成长值
     *
     * @param growthValue 成长值
     */
    public void setGrowthValue(Integer growthValue) {
        this.growthValue = growthValue;
    }
}