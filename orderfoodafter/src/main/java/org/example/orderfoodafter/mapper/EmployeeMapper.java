package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.Employee;

import java.util.List;

public interface EmployeeMapper extends BaseMapper<Employee> {
    List<Employee> selectemployee(@Param("ew") QueryWrapper queryWrapper);
}