package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.orderfoodafter.entity.Shop;

import java.util.List;
import java.util.Map;

public interface OrderService extends IService<Order>{


    List<Order> selectbyorderidlist(@Param("shopid") int shopid);

    List<Order> selectorderbyuser(QueryWrapper queryWrapper, String userId);
    List<Order> selectorderbyuserByParams(Map<String, Object> params);

    List<Order> selectOrderByshopid(QueryWrapper queryWrapper);
}
