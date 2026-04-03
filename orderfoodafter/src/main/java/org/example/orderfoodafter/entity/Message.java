package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 消息通知实体类
 * 用于存储系统消息通知信息，包括消息内容、接收者、发送时间等
 * 
 * @author 李吉隆
 * @date 2025-12-16
 */
@TableName(value = "message")
public class Message {
    /**
     * 消息ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 接收用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 消息类型（1-系统通知 2-订单提醒 3-优惠活动）
     */
    @TableField(value = "type")
    private Byte type;

    /**
     * 消息标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 消息内容
     */
    @TableField(value = "content")
    private String content;


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
     * 获取 getId
     * 
     * @return getId
     * @author 李梦瑶
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
 * getUserId方法
 *
 * @author 李吉隆
 */

    public Long getUserId() {
        return userId;
    }
/**
 * setUserId方法
 *
 * @author 李吉隆
 */

    public void setUserId(Long userId) {
        this.userId = userId;
    }
/**
 * getType方法
 *
 * @author 李吉隆
 */

    public Byte getType() {
        return type;
    }
/**
 * setType方法
 *
 * @author 李吉隆
 */

    public void setType(Byte type) {
        this.type = type;
    }
/**
 * getTitle方法
 *
 * @author 李吉隆
 */

    public String getTitle() {
        return title;
    }
/**
 * setTitle方法
 *
 * @author 李吉隆
 */

    public void setTitle(String title) {
        this.title = title;
    }
/**
 * getContent方法
 *
 * @author 李吉隆
 */

    public String getContent() {
        return content;
    }
/**
 * setContent方法
 *
 * @author 李吉隆
 */

    public void setContent(String content) {
        this.content = content;
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