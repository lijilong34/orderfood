package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.common.CommontUtil;
import org.example.orderfoodafter.common.JwtUtils;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.common.WhereEntity;
import org.example.orderfoodafter.entity.Order;
import org.example.orderfoodafter.entity.OrderItem;
import org.example.orderfoodafter.service.OrderItemService;
import org.example.orderfoodafter.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class OrderbyUserController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    CommontUtil commontUtil;
    @Autowired
    JwtUtils jwtUtils;
    
    @PostMapping("/orderbyUser")
    public R orderbyUser(@RequestHeader("Authorization") String token,@RequestBody Map<String,Object> selectwhere) throws Exception{
        List where = (List) selectwhere.get("where");
        // 不再添加user_id条件到where列表，因为SQL中已经硬编码了WHERE o.user_id = #{userId}
        token=token.substring(7);
        String subject = jwtUtils.getSubject(token);
        QueryWrapper queryWrapper = commontUtil.getWhere(where);
        if (selectwhere.get("page") != null) {
            int page = Integer.parseInt(selectwhere.get("page").toString());

            PageHelper.startPage(page, 5);
        }
        List<Order> orders=orderService.selectorderbyuser(queryWrapper, subject);
        PageInfo<Order> pageInfo=new PageInfo<>(orders);
        return new R().addData("pageInfo",pageInfo);
    }
    
    @GetMapping("/selectorderitembyorderid/{orderid}")
    public R selectorderitembyorderid(@PathVariable("orderid") int orderid){
        List<OrderItem> orderItemList=orderItemService.selectorderitembyorderid(orderid);
        return new R().addData("orderItemList",orderItemList);
    }
    @GetMapping("/Cancelorderbyorder/{orderid}")
    public R Cancelorderbyorder(@PathVariable("orderid") long orderid){
        Order order=new Order();
        order.setId(orderid);
        order.setStatus((byte) 3);
        boolean flag=orderService.updateById(order);
        if (!flag){
            throw new RuntimeException("取消失败");
        }
        return new R().addData("status","取消成功");
    }
}
