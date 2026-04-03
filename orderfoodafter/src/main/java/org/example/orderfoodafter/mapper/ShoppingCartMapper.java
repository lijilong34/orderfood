package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.ShoppingCart;

import java.util.List;

/**
 * 购物车Mapper接口
 * 用于对购物车表进行数据访问操作，提供购物车信息的增删改查功能
 *
 * @author 赵康乐
 * @date 2025-11-22
 */
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
    List<ShoppingCart> selectshopcartbyuserid(@Param("userid") int userid);
}