package org.example.orderfoodafter.service;

import org.example.orderfoodafter.entity.EmployeeMenus;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface EmployeeMenusService extends IService<EmployeeMenus>{


    boolean addEmployeeMenus(int employeeId, List menuIds);
}
