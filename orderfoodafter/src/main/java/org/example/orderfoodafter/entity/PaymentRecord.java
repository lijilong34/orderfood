package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
    * 支付记录表
    */
@TableName(value = "payment_record")
public class PaymentRecord {
    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 订单ID
     */
    @TableField(value = "order_id")
    private Long orderId;

    /**
     * 订单编号
     */
    @TableField(value = "order_no")
    private String orderNo;

    /**
     * 第三方支付流水号
     */
    @TableField(value = "pay_no")
    private String payNo;

    /**
     * 支付金额
     */
    @TableField(value = "pay_amount")
    private BigDecimal payAmount;

    /**
     * 支付方式(1-微信 2-支付宝)
     */
    @TableField(value = "pay_type")
    private Byte payType;

    /**
     * 支付状态(0-未支付 1-支付成功 2-支付失败)
     */
    @TableField(value = "`status`")
    private Byte status;

    /**
     * 支付时间
     */
    @TableField(value = "pay_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;

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
     * 退款状态（0-未退款 1-部分退款 2-全额退款）
     */
    @TableField(value = "refund_status")
    private Byte refundStatus;

    /**
     * 退款金额
     */
    @TableField(value = "refund_amount")
    private BigDecimal refundAmount;

    /**
     * 退款时间
     */
    @TableField(value = "refund_time")
    private Date refundTime;

    /**
     * 退款原因
     */
    @TableField(value = "refund_reason")
    private String refundReason;

    /**
     * 获取记录ID
     *
     * @return id - 记录ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置记录ID
     *
     * @param id 记录ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取订单ID
     *
     * @return order_id - 订单ID
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * 设置订单ID
     *
     * @param orderId 订单ID
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取订单编号
     *
     * @return order_no - 订单编号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置订单编号
     *
     * @param orderNo 订单编号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取第三方支付流水号
     *
     * @return pay_no - 第三方支付流水号
     */
    public String getPayNo() {
        return payNo;
    }

    /**
     * 设置第三方支付流水号
     *
     * @param payNo 第三方支付流水号
     */
    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }

    /**
     * 获取支付金额
     *
     * @return pay_amount - 支付金额
     */
    public BigDecimal getPayAmount() {
        return payAmount;
    }

    /**
     * 设置支付金额
     *
     * @param payAmount 支付金额
     */
    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    /**
     * 获取支付方式(1-微信 2-支付宝)
     *
     * @return pay_type - 支付方式(1-微信 2-支付宝)
     */
    public Byte getPayType() {
        return payType;
    }

    /**
     * 设置支付方式(1-微信 2-支付宝)
     *
     * @param payType 支付方式(1-微信 2-支付宝)
     */
    public void setPayType(Byte payType) {
        this.payType = payType;
    }

    /**
     * 获取支付状态(0-未支付 1-支付成功 2-支付失败)
     *
     * @return status - 支付状态(0-未支付 1-支付成功 2-支付失败)
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置支付状态(0-未支付 1-支付成功 2-支付失败)
     *
     * @param status 支付状态(0-未支付 1-支付成功 2-支付失败)
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取支付时间
     *
     * @return pay_time - 支付时间
     */
    public Date getPayTime() {
        return payTime;
    }

    /**
     * 设置支付时间
     *
     * @param payTime 支付时间
     */
    public void setPayTime(Date payTime) {
        this.payTime = payTime;
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
     * 获取退款状态（0-未退款 1-部分退款 2-全额退款）
     *
     * @return refund_status - 退款状态（0-未退款 1-部分退款 2-全额退款）
     */
    public Byte getRefundStatus() {
        return refundStatus;
    }

    /**
     * 设置退款状态（0-未退款 1-部分退款 2-全额退款）
     *
     * @param refundStatus 退款状态（0-未退款 1-部分退款 2-全额退款）
     */
    public void setRefundStatus(Byte refundStatus) {
        this.refundStatus = refundStatus;
    }

    /**
     * 获取退款金额
     *
     * @return refund_amount - 退款金额
     */
    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    /**
     * 设置退款金额
     *
     * @param refundAmount 退款金额
     */
    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    /**
     * 获取退款时间
     *
     * @return refund_time - 退款时间
     */
    public Date getRefundTime() {
        return refundTime;
    }

    /**
     * 设置退款时间
     *
     * @param refundTime 退款时间
     */
    public void setRefundTime(Date refundTime) {
        this.refundTime = refundTime;
    }

    /**
     * 获取退款原因
     *
     * @return refund_reason - 退款原因
     */
    public String getRefundReason() {
        return refundReason;
    }

    /**
     * 设置退款原因
     *
     * @param refundReason 退款原因
     */
    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }
}