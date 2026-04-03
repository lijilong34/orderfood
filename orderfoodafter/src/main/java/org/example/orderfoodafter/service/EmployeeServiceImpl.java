package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.mapper.EmployeeMapper;
import org.example.orderfoodafter.entity.Employee;
import org.example.orderfoodafter.service.EmployeeService;

/**
 * 员工Service实现类
 * 实现员工相关的业务逻辑处理功能
 *
 * @author 李梦瑶
 * @date 2025-12-09
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{
/**
 * selectemployee方法
 *
 * @author 李梦瑶
 */

    @Override
    public List<Employee> selectemployee(QueryWrapper queryWrapper) {
        List<Employee> employeeList=baseMapper.selectemployee(queryWrapper);
        return employeeList;
    }
}
