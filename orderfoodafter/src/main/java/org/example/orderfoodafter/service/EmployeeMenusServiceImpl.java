package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.mapper.EmployeeMenusMapper;
import org.example.orderfoodafter.entity.EmployeeMenus;
import org.example.orderfoodafter.service.EmployeeMenusService;
@Service
public class EmployeeMenusServiceImpl extends ServiceImpl<EmployeeMenusMapper, EmployeeMenus> implements EmployeeMenusService{

    @Override
    public boolean addEmployeeMenus(int employeeId, List menuIds) {
        for (Object o : menuIds){
            EmployeeMenus roleMenu = new EmployeeMenus();
            roleMenu.setEmployeeid(Long.parseLong(employeeId+""));
            roleMenu.setMenusid(Long.parseLong(o.toString()));
            this.baseMapper.insert(roleMenu);
        }
        return true;
    }
}
