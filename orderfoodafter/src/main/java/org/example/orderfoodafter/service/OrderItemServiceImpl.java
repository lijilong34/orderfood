package org.example.orderfoodafter.service;


import org.example.orderfoodafter.entity.OrderItem;
import org.example.orderfoodafter.mapper.OrderItemMapper;

import org.example.orderfoodafter.entity.Shop;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 订单项Service实现类
 * 实现订单项相关的业务逻辑处理功能
 *
 * @author 李梦瑶
 * @date 2025-11-29
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService{
/**
 * selectorderitembyorderid方法
 *
 * @author 李吉隆
 */

    @Override
    public List<OrderItem> selectorderitembyorderid(int orderid) {
        List<OrderItem> orderItemList=baseMapper.selectorderitembyorderid(orderid);
        return orderItemList;
    }
/**
 * selectbyorderitemid方法
 *
 * @author 李吉隆
 */

    @Override
    public List<OrderItem> selectbyorderitemid(int shopid) {
        List<OrderItem> orderitem=baseMapper.selectbyorderitembyid(shopid);
        return orderitem;
    }
/**
 * selectbyuserid方法
 *
 * @author 李吉隆
 */

    @Override
    public List<OrderItem> selectbyuserid(int userid) {
        List<OrderItem> orderitem=baseMapper.selectbyuserid(userid);
        return orderitem;
    }
/**
 * selectOrderBylist方法
 *
 * @author 李吉隆
 */

    @Override
    public List<OrderItem> selectOrderBylist(int id, int oid, String name, String phone) {
        List<OrderItem> orderitemList=baseMapper.selectOrderItembylist(id,oid,name,phone);
        return orderitemList;
    }
}
