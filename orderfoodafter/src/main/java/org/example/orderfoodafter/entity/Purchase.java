package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购记录实体类
 * 用于存储采购记录信息，包括采购商品、数量、价格、供应商等
 * 
 * @author 李吉隆
 * @date 2026-01-11
 */
@TableName(value = "purchase")
public class Purchase {
    /**
     * 采购记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 店铺ID
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 供应商ID
     */
    @TableField(value = "supplier_id")
    private Long supplierId;

    /**
     * 产品ID
     */
    @TableField(value = "product_id")
    private Long productId;

    /**
     * 采购数量
     */
    @TableField(value = "quantity")
    private Integer quantity;

    /**
     * 采购单价
     */
    @TableField(value = "unit_price")
    private BigDecimal unitPrice;

    /**
     * 总金额
     */
    @TableField(value = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 采购日期
     */
    @TableField(value = "purchase_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date purchaseDate;

    /**
     * 采购状态（0-待确认 1-已完成 2-已取消）
     */
    @TableField(value = "`status`")
    private Byte status;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

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

    // 添加产品名称和供应商名称字段
    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String supplierName;

    // getter和setter方法
        /**
     * 获取 getId
     * 
     * @return getId
     * @author 陈逸磊
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
 * getShopId方法
 *
 * @author 李吉隆
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
 * getSupplierId方法
 *
 * @author 李吉隆
 */

    public Long getSupplierId() {
        return supplierId;
    }
/**
 * setSupplierId方法
 *
 * @author 李吉隆
 */

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
/**
 * getProductId方法
 *
 * @author 李吉隆
 */

    public Long getProductId() {
        return productId;
    }
/**
 * setProductId方法
 *
 * @author 李吉隆
 */

    public void setProductId(Long productId) {
        this.productId = productId;
    }
/**
 * getQuantity方法
 *
 * @author 李吉隆
 */

    public Integer getQuantity() {
        return quantity;
    }
/**
 * setQuantity方法
 *
 * @author 李吉隆
 */

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
/**
 * getUnitPrice方法
 *
 * @author 李吉隆
 */

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
/**
 * setUnitPrice方法
 *
 * @author 李吉隆
 */

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
/**
 * getTotalAmount方法
 *
 * @author 李吉隆
 */

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
/**
 * setTotalAmount方法
 *
 * @author 李吉隆
 */

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
/**
 * getPurchaseDate方法
 *
 * @author 李吉隆
 */

    public Date getPurchaseDate() {
        return purchaseDate;
    }
/**
 * setPurchaseDate方法
 *
 * @author 李吉隆
 */

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
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
 * getRemark方法
 *
 * @author 李吉隆
 */

    public String getRemark() {
        return remark;
    }
/**
 * setRemark方法
 *
 * @author 李吉隆
 */

    public void setRemark(String remark) {
        this.remark = remark;
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
/**
 * getProductName方法
 *
 * @author 李吉隆
 */

    public String getProductName() {
        return productName;
    }
/**
 * setProductName方法
 *
 * @author 李吉隆
 */

    public void setProductName(String productName) {
        this.productName = productName;
    }
/**
 * getSupplierName方法
 *
 * @author 李吉隆
 */

    public String getSupplierName() {
        return supplierName;
    }
/**
 * setSupplierName方法
 *
 * @author 李吉隆
 */

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}
