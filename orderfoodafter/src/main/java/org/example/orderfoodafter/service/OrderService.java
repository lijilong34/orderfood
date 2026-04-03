package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.orderfoodafter.entity.Shop;

import java.util.List;
import java.util.Map;

/**
 * 订单Service接口
 * 提供订单相关的业务逻辑处理功能，包括订单信息的增删改查等操作
 *
 * @author 李梦瑶
 * @date 2025-11-28
 */
public interface OrderService extends IService<Order>{


    List<Order> selectbyorderidlist(@Param("shopid") int shopid);

    List<Order> selectorderbyuser(QueryWrapper queryWrapper, String userId);
    List<Order> selectorderbyuserByParams(Map<String, Object> params);

    List<Order> selectOrderByshopid(QueryWrapper queryWrapper);
}
