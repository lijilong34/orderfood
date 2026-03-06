package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;

/**
    * 库存表（餐厅管理员库存管理、厨师库存查看功能）
    */
@TableName(value = "inventory")
public class Inventory {
    /**
     * 库存ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 所属店铺ID
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 食材名称
     */
    @TableField(value = "material_name")
    private String materialName;

    /**
     * 食材规格（如500g/袋）
     */
    @TableField(value = "spec")
    private String spec;

    /**
     * 库存数量
     */
    @TableField(value = "stock_quantity")
    private BigDecimal stockQuantity;

    /**
     * 库存预警阈值
     */
    @TableField(value = "warning_threshold")
    private BigDecimal warningThreshold;

    /**
     * 供应商ID（关联供应商表）
     */
    @TableField(value = "supplier_id")
    private Long supplierId;

    /**
     * 最后采购时间
     */
    @TableField(value = "last_purchase_time")
    private Date lastPurchaseTime;

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
     * 获取库存ID
     *
     * @return id - 库存ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置库存ID
     *
     * @param id 库存ID
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
     * 获取食材名称
     *
     * @return material_name - 食材名称
     */
    public String getMaterialName() {
        return materialName;
    }

    /**
     * 设置食材名称
     *
     * @param materialName 食材名称
     */
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    /**
     * 获取食材规格（如500g/袋）
     *
     * @return spec - 食材规格（如500g/袋）
     */
    public String getSpec() {
        return spec;
    }

    /**
     * 设置食材规格（如500g/袋）
     *
     * @param spec 食材规格（如500g/袋）
     */
    public void setSpec(String spec) {
        this.spec = spec;
    }

    /**
     * 获取库存数量
     *
     * @return stock_quantity - 库存数量
     */
    public BigDecimal getStockQuantity() {
        return stockQuantity;
    }

    /**
     * 设置库存数量
     *
     * @param stockQuantity 库存数量
     */
    public void setStockQuantity(BigDecimal stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    /**
     * 获取库存预警阈值
     *
     * @return warning_threshold - 库存预警阈值
     */
    public BigDecimal getWarningThreshold() {
        return warningThreshold;
    }

    /**
     * 设置库存预警阈值
     *
     * @param warningThreshold 库存预警阈值
     */
    public void setWarningThreshold(BigDecimal warningThreshold) {
        this.warningThreshold = warningThreshold;
    }

    /**
     * 获取供应商ID（关联供应商表）
     *
     * @return supplier_id - 供应商ID（关联供应商表）
     */
    public Long getSupplierId() {
        return supplierId;
    }

    /**
     * 设置供应商ID（关联供应商表）
     *
     * @param supplierId 供应商ID（关联供应商表）
     */
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    /**
     * 获取最后采购时间
     *
     * @return last_purchase_time - 最后采购时间
     */
    public Date getLastPurchaseTime() {
        return lastPurchaseTime;
    }

    /**
     * 设置最后采购时间
     *
     * @param lastPurchaseTime 最后采购时间
     */
    public void setLastPurchaseTime(Date lastPurchaseTime) {
        this.lastPurchaseTime = lastPurchaseTime;
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