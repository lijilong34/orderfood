package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.Menus;

import java.util.List;

public interface MenusMapper extends BaseMapper<Menus> {
    List<Menus> selectMenusbyId(@Param("employeeid") int employeeid);
}