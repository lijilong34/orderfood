package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.entity.Employee;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 员工Service接口
 * 提供员工相关的业务逻辑处理功能，包括员工信息的增删改查等操作
 *
 * @author 李梦瑶
 * @date 2025-12-08
 */
public interface EmployeeService extends IService<Employee>{
     List<Employee> selectemployee(QueryWrapper queryWrapper);
}
