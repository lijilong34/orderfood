package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 餐桌信息实体类
 * 用于存储餐桌信息，包括餐桌编号、状态、位置等
 * 
 * @author 李吉隆
 * @date 2026-01-23
 */
@TableName(value = "table_info")
public class TableInfo {
    /**
     * 餐桌ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 所属店铺ID
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 桌号（如A01、B02）
     */
    @TableField(value = "table_no")
    private String tableNo;

    /**
     * 容纳人数
     */
    @TableField(value = "capacity")
    private Integer capacity;

    /**
     * 状态（0-空闲 1-已占用 2-已预订 3-待清理）
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
     * 获取餐桌ID
     *
     * @return id - 餐桌ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置餐桌ID
     *
     * @param id 餐桌ID
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
     * 获取桌号（如A01、B02）
     *
     * @return table_no - 桌号（如A01、B02）
     */
    public String getTableNo() {
        return tableNo;
    }

    /**
     * 设置桌号（如A01、B02）
     *
     * @param tableNo 桌号（如A01、B02）
     */
    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

 
    /* * 获取容纳人数
     *
     * @return capacity - 容纳人数
     */
/**
 * getCapacity方法
 *
 * @author 李吉隆
 */
    public Integer getCapacity() {
        return capacity;
    }

    /**
     * 设置容纳人数
     *
     * @param capacity 容纳人数
     */
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * 获取状态（0-空闲 1-已占用 2-已预订 3-待清理）
     *
     * @return status - 状态（0-空闲 1-已占用 2-已预订 3-待清理）
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置状态（0-空闲 1-已占用 2-已预订 3-待清理）
     *
     * @param status 状态（0-空闲 1-已占用 2-已预订 3-待清理）
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
}