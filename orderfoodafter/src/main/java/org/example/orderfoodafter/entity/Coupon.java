package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券实体类
 * 用于存储优惠券信息，包括优惠券类型、金额、使用条件、有效期等
 * 
 * @author 李吉隆
 * @date 2025-11-26
 */
@TableName(value = "coupon")
public class Coupon {
    /**
     * 优惠券ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 所属用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 优惠券模板ID（用于关联原始模板）
     */
    @TableField(exist = false)
    private Long templateId;

    /**
     * 适用店铺ID(0表示全店通用)
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 优惠券标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 优惠类型(1-满减 2-折扣)
     */
    @TableField(value = "discount_type")
    private Byte discountType;

    /**
     * 优惠值(满减金额/折扣比例)
     */
    @TableField(value = "discount_value")
    private BigDecimal discountValue;

    /**
     * 最低使用金额
     */
    @TableField(value = "min_amount")
    private BigDecimal minAmount;

    /**
     * 生效时间
     */
    @TableField(value = "start_time")
    private Date startTime;

    /**
     * 过期时间
     */
    @TableField(value = "end_time")
    private Date endTime;

    /**
     * 状态(0-已使用 1-未使用 2-已过期)
     */
    @TableField(value = "`status`")
    private Byte status;

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
     * 获取优惠券ID
     *
     * @return id - 优惠券ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置优惠券ID
     *
     * @param id 优惠券ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取所属用户ID
     *
     * @return user_id - 所属用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置所属用户ID
     *
     * @param userId 所属用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取优惠券模板ID（用于关联原始模板）
     *
     * @return template_id - 优惠券模板ID（用于关联原始模板）
     */
    public Long getTemplateId() {
        return templateId;
    }

    /**
     * 设置优惠券模板ID（用于关联原始模板）
     *
     * @param templateId 优惠券模板ID（用于关联原始模板）
     */
    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    /**
     * 获取适用店铺ID(0表示全店通用)
     *
     * @return shop_id - 适用店铺ID(0表示全店通用)
     */
    public Long getShopId() {
        return shopId;
    }

    /**
     * 设置适用店铺ID(0表示全店通用)
     *
     * @param shopId 适用店铺ID(0表示全店通用)
     */
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    /**
     * 获取优惠券标题
     *
     * @return title - 优惠券标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置优惠券标题
     *
     * @param title 优惠券标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取优惠类型(1-满减 2-折扣)
     *
     * @return discount_type - 优惠类型(1-满减 2-折扣)
     */
    public Byte getDiscountType() {
        return discountType;
    }

    /**
     * 设置优惠类型(1-满减 2-折扣)
     *
     * @param discountType 优惠类型(1-满减 2-折扣)
     */
    public void setDiscountType(Byte discountType) {
        this.discountType = discountType;
    }

    /**
     * 获取优惠值(满减金额/折扣比例)
     *
     * @return discount_value - 优惠值(满减金额/折扣比例)
     */
    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    /**
     * 设置优惠值(满减金额/折扣比例)
     *
     * @param discountValue 优惠值(满减金额/折扣比例)
     */
    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    /**
     * 获取最低使用金额
     *
     * @return min_amount - 最低使用金额
     */
    public BigDecimal getMinAmount() {
        return minAmount;
    }

    /**
     * 设置最低使用金额
     *
     * @param minAmount 最低使用金额
     */
    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    /**
     * 获取生效时间
     *
     * @return start_time - 生效时间
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * 设置生效时间
     *
     * @param startTime 生效时间
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取过期时间
     *
     * @return end_time - 过期时间
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 设置过期时间
     *
     * @param endTime 过期时间
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取状态(0-已使用 1-未使用 2-已过期)
     *
     * @return status - 状态(0-已使用 1-未使用 2-已过期)
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置状态(0-已使用 1-未使用 2-已过期)
     *
     * @param status 状态(0-已使用 1-未使用 2-已过期)
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
}