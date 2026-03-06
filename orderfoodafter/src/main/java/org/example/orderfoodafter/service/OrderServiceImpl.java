package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.Order;
import org.example.orderfoodafter.mapper.OrderMapper;
import org.example.orderfoodafter.service.OrderService;
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService{

    @Override
    public List<Order> selectbyorderidlist(int shopid) {
        List<Order> orderList=baseMapper.selectbyorderid(shopid);
        return orderList;
    }

    @Override
    public List<Order> selectorderbyuser(QueryWrapper queryWrapper, String userId) {
        List<Order> orderList=baseMapper.selectorderbyuser(queryWrapper, userId);
        return orderList;
    }

    @Override
    public List<Order> selectorderbyuserByParams(Map<String, Object> params) {
        return baseMapper.selectorderbyuserByParams(params);
    }

    @Override
    public List<Order> selectOrderByshopid(QueryWrapper queryWrapper) {
        List<Order> orderList=baseMapper.selectOrderByshopid(queryWrapper);
        return orderList;
    }
}
