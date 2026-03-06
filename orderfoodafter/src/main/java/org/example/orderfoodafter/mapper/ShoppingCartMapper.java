package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
    List<ShoppingCart> selectshopcartbyuserid(@Param("userid") int userid);
}