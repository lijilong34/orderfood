package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

/**
    * 会员信息表（餐厅管理员会员管理、顾客会员权益功能）
    */
@TableName(value = "`member`")
public class Member {
    /**
     * 会员ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 关联用户表ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 会员等级（普通/银卡/金卡）
     */
    @TableField(value = "`level`")
    private String level;

    /**
     * 会员积分
     */
    @TableField(value = "points")
    private Integer points;

    /**
     * 成长值
     */
    @TableField(value = "growth_value")
    private Integer growthValue;

    /**
     * 会员有效期（NULL表示永久）
     */
    @TableField(value = "expire_time")
    private Date expireTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 获取会员ID
     *
     * @return id - 会员ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置会员ID
     *
     * @param id 会员ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取关联用户表ID
     *
     * @return user_id - 关联用户表ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置关联用户表ID
     *
     * @param userId 关联用户表ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取会员等级（普通/银卡/金卡）
     *
     * @return level - 会员等级（普通/银卡/金卡）
     */
    public String getLevel() {
        return level;
    }

    /**
     * 设置会员等级（普通/银卡/金卡）
     *
     * @param level 会员等级（普通/银卡/金卡）
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * 获取会员积分
     *
     * @return points - 会员积分
     */
    public Integer getPoints() {
        return points;
    }

    /**
     * 设置会员积分
     *
     * @param points 会员积分
     */
    public void setPoints(Integer points) {
        this.points = points;
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

    /**
     * 获取会员有效期（NULL表示永久）
     *
     * @return expire_time - 会员有效期（NULL表示永久）
     */
    public Date getExpireTime() {
        return expireTime;
    }

    /**
     * 设置会员有效期（NULL表示永久）
     *
     * @param expireTime 会员有效期（NULL表示永久）
     */
    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
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