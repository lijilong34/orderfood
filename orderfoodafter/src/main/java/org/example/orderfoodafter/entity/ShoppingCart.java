package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 购物车实体类
 * 用于存储用户的购物车信息，记录用户想要购买的商品和数量
 * 
 * @author 李吉隆
 * @date 2025-11-23
 */
@TableName(value = "shopping_cart")
public class ShoppingCart {
    /**
     * 购物车ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

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
     * 商品ID
     */
    @TableField(value = "product_id")
    private Long productId;

    /**
     * 商品数量
     */
    @TableField(value = "quantity")
    private Integer quantity;

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
     * 获取 getProduct
     * 
     * @return getProduct
     * @author 李吉隆
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
     * 关联商品实体类
     */
    @TableField(exist = false)
    private Product product;
    /**
     * 获取购物车ID
     *
     * @return id - 购物车ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置购物车ID
     *
     * @param id 购物车ID
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
     * 获取商品ID
     *
     * @return product_id - 商品ID
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * 设置商品ID
     *
     * @param productId 商品ID
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * 获取商品数量
     *
     * @return quantity - 商品数量
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * 设置商品数量
     *
     * @param quantity 商品数量
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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