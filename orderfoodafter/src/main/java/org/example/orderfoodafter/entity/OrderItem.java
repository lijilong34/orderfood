package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单项实体类
 * 用于存储订单项信息，记录订单中每个商品的详细信息
 * 
 * @author 李吉隆
 * @date 2025-11-26
 */
@TableName(value = "order_item")
public class OrderItem {
    @TableField(exist = false)
    private Order order;
    
    @TableField(exist = false)
    private Product product;

    /**
     * 订单项ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    @TableField(value = "order_id")
    private Long orderId;

    /**
     * 商品ID
     */
    @TableField(value = "product_id")
    private Long productId;

    /**
     * 商品单价
     */
    @TableField(value = "price")
    private BigDecimal price;

    /**
     * 购买数量
     */
    @TableField(value = "quantity")
    private Integer quantity;

    /**
     * 商品总价
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

        /**
     * 获取 getProductName
     * 
     * @return getProductName
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

    @TableField(exist = false)
    private String productName;
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

    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    // Getter and Setter methods
/**
 * getOrder方法
 *
 * @author 李吉隆
 */
    public Order getOrder() {
        return order;
    }
/**
 * setOrder方法
 *
 * @author 李吉隆
 */

    public void setOrder(Order order) {
        this.order = order;
    }
/**
 * getProduct方法
 *
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
 * getId方法
 *
 * @author 李吉隆
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
 * getOrderId方法
 *
 * @author 李吉隆
 */

    public Long getOrderId() {
        return orderId;
    }
/**
 * setOrderId方法
 *
 * @author 李吉隆
 */

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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
 * getPrice方法
 *
 * @author 李吉隆
 */

    public BigDecimal getPrice() {
        return price;
    }
/**
 * setPrice方法
 *
 * @author 李吉隆
 */

    public void setPrice(BigDecimal price) {
        this.price = price;
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
 * getAmount方法
 *
 * @author 李吉隆
 */

    public BigDecimal getAmount() {
        return amount;
    }
/**
 * setAmount方法
 *
 * @author 李吉隆
 */

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
}