package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

/**
 * 店铺实体类
 * 用于存储店铺信息，包括店铺名称、地址、联系方式、状态等
 * 
 * @author 李吉隆
 * @date 2025-12-31
 */
@TableName(value = "shop")
public class Shop {
    /**
     * 店铺ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;
    @TableField(exist = false)
    private Product product;
    @TableField(exist = false)
    private User user;

        /**
     * 获取 getProduct
     * 
     * @return getProduct
     * @author 李梦瑶
     */
    public Product getProduct() {
        return product;
    }
/**
 * setProduct方法
 *
 * @author 李吉隆
 */

    public void setProduct(Product product) {
        this.product = product;
    }
/**
 * getUser方法
 *
 * @author 李吉隆
 */

    public User getUser() {
        return user;
    }
/**
 * setUser方法
 *
 * @author 李吉隆
 */

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 店铺名称
     */
    @TableField(value = "`name`")
    private String name;

    /**
     * 店铺logo
     */
    @TableField(value = "logo")
    private String logo;

    /**
     * 店铺地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 联系电话
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 营业时间
     */
    @TableField(value = "business_hours")
    private String businessHours;

    /**
     * 状态(0-休息 1-营业 2-暂停营业)
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
     * 经营品类（如中餐、西餐、火锅）
     */
    @TableField(value = "business_scope")
    private String businessScope;

    /**
     * 停车信息（如免费停车2小时）
     */
    @TableField(value = "parking_info")
    private String parkingInfo;

    /**
     * 店铺介绍（展示给顾客）
     */
    @TableField(value = "introduction")
    private String introduction;
    /**
 * 订单数量
 */
@TableField(exist = false)
private int order_cnt;

    public List<ProductCategory> getProductCategory() {
        return productCategory;
    }
/**
 * setProductCategory方法
 *
 * @author 李吉隆
 */

    public void setProductCategory(List<ProductCategory> productCategory) {
        this.productCategory = productCategory;
    }

    /**
 * 引用分类集合
 */
@TableField(exist = false)
private List<ProductCategory> productCategory;

/**
 * 销量前5的商品集合
 */
@TableField(exist = false)
private List<Product> topProducts;

public List<Product> getTopProducts() {
    return topProducts;
}
/**
 * setTopProducts方法
 *
 * @author 李吉隆
 */

public void setTopProducts(List<Product> topProducts) {
    this.topProducts = topProducts;
}

    /**
     * 获取店铺ID
     *
     * @return id - 店铺ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置店铺ID
     *
     * @param id 店铺ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取店铺名称
     *
     * @return name - 店铺名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置店铺名称
     *
     * @param name 店铺名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取店铺logo
     *
     * @return logo - 店铺logo
     */
    public String getLogo() {
        return logo;
    }

    /**
     * 设置店铺logo
     *
     * @param logo 店铺logo
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }

    /**
     * 获取店铺地址
     *
     * @return address - 店铺地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置店铺地址
     *
     * @param address 店铺地址
     */
    public void setAddress(String address) {
        this.address = address;
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
     * 获取营业时间
     *
     * @return business_hours - 营业时间
     */
    public String getBusinessHours() {
        return businessHours;
    }

    /**
     * 设置营业时间
     *
     * @param businessHours 营业时间
     */
    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }

    /**
     * 获取状态(0-休息 1-营业 2-暂停营业)
     *
     * @return status - 状态(0-休息 1-营业 2-暂停营业)
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置状态(0-休息 1-营业 2-暂停营业)
     *
     * @param status 状态(0-休息 1-营业 2-暂停营业)
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
     * 获取经营品类（如中餐、西餐、火锅）
     *
     * @return business_scope - 经营品类（如中餐、西餐、火锅）
     */
    public String getBusinessScope() {
        return businessScope;
    }

    /**
     * 设置经营品类（如中餐、西餐、火锅）
     *
     * @param businessScope 经营品类（如中餐、西餐、火锅）
     */
    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

    /**
     * 获取停车信息（如免费停车2小时）
     *
     * @return parking_info - 停车信息（如免费停车2小时）
     */
    public String getParkingInfo() {
        return parkingInfo;
    }

    /**
     * 设置停车信息（如免费停车2小时）
     *
     * @param parkingInfo 停车信息（如免费停车2小时）
     */
    public void setParkingInfo(String parkingInfo) {
        this.parkingInfo = parkingInfo;
    }

    /**
     * 获取店铺介绍（展示给顾客）
     *
     * @return introduction - 店铺介绍（展示给顾客）
     */
    public String getIntroduction() {
        return introduction;
    }

    /**
     * 设置店铺介绍（展示给顾客）
     *
     * @param introduction 店铺介绍（展示给顾客）
     */
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
/**
 * getOrder_cnt方法
 *
 * @author 李吉隆
 */
    public int getOrder_cnt() {
        return order_cnt;
    }
/**
 * setOrder_cnt方法
 *
 * @author 李吉隆
 */

    public void setOrder_cnt(int order_cnt) {
        this.order_cnt = order_cnt;
    }
}