package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.extension.service.IService;

import org.example.orderfoodafter.entity.OrderItem;

import org.example.orderfoodafter.entity.Shop;

import java.util.List;

/**
 * 订单项Service接口
 * 提供订单项相关的业务逻辑处理功能，包括订单项信息的增删改查等操作
 *
 * @author 李梦瑶
 * @date 2025-11-28
 */
public interface OrderItemService extends IService<OrderItem>{


    List<OrderItem> selectorderitembyorderid(int orderid);

    List<OrderItem> selectbyorderitemid(int shopid);

    List<OrderItem> selectbyuserid(int userid);

    List<OrderItem> selectOrderBylist(int id, int oid, String name, String phone);
}
