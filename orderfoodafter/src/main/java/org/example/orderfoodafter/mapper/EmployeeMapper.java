package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.Employee;

import java.util.List;

/**
 * 员工Mapper接口
 * 用于对员工表进行数据访问操作，提供员工信息的增删改查功能
 *
 * @author 李梦瑶
 * @date 2025-12-08
 */
public interface EmployeeMapper extends BaseMapper<Employee> {
    List<Employee> selectemployee(@Param("ew") QueryWrapper queryWrapper);
}