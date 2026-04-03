package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.Order;
import org.example.orderfoodafter.entity.Shop;

import java.util.List;
import java.util.Map;

/**
 * 订单Mapper接口
 * 用于对订单表进行数据访问操作，提供订单信息的增删改查功能
 *
 * @author 李梦瑶
 * @date 2025-11-30
 */
public interface OrderMapper extends BaseMapper<Order> {
    List<Order> selectbyorderid(int shopid);

    List<Order> selectorderbyuser(@Param("ew") QueryWrapper queryWrapper, @Param("userId") String userId);
    List<Order> selectorderbyuserByParams(@Param("params") Map<String, Object> params);

    List<Order> selectorderByShopid(@Param("ew") QueryWrapper QueryWrapper);

    List<Order> selectOrderByshopid(@Param("ew") QueryWrapper queryWrapper);
    
    List<Order> selectLatestOrdersByShopId(@Param("shopId") Long shopId);
}
