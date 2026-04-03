package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 商品分类实体类
 * 用于存储商品分类信息，实现商品的分类管理
 * 
 * @author 李吉隆
 * @date 2026-01-04
 */
@TableName(value = "product_category")
public class ProductCategory {
    /**
     * 分类ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 所属店铺ID
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 分类名称
     */
    @TableField(value = "`name`")
    private String name;

    /**
     * 排序(值越小越靠前)
     */
    @TableField(value = "sort")
    private Integer sort;

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
    @TableField(exist = false)
    private String shopname;
    /**
     * 获取分类ID
     *
     * @return id - 分类ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置分类ID
     *
     * @param id 分类ID
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
     * 获取分类名称
     *
     * @return name - 分类名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置分类名称
     *
     * @param name 分类名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取排序(值越小越靠前)
     *
     * @return sort - 排序(值越小越靠前)
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * 设置排序(值越小越靠前)
     *
     * @param sort 排序(值越小越靠前)
     */
    public void setSort(Integer sort) {
        this.sort = sort;
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
 * getShopname方法
 *
 * @author 李吉隆
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
}