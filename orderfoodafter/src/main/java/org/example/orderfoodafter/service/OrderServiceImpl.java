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

/**
 * 订单Service实现类
 * 实现订单相关的业务逻辑处理功能
 *
 * @author 李梦瑶
 * @date 2025-11-29
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService{
/**
 * selectbyorderidlist方法
 *
 * @author 李梦瑶
 */

    @Override
    public List<Order> selectbyorderidlist(int shopid) {
        List<Order> orderList=baseMapper.selectbyorderid(shopid);
        return orderList;
    }
/**
 * selectorderbyuser方法
 *
 * @author 李梦瑶
 */

    @Override
    public List<Order> selectorderbyuser(QueryWrapper queryWrapper, String userId) {
        List<Order> orderList=baseMapper.selectorderbyuser(queryWrapper, userId);
        return orderList;
    }
/**
 * selectorderbyuserByParams方法
 *
 * @author 李梦瑶
 */

    @Override
    public List<Order> selectorderbyuserByParams(Map<String, Object> params) {
        return baseMapper.selectorderbyuserByParams(params);
    }
/**
 * selectOrderByshopid方法
 *
 * @author 李梦瑶
 */

    @Override
    public List<Order> selectOrderByshopid(QueryWrapper queryWrapper) {
        List<Order> orderList=baseMapper.selectOrderByshopid(queryWrapper);
        return orderList;
    }
}
