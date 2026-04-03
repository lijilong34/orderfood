package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 评价实体类
 * 用于存储用户的评价信息，包括对商品、服务、环境的评价内容
 * 
 * @author 李吉隆
 * @date 2026-01-06
 */
@TableName(value = "evaluation")
public class Evaluation {
    /**
     * 评价ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 评价用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

        /**
     * 获取 getNickname
     * 
     * @return getNickname
     * @author 李梦瑶
     */
    public String getNickname() {
        return nickname;
    }
/**
 * setNickname方法
 *
 * @author 李吉隆
 */

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 昵称
     */
    @TableField(exist = false)
    private String nickname;
    /**
     * 所属店铺ID
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 关联订单ID
     */
    @TableField(value = "order_id")
    private Long orderId;

    /**
     * 菜品评分（1-5分）
     */
    @TableField(value = "dish_score")
    private Byte dishScore;

    /**
     * 服务评分（1-5分）
     */
    @TableField(value = "service_score")
    private Byte serviceScore;

    /**
     * 环境评分（1-5分）
     */
    @TableField(value = "environment_score")
    private Byte environmentScore;

    /**
     * 评价内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 评价图片URL（逗号分隔）
     */
    @TableField(value = "image_urls")
    private String imageUrls;

 /*   *//**
     * 商家回复内容
     *//*
    @TableField(value = "reply_content")
    private String replyContent;*/
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
     * 商品名称
     * @return
     */
    @TableField(exist = false)
    private String productname;

        /**
     * 获取 getProductname
     * 
     * @return getProductname
     * @author 李梦瑶
     */
    public String getProductname() {
        return productname;
    }
/**
 * setProductname方法
 *
 * @author 李吉隆
 */

    public void setProductname(String productname) {
        this.productname = productname;
    }
/**
 * getAvatar方法
 *
 * @author 李吉隆
 */

    public String getAvatar() {
        return avatar;
    }
/**
 * setAvatar方法
 *
 * @author 李吉隆
 */

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @TableField(exist = false)
    private String avatar;

    /**
     * 店铺名称
     */
    @TableField(exist = false)
    private String shopName;

        /**
     * 获取 getShopName
     * 
     * @return getShopName
     * @author 李梦瑶
     */
    public String getShopName() {
        return shopName;
    }
/**
 * setShopName方法
 *
 * @author 李吉隆
 */

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    /**
     * 获取评价ID
     *
     * @return id - 评价ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置评价ID
     *
     * @param id 评价ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取评价用户ID
     *
     * @return user_id - 评价用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置评价用户ID
     *
     * @param userId 评价用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
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
     * 获取关联订单ID
     *
     * @return order_id - 关联订单ID
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * 设置关联订单ID
     *
     * @param orderId 关联订单ID
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取菜品评分（1-5分）
     *
     * @return dish_score - 菜品评分（1-5分）
     */
    public Byte getDishScore() {
        return dishScore;
    }

    /**
     * 设置菜品评分（1-5分）
     *
     * @param dishScore 菜品评分（1-5分）
     */
    public void setDishScore(Byte dishScore) {
        this.dishScore = dishScore;
    }

    /**
     * 获取服务评分（1-5分）
     *
     * @return service_score - 服务评分（1-5分）
     */
    public Byte getServiceScore() {
        return serviceScore;
    }

    /**
     * 设置服务评分（1-5分）
     *
     * @param serviceScore 服务评分（1-5分）
     */
    public void setServiceScore(Byte serviceScore) {
        this.serviceScore = serviceScore;
    }

    /**
     * 获取环境评分（1-5分）
     *
     * @return environment_score - 环境评分（1-5分）
     */
    public Byte getEnvironmentScore() {
        return environmentScore;
    }

    /**
     * 设置环境评分（1-5分）
     *
     * @param environmentScore 环境评分（1-5分）
     */
    public void setEnvironmentScore(Byte environmentScore) {
        this.environmentScore = environmentScore;
    }

    /**
     * 获取评价内容
     *
     * @return content - 评价内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置评价内容
     *
     * @param content 评价内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取评价图片URL（逗号分隔）
     *
     * @return image_urls - 评价图片URL（逗号分隔）
     */
    public String getImageUrls() {
        return imageUrls;
    }

    /**
     * 设置评价图片URL（逗号分隔）
     *
     * @param imageUrls 评价图片URL（逗号分隔）
     */
    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }



    /**
     * 设置商家回复内容
     *
     * @param replyContent 商家回复内容
     **/

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