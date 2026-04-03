package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

/**
 * 浏览历史实体类
 * 用于存储用户的浏览历史记录，记录用户浏览过的商品信息
 * 
 * @author 李吉隆
 * @date 2025-11-25
 */
@TableName(value = "browse_history")
public class BrowseHistory {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 菜品ID
     */
    @TableField(value = "product_id")
    private Long productId;

        /**
     * 获取 getShopname
     * 
     * @return getShopname
     * @author 李梦瑶
     */
    public String getShopname() {
        return shopname;
    }
/**
 * setShopname方法
 *
 * @author 李吉隆
 */

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    @TableField(exist = false)
    private String shopname;
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
    @TableField(exist = false)
    private Product product;

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
     * 获取主键ID
     *
     * @return id - 主键ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
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
     * 获取菜品ID
     *
     * @return product_id - 菜品ID
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * 设置菜品ID
     *
     * @param productId 菜品ID
     */
    public void setProductId(Long productId) {
        this.productId = productId;
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