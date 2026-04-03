package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.mapper.EmployeeMenusMapper;
import org.example.orderfoodafter.entity.EmployeeMenus;
import org.example.orderfoodafter.service.EmployeeMenusService;

/**
 * 员工菜单权限Service实现类
 * 实现员工菜单权限相关的业务逻辑处理功能
 * 
 * @author 李吉隆
 * @date 2025-12-14
 */
@Service
public class EmployeeMenusServiceImpl extends ServiceImpl<EmployeeMenusMapper, EmployeeMenus> implements EmployeeMenusService{
/**
 * addEmployeeMenus方法
 *
 * @author 李梦瑶
 */

    @Override
    public boolean addEmployeeMenus(int employeeId, List menuIds) {
        for (Object o : menuIds){
                /**
     * EmployeeMenus
     * 
     * @author 李吉隆
     */
            EmployeeMenus roleMenu = new EmployeeMenus();
            roleMenu.setEmployeeid(Long.parseLong(employeeId+""));
            roleMenu.setMenusid(Long.parseLong(o.toString()));
            this.baseMapper.insert(roleMenu);
        }
        return true;
    }
}
