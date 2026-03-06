package org.example.orderfoodafter.service;


import org.example.orderfoodafter.entity.OrderItem;
import org.example.orderfoodafter.mapper.OrderItemMapper;

import org.example.orderfoodafter.entity.Shop;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService{

    @Override
    public List<OrderItem> selectorderitembyorderid(int orderid) {
        List<OrderItem> orderItemList=baseMapper.selectorderitembyorderid(orderid);
        return orderItemList;
    }

    @Override
    public List<OrderItem> selectbyorderitemid(int shopid) {
        List<OrderItem> orderitem=baseMapper.selectbyorderitembyid(shopid);
        return orderitem;
    }

    @Override
    public List<OrderItem> selectbyuserid(int userid) {
        List<OrderItem> orderitem=baseMapper.selectbyuserid(userid);
        return orderitem;
    }

    @Override
    public List<OrderItem> selectOrderBylist(int id, int oid, String name, String phone) {
        List<OrderItem> orderitemList=baseMapper.selectOrderItembylist(id,oid,name,phone);
        return orderitemList;
    }
}
