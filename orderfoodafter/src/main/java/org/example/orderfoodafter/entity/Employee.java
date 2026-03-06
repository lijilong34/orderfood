package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

/**
 * 员工信息表（餐厅管理员员工管理功能）
 */
@TableName(value = "employee")
public class Employee {
    /**
     * 员工ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 所属店铺ID
     */
    @TableField(value = "shop_id")
    private Long shopId;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    @TableField(exist = false)
    private String shopName;
    /**
     * 登录用户名
     */
    @TableField(value = "username")
    private String username;


    /**
     * 登录密码(加密存储)
     */
    @TableField(value = "`password`")
    private String password;

    /**
     * 岗位（服务员/厨师/收银员）
     */
    @TableField(value = "post")
    private String post;

    /**
     * 工作状态（0-休假 1-在岗）
     */
    @TableField(value = "work_status")
    private Byte workStatus;

    /**
     * 排班信息（如周一至周五 10:00-20:00）
     */
    @TableField(value = "schedule")
    private String schedule;

    /**
     * 考勤次数
     */
    @TableField(value = "attendance_count")
    private Integer attendanceCount;

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
     * 获取员工ID
     *
     * @return id - 员工ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置员工ID
     *
     * @param id 员工ID
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
     * 获取登录用户名
     *
     * @return username - 登录用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置登录用户名
     *
     * @param username 登录用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取登录密码(加密存储)
     *
     * @return password - 登录密码(加密存储)
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置登录密码(加密存储)
     *
     * @param password 登录密码(加密存储)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取岗位（服务员/厨师/收银员）
     *
     * @return post - 岗位（服务员/厨师/收银员）
     */
    public String getPost() {
        return post;
    }

    /**
     * 设置岗位（服务员/厨师/收银员）
     *
     * @param post 岗位（服务员/厨师/收银员）
     */
    public void setPost(String post) {
        this.post = post;
    }

    /**
     * 获取工作状态（0-休假 1-在岗）
     *
     * @return work_status - 工作状态（0-休假 1-在岗）
     */
    public Byte getWorkStatus() {
        return workStatus;
    }

    /**
     * 设置工作状态（0-休假 1-在岗）
     *
     * @param workStatus 工作状态（0-休假 1-在岗）
     */
    public void setWorkStatus(Byte workStatus) {
        this.workStatus = workStatus;
    }

    /**
     * 获取排班信息（如周一至周五 10:00-20:00）
     *
     * @return schedule - 排班信息（如周一至周五 10:00-20:00）
     */
    public String getSchedule() {
        return schedule;
    }

    /**
     * 设置排班信息（如周一至周五 10:00-20:00）
     *
     * @param schedule 排班信息（如周一至周五 10:00-20:00）
     */
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    /**
     * 获取考勤次数
     *
     * @return attendance_count - 考勤次数
     */
    public Integer getAttendanceCount() {
        return attendanceCount;
    }

    /**
     * 设置考勤次数
     *
     * @param attendanceCount 考勤次数
     */
    public void setAttendanceCount(Integer attendanceCount) {
        this.attendanceCount = attendanceCount;
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