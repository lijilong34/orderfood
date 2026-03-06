package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 投诉表
 */
@TableName(value = "complaint")
public class Complaint {

    /**
     * 投诉ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 投诉用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 店铺ID
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 关联订单ID
     */
    @TableField(value = "order_id")
    private Long orderId;

    /**
     * 投诉类型：1-菜品问题 2-服务问题 3-配送问题 4-其他
     */
    @TableField(value = "complaint_type")
    private Byte complaintType;

    /**
     * 投诉内容
     */
    @TableField(value = "content")
    private String content;


    /**
     * 状态：0-待处理 1-已处理 2-已关闭
     */
    @TableField(value = "status")
    private Byte status;



    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "update_time")
    private Date updateTime;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    /**
     * 用户名
     */
    @TableField(exist = false)
    private String username;

    /**
     *手机号
     */
    @TableField(exist = false)
    private String phonenum;
    /**
     * 订单编号
     */
    @TableField(exist = false)
    private String orderNo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }



    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Byte getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(Byte complaintType) {
        this.complaintType = complaintType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}