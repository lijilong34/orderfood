package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 员工菜单权限实体类
 * 用于存储员工的菜单权限关联信息，实现权限管理
 * 
 * @author 李吉隆
 * @date 2025-12-11
 */
@TableName(value = "employee_menus")
public class EmployeeMenus {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 员工id
     */
    @TableField(value = "employeeid")
    private Long employeeid;

    /**
     * 菜单id
     */
    @TableField(value = "menusid")
    private Long menusid;

    /**
     * 获取id
     *
     * @return id - id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置id
     *
     * @param id id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取员工id
     *
     * @return employeeid - 员工id
     */
    public Long getEmployeeid() {
        return employeeid;
    }

    /**
     * 设置员工id
     *
     * @param employeeid 员工id
     */
    public void setEmployeeid(Long employeeid) {
        this.employeeid = employeeid;
    }

    /**
     * 获取菜单id
     *
     * @return menusid - 菜单id
     */
    public Long getMenusid() {
        return menusid;
    }

    /**
     * 设置菜单id
     *
     * @param menusid 菜单id
     */
    public void setMenusid(Long menusid) {
        this.menusid = menusid;
    }
}