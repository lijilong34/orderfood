package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 财务报表实体类
 * 用于存储财务报表统计数据，包括营业额、订单数、利润等财务指标
 * 
 * @author 李吉隆
 * @date 2026-01-16
 */
@TableName(value = "financial_report")
public class FinancialReport {
    /**
     * 报表ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 报表日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date reportDate;

    /**
     * 报表类型(1-日报 2-月报 3-季报 4-年报)
     */
    private Byte reportType;

    /**
     * 总订单数
     */
    private Integer totalOrders;

    /**
     * 已完成订单数
     */
    private Integer completedOrders;

    /**
     * 已取消订单数
     */
    private Integer cancelledOrders;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 实付金额
     */
    private BigDecimal payAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 微信支付金额
     */
    private BigDecimal wechatPayAmount;

    /**
     * 支付宝支付金额
     */
    private BigDecimal alipayAmount;

    /**
     * 余额支付金额
     */
    private BigDecimal balancePayAmount;

    /**
     * 平均订单金额
     */
    private BigDecimal avgOrderAmount;

    /**
     * 总客户数
     */
    private Integer totalCustomers;

    /**
     * 新增客户数
     */
    private Integer newCustomers;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    // Getter and Setter methods

        /**
     * 获取 getId
     * 
     * @return getId
     * @author 熊杨博
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
 * getShopName方法
 *
 * @author 李吉隆
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
 * getReportDate方法
 *
 * @author 李吉隆
 */

    public Date getReportDate() {
        return reportDate;
    }
/**
 * setReportDate方法
 *
 * @author 李吉隆
 */

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }
/**
 * getReportType方法
 *
 * @author 李吉隆
 */

    public Byte getReportType() {
        return reportType;
    }
/**
 * setReportType方法
 *
 * @author 李吉隆
 */

    public void setReportType(Byte reportType) {
        this.reportType = reportType;
    }
/**
 * getTotalOrders方法
 *
 * @author 李吉隆
 */

    public Integer getTotalOrders() {
        return totalOrders;
    }
/**
 * setTotalOrders方法
 *
 * @author 李吉隆
 */

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }
/**
 * getCompletedOrders方法
 *
 * @author 李吉隆
 */

    public Integer getCompletedOrders() {
        return completedOrders;
    }
/**
 * setCompletedOrders方法
 *
 * @author 李吉隆
 */

    public void setCompletedOrders(Integer completedOrders) {
        this.completedOrders = completedOrders;
    }
/**
 * getCancelledOrders方法
 *
 * @author 李吉隆
 */

    public Integer getCancelledOrders() {
        return cancelledOrders;
    }
/**
 * setCancelledOrders方法
 *
 * @author 李吉隆
 */

    public void setCancelledOrders(Integer cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
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
 * getPayAmount方法
 *
 * @author 李吉隆
 */

    public BigDecimal getPayAmount() {
        return payAmount;
    }
/**
 * setPayAmount方法
 *
 * @author 李吉隆
 */

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }
/**
 * getDiscountAmount方法
 *
 * @author 李吉隆
 */

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
/**
 * setDiscountAmount方法
 *
 * @author 李吉隆
 */

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
/**
 * getWechatPayAmount方法
 *
 * @author 李吉隆
 */

    public BigDecimal getWechatPayAmount() {
        return wechatPayAmount;
    }
/**
 * setWechatPayAmount方法
 *
 * @author 李吉隆
 */

    public void setWechatPayAmount(BigDecimal wechatPayAmount) {
        this.wechatPayAmount = wechatPayAmount;
    }
/**
 * getAlipayAmount方法
 *
 * @author 李吉隆
 */

    public BigDecimal getAlipayAmount() {
        return alipayAmount;
    }
/**
 * setAlipayAmount方法
 *
 * @author 李吉隆
 */

    public void setAlipayAmount(BigDecimal alipayAmount) {
        this.alipayAmount = alipayAmount;
    }
/**
 * getBalancePayAmount方法
 *
 * @author 李吉隆
 */

    public BigDecimal getBalancePayAmount() {
        return balancePayAmount;
    }
/**
 * setBalancePayAmount方法
 *
 * @author 李吉隆
 */

    public void setBalancePayAmount(BigDecimal balancePayAmount) {
        this.balancePayAmount = balancePayAmount;
    }
/**
 * getAvgOrderAmount方法
 *
 * @author 李吉隆
 */

    public BigDecimal getAvgOrderAmount() {
        return avgOrderAmount;
    }
/**
 * setAvgOrderAmount方法
 *
 * @author 李吉隆
 */

    public void setAvgOrderAmount(BigDecimal avgOrderAmount) {
        this.avgOrderAmount = avgOrderAmount;
    }
/**
 * getTotalCustomers方法
 *
 * @author 李吉隆
 */

    public Integer getTotalCustomers() {
        return totalCustomers;
    }
/**
 * setTotalCustomers方法
 *
 * @author 李吉隆
 */

    public void setTotalCustomers(Integer totalCustomers) {
        this.totalCustomers = totalCustomers;
    }
/**
 * getNewCustomers方法
 *
 * @author 李吉隆
 */

    public Integer getNewCustomers() {
        return newCustomers;
    }
/**
 * setNewCustomers方法
 *
 * @author 李吉隆
 */

    public void setNewCustomers(Integer newCustomers) {
        this.newCustomers = newCustomers;
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
}