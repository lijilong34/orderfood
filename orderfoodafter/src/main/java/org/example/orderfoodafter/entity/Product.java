package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
    * 商品表
    */
@TableName(value = "product")
public class Product {
    /**
     * 商品ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 所属店铺ID
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 所属分类ID
     */
    @TableField(value = "category_id")
    private Long categoryId;
    /**
     * 商品名字
     */
    @TableField(exist = false)
    private String shopname;

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }
    @TableField(exist = false)
    private String categoryname;

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    /**
     * 商品名称
     */
    @TableField(value = "`name`")
    private String name;

    /**
     * 商品图片
     */
    @TableField(value = "image")
    private String image;

    /**
     * 商品价格
     */
    @TableField(value = "price")
    private BigDecimal price;

    /**
     * 商品描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 库存数量(0表示无限库存)
     */
    @TableField(value = "stock")
    private Integer stock;

    /**
     * 销售数量
     */
    @TableField(value = "sales")
    private Integer sales;

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    /**
     * 状态(0-下架 1-上架)
     */
    @TableField(value = "status")
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
     * 菜品制作规范（给厨师查看）
     */
    @TableField(value = "production_spec")
    private String productionSpec;

    /**
     * 是否推荐菜品（0-否 1-是）
     */
    @TableField(value = "is_recommend")
    private Byte isRecommend;

    /**
     * 获取商品ID
     *
     * @return id - 商品ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置商品ID
     *
     * @param id 商品ID
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
     * 获取所属分类ID
     *
     * @return category_id - 所属分类ID
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * 设置所属分类ID
     *
     * @param categoryId 所属分类ID
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * 获取商品名称
     *
     * @return name - 商品名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置商品名称
     *
     * @param name 商品名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取商品图片
     *
     * @return image - 商品图片
     */
    public String getImage() {
        return image;
    }

    /**
     * 设置商品图片
     *
     * @param image 商品图片
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * 获取商品价格
     *
     * @return price - 商品价格
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置商品价格
     *
     * @param price 商品价格
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取商品描述
     *
     * @return description - 商品描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置商品描述
     *
     * @param description 商品描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取库存数量(0表示无限库存)
     *
     * @return stock - 库存数量(0表示无限库存)
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * 设置库存数量(0表示无限库存)
     *
     * @param stock 库存数量(0表示无限库存)
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }




    /**
     * 获取状态(0-下架 1-上架)
     *
     * @return status - 状态(0-下架 1-上架)
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置状态(0-下架 1-上架)
     *
     * @param status 状态(0-下架 1-上架)
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
     * 获取菜品制作规范（给厨师查看）
     *
     * @return production_spec - 菜品制作规范（给厨师查看）
     */
    public String getProductionSpec() {
        return productionSpec;
    }

    /**
     * 设置菜品制作规范（给厨师查看）
     *
     * @param productionSpec 菜品制作规范（给厨师查看）
     */
    public void setProductionSpec(String productionSpec) {
        this.productionSpec = productionSpec;
    }

    /**
     * 获取是否推荐菜品（0-否 1-是）
     *
     * @return is_recommend - 是否推荐菜品（0-否 1-是）
     */
    public Byte getIsRecommend() {
        return isRecommend;
    }

    /**
     * 设置是否推荐菜品（0-否 1-是）
     *
     * @param isRecommend 是否推荐菜品（0-否 1-是）
     */
    public void setIsRecommend(Byte isRecommend) {
        this.isRecommend = isRecommend;
    }

    /**
     * 商品详情图片路径1
     */
    @TableField(exist = false)
    private String imagePath1;

    /**
     * 商品详情图片路径2
     */
    @TableField(exist = false)
    private String imagePath2;

    /**
     * 商品详情图片路径3
     */
    @TableField(exist = false)
    private String imagePath3;

    /**
     * 获取商品详情图片路径1
     *
     * @return imagePath1 - 商品详情图片路径1
     */
    public String getImagePath1() {
        return imagePath1;
    }

    /**
     * 设置商品详情图片路径1
     *
     * @param imagePath1 商品详情图片路径1
     */
    public void setImagePath1(String imagePath1) {
        this.imagePath1 = imagePath1;
    }

    /**
     * 获取商品详情图片路径2
     *
     * @return imagePath2 - 商品详情图片路径2
     */
    public String getImagePath2() {
        return imagePath2;
    }

    /**
     * 设置商品详情图片路径2
     *
     * @param imagePath2 商品详情图片路径2
     */
    public void setImagePath2(String imagePath2) {
        this.imagePath2 = imagePath2;
    }

    /**
     * 获取商品详情图片路径3
     *
     * @return imagePath3 - 商品详情图片路径3
     */
    public String getImagePath3() {
        return imagePath3;
    }

    /**
     * 设置商品详情图片路径3
     *
     * @param imagePath3 商品详情图片路径3
     */
    public void setImagePath3(String imagePath3) {
        this.imagePath3 = imagePath3;
    }
}