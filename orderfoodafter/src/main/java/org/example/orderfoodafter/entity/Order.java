package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
    * 订单表
    */
@TableName(value = "`order`")
public class Order {
    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号(唯一)
     */
    @TableField(value = "order_no")
    private String orderNo;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 店铺ID
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 订单总金额
     */
    @TableField(value = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 实付金额
     */
    @TableField(value = "pay_amount")
    private BigDecimal payAmount;

    /**
     * 优惠金额
     */
    @TableField(value = "discount_amount")
    private BigDecimal discountAmount;

    /**
     * 支付方式(1-微信 2-支付宝)
     */
    @TableField(value = "pay_type")
    private Byte payType;

    /**
     * 订单状态(0-待支付 1-已支付 2-已接单 3-配送中 4-已完成 5-已取消)
     */
    @TableField(value = "`status`")
    private int status;

    /**
     * 收货地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 收货人电话
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 收货人姓名
     */
    @TableField(value = "receiver")
    private String receiver;

    /**
     * 支付时间
     */
    @TableField(value = "pay_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;

    /**
     * 完成时间
     */
    @TableField(value = "finish_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date finishTime;

    /**
     * 取消时间
     */
    @TableField(value = "cancel_time")
    private Date cancelTime;

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
     * 关联餐桌ID（堂食场景）
     */
    @TableField(value = "table_id")
    private Long tableId;

    /**
     * 顾客特殊要求（如少辣、多醋）
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 订单类型（1-堂食 2-外卖）
     */
    @TableField(value = "order_type")
    private Byte orderType;

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    @TableField(exist = false)
    private String tableNo;

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    /**
     * 商店名称
     */
    @TableField(exist = false)
    private String shopname;
    /**
     * 获取订单ID
     *
     * @return id - 订单ID
     */
    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @TableField(exist = false)
    private String nickname;
    /**
     * 设置订单ID
     *
     * @param id 订单ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取订单编号(唯一)
     *
     * @return order_no - 订单编号(唯一)
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置订单编号(唯一)
     *
     * @param orderNo 订单编号(唯一)
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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
     * 获取店铺ID
     *
     * @return shop_id - 店铺ID
     */
    public Long getShopId() {
        return shopId;
    }

    /**
     * 设置店铺ID
     *
     * @param shopId 店铺ID
     */
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    /**
     * 获取订单总金额
     *
     * @return total_amount - 订单总金额
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * 设置订单总金额
     *
     * @param totalAmount 订单总金额
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * 获取实付金额
     *
     * @return pay_amount - 实付金额
     */
    public BigDecimal getPayAmount() {
        return payAmount;
    }

    /**
     * 设置实付金额
     *
     * @param payAmount 实付金额
     */
    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    /**
     * 获取优惠金额
     *
     * @return discount_amount - 优惠金额
     */
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    /**
     * 设置优惠金额
     *
     * @param discountAmount 优惠金额
     */
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
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
     * 获取订单状态(0-待支付 1-已支付 2-已接单 3-配送中 4-已完成 5-已取消)
     *
     * @return status - 订单状态(0-待支付 1-已支付 2-已接单 3-配送中 4-已完成 5-已取消)
     */
    public int getStatus() {
        return status;
    }

    /**
     * 设置订单状态(0-待支付 1-已支付 2-已接单 3-配送中 4-已完成 5-已取消)
     *
     * @param status 订单状态(0-待支付 1-已支付 2-已接单 3-配送中 4-已完成 5-已取消)
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 获取收货地址
     *
     * @return address - 收货地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置收货地址
     *
     * @param address 收货地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取收货人电话
     *
     * @return phone - 收货人电话
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置收货人电话
     *
     * @param phone 收货人电话
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取收货人姓名
     *
     * @return receiver - 收货人姓名
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * 设置收货人姓名
     *
     * @param receiver 收货人姓名
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
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
     * 获取完成时间
     *
     * @return finish_time - 完成时间
     */
    public Date getFinishTime() {
        return finishTime;
    }

    /**
     * 设置完成时间
     *
     * @param finishTime 完成时间
     */
    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    /**
     * 获取取消时间
     *
     * @return cancel_time - 取消时间
     */
    public Date getCancelTime() {
        return cancelTime;
    }

    /**
     * 设置取消时间
     *
     * @param cancelTime 取消时间
     */
    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
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
     * 获取关联餐桌ID（堂食场景）
     *
     * @return table_id - 关联餐桌ID（堂食场景）
     */
    public Long getTableId() {
        return tableId;
    }

    /**
     * 设置关联餐桌ID（堂食场景）
     *
     * @param tableId 关联餐桌ID（堂食场景）
     */
    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    /**
     * 获取顾客特殊要求（如少辣、多醋）
     *
     * @return remark - 顾客特殊要求（如少辣、多醋）
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置顾客特殊要求（如少辣、多醋）
     *
     * @param remark 顾客特殊要求（如少辣、多醋）
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取订单类型（1-堂食 2-外卖）
     *
     * @return order_type - 订单类型（1-堂食 2-外卖）
     */
    public Byte getOrderType() {
        return orderType;
    }

    /**
     * 设置订单类型（1-堂食 2-外卖）
     *
     * @param orderType 订单类型（1-堂食 2-外卖）
     */
    public void setOrderType(Byte orderType) {
        this.orderType = orderType;
    }
}