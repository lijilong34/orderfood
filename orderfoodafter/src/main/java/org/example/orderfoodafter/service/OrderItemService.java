package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.extension.service.IService;

import org.example.orderfoodafter.entity.OrderItem;

import org.example.orderfoodafter.entity.Shop;

import java.util.List;

public interface OrderItemService extends IService<OrderItem>{


    List<OrderItem> selectorderitembyorderid(int orderid);

    List<OrderItem> selectbyorderitemid(int shopid);

    List<OrderItem> selectbyuserid(int userid);

    List<OrderItem> selectOrderBylist(int id, int oid, String name, String phone);
}
