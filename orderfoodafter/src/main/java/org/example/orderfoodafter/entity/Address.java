package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 地址实体类
 * 用于存储用户的收货地址信息，包括收货人、联系电话、详细地址等
 * 
 * @author 李吉隆
 * @date 2025-11-15
 */
@TableName(value = "address")
public class Address {
    /**
     * 地址ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 收货人
     */
    @TableField(value = "receiver")
    private String receiver;

    /**
     * 联系电话
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 城市
     */
    @TableField(value = "city")
    private String city;

    /**
     * 区/县
     */
    @TableField(value = "district")
    private String district;

    /**
     * 详细地址
     */
    @TableField(value = "detail_address")
    private String detailAddress;

    /**
     * 是否默认地址(0-否 1-是)
     */
    @TableField(value = "is_default")
    private Byte isDefault;

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
     * 获取地址ID
     *
     * @return id - 地址ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置地址ID
     *
     * @param id 地址ID
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
     * 获取收货人
     *
     * @return receiver - 收货人
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * 设置收货人
     *
     * @param receiver 收货人
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * 获取联系电话
     *
     * @return phone - 联系电话
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置联系电话
     *
     * @param phone 联系电话
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }


    /**
     * 获取城市
     *
     * @return city - 城市
     */
    public String getCity() {
        return city;
    }

    /**
     * 设置城市
     *
     * @param city 城市
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 获取区/县
     *
     * @return district - 区/县
     */
    public String getDistrict() {
        return district;
    }

    /**
     * 设置区/县
     *
     * @param district 区/县
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * 获取详细地址
     *
     * @return detail_address - 详细地址
     */
    public String getDetailAddress() {
        return detailAddress;
    }

    /**
     * 设置详细地址
     *
     * @param detailAddress 详细地址
     */
    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    /**
     * 获取是否默认地址(0-否 1-是)
     *
     * @return is_default - 是否默认地址(0-否 1-是)
     */
    public Byte getIsDefault() {
        return isDefault;
    }

    /**
     * 设置是否默认地址(0-否 1-是)
     *
     * @param isDefault 是否默认地址(0-否 1-是)
     */
    public void setIsDefault(Byte isDefault) {
        this.isDefault = isDefault;
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