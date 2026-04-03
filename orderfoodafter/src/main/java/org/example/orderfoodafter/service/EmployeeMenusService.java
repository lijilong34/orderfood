package org.example.orderfoodafter.service;

import org.example.orderfoodafter.entity.EmployeeMenus;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 员工菜单Service接口
 * 提供员工菜单相关的业务逻辑处理功能，包括员工菜单信息的增删改查等操作
 *
 * @author 李梦瑶
 * @date 2025-12-10
 */
public interface EmployeeMenusService extends IService<EmployeeMenus>{


        /**
     * addEmployeeMenus
     * 
     * @author 李梦瑶
     */
    boolean addEmployeeMenus(int employeeId, List menuIds);
}
