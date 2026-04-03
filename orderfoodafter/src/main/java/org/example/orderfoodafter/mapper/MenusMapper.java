package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.Menus;

import java.util.List;

/**
 * 菜单Mapper接口
 * 用于对菜单表进行数据访问操作，提供菜单信息的增删改查功能
 * 
 * @author 李吉隆
 * @date 2025-12-15
 */
public interface MenusMapper extends BaseMapper<Menus> {
    List<Menus> selectMenusbyId(@Param("employeeid") int employeeid);
}