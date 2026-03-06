package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.mapper.EmployeeMapper;
import org.example.orderfoodafter.entity.Employee;
import org.example.orderfoodafter.service.EmployeeService;
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{

    @Override
    public List<Employee> selectemployee(QueryWrapper queryWrapper) {
        List<Employee> employeeList=baseMapper.selectemployee(queryWrapper);
        return employeeList;
    }
}
