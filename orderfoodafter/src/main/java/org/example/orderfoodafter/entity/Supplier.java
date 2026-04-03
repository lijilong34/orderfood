package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 供应商实体类
 * 用于存储供应商信息，包括供应商名称、联系方式、地址等
 * 
 * @author 李吉隆
 * @date 2026-01-19
 */
@TableName(value = "supplier")
public class Supplier {
    /**
     * 供应商ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 所属店铺ID
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 供应商名称
     */
    @TableField(value = "`name`")
    private String name;

    /**
     * 联系人
     */
    @TableField(value = "contact_person")
    private String contactPerson;

    /**
     * 联系电话
     */
    @TableField(value = "contact_phone")
    private String contactPhone;

    /**
     * 供应商地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 合作状态（0-终止 1-正常）
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
    private Date updateTime;

    /**
     * 获取供应商ID
     *
     * @return id - 供应商ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置供应商ID
     *
     * @param id 供应商ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取所属店铺ID
     *
     * @return shop_id - 所属店铺ID
     */
    public Long getShopId() {
        return shopId;
    }

    /**
     * 设置所属店铺ID
     *
     * @param shopId 所属店铺ID
     */
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    /**
     * 获取供应商名称
     *
     * @return name - 供应商名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置供应商名称
     *
     * @param name 供应商名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取联系人
     *
     * @return contact_person - 联系人
     */
    public String getContactPerson() {
        return contactPerson;
    }

    /**
     * 设置联系人
     *
     * @param contactPerson 联系人
     */
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    /**
     * 获取联系电话
     *
     * @return contact_phone - 联系电话
     */
    public String getContactPhone() {
        return contactPhone;
    }

    /**
     * 设置联系电话
     *
     * @param contactPhone 联系电话
     */
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    /**
     * 获取供应商地址
     *
     * @return address - 供应商地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置供应商地址
     *
     * @param address 供应商地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取合作状态（0-终止 1-正常）
     *
     * @return status - 合作状态（0-终止 1-正常）
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置合作状态（0-终止 1-正常）
     *
     * @param status 合作状态（0-终止 1-正常）
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