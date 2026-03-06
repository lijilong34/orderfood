package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.Order;
import org.example.orderfoodafter.entity.Shop;

import java.util.List;
import java.util.Map;

public interface OrderMapper extends BaseMapper<Order> {
    List<Order> selectbyorderid(int shopid);

    List<Order> selectorderbyuser(@Param("ew") QueryWrapper queryWrapper, @Param("userId") String userId);
    List<Order> selectorderbyuserByParams(@Param("params") Map<String, Object> params);

    List<Order> selectorderByShopid(@Param("ew") QueryWrapper QueryWrapper);

    List<Order> selectOrderByshopid(@Param("ew") QueryWrapper queryWrapper);
    
    List<Order> selectLatestOrdersByShopId(@Param("shopId") Long shopId);
}
