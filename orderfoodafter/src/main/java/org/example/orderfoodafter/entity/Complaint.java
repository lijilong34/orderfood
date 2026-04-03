package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 投诉实体类
 * 用于存储用户的投诉信息，包括投诉内容、处理状态等
 * 
 * @author 李吉隆
 * @date 2026-01-09
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

        /**
     * 获取 getShopId
     * 
     * @return getShopId
     * @author 熊杨博
     */
    public Long getShopId() {
        return shopId;
    }
/**
 * setShopId方法
 *
 * @author 李吉隆
 */

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
/**
 * getUsername方法
 *
 * @author 李吉隆
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
 * getPhonenum方法
 *
 * @author 李吉隆
 */

    public String getPhonenum() {
        return phonenum;
    }
/**
 * setPhonenum方法
 *
 * @author 李吉隆
 */

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

        /**
     * 获取 getOrderNo
     * 
     * @return getOrderNo
     * @author 熊杨博
     */
    public String getOrderNo() {
        return orderNo;
    }
/**
 * setOrderNo方法
 *
 * @author 李吉隆
 */

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    // Getters and Setters
/**
 * getId方法
 *
 * @author 李吉隆
 */
    public Long getId() {
        return id;
    }
/**
 * setId方法
 *
 * @author 李吉隆
 */

    public void setId(Long id) {
        this.id = id;
    }
/**
 * getUserId方法
 *
 * @author 李吉隆
 */

    public Long getUserId() {
        return userId;
    }
/**
 * setUserId方法
 *
 * @author 李吉隆
 */

    public void setUserId(Long userId) {
        this.userId = userId;
    }
/**
 * getOrderId方法
 *
 * @author 李吉隆
 */



    public Long getOrderId() {
        return orderId;
    }
/**
 * setOrderId方法
 *
 * @author 李吉隆
 */

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
/**
 * getComplaintType方法
 *
 * @author 李吉隆
 */

    public Byte getComplaintType() {
        return complaintType;
    }
/**
 * setComplaintType方法
 *
 * @author 李吉隆
 */

    public void setComplaintType(Byte complaintType) {
        this.complaintType = complaintType;
    }
/**
 * getContent方法
 *
 * @author 李吉隆
 */

    public String getContent() {
        return content;
    }
/**
 * setContent方法
 *
 * @author 李吉隆
 */

    public void setContent(String content) {
        this.content = content;
    }
/**
 * getStatus方法
 *
 * @author 李吉隆
 */


    public Byte getStatus() {
        return status;
    }
/**
 * setStatus方法
 *
 * @author 李吉隆
 */

    public void setStatus(Byte status) {
        this.status = status;
    }
/**
 * getCreateTime方法
 *
 * @author 李吉隆
 */


    public Date getCreateTime() {
        return createTime;
    }
/**
 * setCreateTime方法
 *
 * @author 李吉隆
 */

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
/**
 * getUpdateTime方法
 *
 * @author 李吉隆
 */

    public Date getUpdateTime() {
        return updateTime;
    }
/**
 * setUpdateTime方法
 *
 * @author 李吉隆
 */

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}