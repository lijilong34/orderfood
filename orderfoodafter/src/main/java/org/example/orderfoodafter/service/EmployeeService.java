package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.entity.Employee;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface EmployeeService extends IService<Employee>{
     List<Employee> selectemployee(QueryWrapper queryWrapper);
}
