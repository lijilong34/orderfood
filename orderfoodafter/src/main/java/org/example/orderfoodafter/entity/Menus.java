package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

/**
 * 菜单权限实体类
 * 用于存储系统菜单权限信息，实现基于角色的权限控制
 * 
 * @author 李吉隆
 * @date 2025-12-13
 */
@TableName(value = "menus")
public class Menus {
    /**
     * 菜单ID
     */
    @TableId(value = "menu_id", type = IdType.INPUT)
    private Long menuId;

    /**
     * 父菜单ID(0表示根菜单)
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 菜单名称
     */
    @TableField(value = "menu_name")
    private String menuName;

    /**
     * 路由路径
     */
    @TableField(value = "`path`")
    private String path;

    /**
     * 组件路径
     */
    @TableField(value = "component")
    private String component;

    /**
     * 排序
     */
    @TableField(value = "sort")
    private Integer sort;

    /**
     * 是否可见(0-隐藏,1-显示)
     */
    @TableField(value = "visible")
    private Byte visible;

    /**
     * 状态(0-禁用,1-启用)
     */
    @TableField(value = "`status`")
    private Byte status;

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
     * 获取菜单ID
     *
     * @return menu_id - 菜单ID
     */
    public Long getMenuId() {
        return menuId;
    }

    /**
     * 设置菜单ID
     *
     * @param menuId 菜单ID
     */
    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    /**
     * 获取父菜单ID(0表示根菜单)
     *
     * @return parent_id - 父菜单ID(0表示根菜单)
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * 设置父菜单ID(0表示根菜单)
     *
     * @param parentId 父菜单ID(0表示根菜单)
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * 获取菜单名称
     *
     * @return menu_name - 菜单名称
     */
    public String getMenuName() {
        return menuName;
    }

    /**
     * 设置菜单名称
     *
     * @param menuName 菜单名称
     */
    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    /**
     * 获取路由路径
     *
     * @return path - 路由路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置路由路径
     *
     * @param path 路由路径
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取组件路径
     *
     * @return component - 组件路径
     */
    public String getComponent() {
        return component;
    }

    /**
     * 设置组件路径
     *
     * @param component 组件路径
     */
    public void setComponent(String component) {
        this.component = component;
    }

    /**
     * 获取排序
     *
     * @return sort - 排序
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * 设置排序
     *
     * @param sort 排序
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * 获取是否可见(0-隐藏,1-显示)
     *
     * @return visible - 是否可见(0-隐藏,1-显示)
     */
    public Byte getVisible() {
        return visible;
    }

    /**
     * 设置是否可见(0-隐藏,1-显示)
     *
     * @param visible 是否可见(0-隐藏,1-显示)
     */
    public void setVisible(Byte visible) {
        this.visible = visible;
    }

    /**
     * 获取状态(0-禁用,1-启用)
     *
     * @return status - 状态(0-禁用,1-启用)
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置状态(0-禁用,1-启用)
     *
     * @param status 状态(0-禁用,1-启用)
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