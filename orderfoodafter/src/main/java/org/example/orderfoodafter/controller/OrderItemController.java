package org.example.orderfoodafter.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Order;
import org.example.orderfoodafter.entity.OrderItem;
import org.example.orderfoodafter.entity.Shop;
import org.example.orderfoodafter.service.OrderItemService;
import org.example.orderfoodafter.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @description(描述):
 * @author(作者): 35938
 * @date(日期): 2025/12/15 上午9:56
 * @version(版本):2024.1.2
 */
@RestController
@RequestMapping("/orderitem")
public class OrderItemController  extends BaseController<OrderItem, OrderItemService>{
    @Autowired
    private OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderitemservice) {
        super(orderitemservice);
        this.orderItemService = orderitemservice;
    }

    /**
     * 根据商店id查询历史订单
     * @param shopid
     * @return
     */
    @GetMapping("/selectbyorderitemid")
    public R selectbyorderid(@RequestParam("shopid") int shopid,@RequestParam("page") String page){
        int orderpage=0;
        if (page==null){
            orderpage=1;
        }else {
            orderpage=Integer.parseInt(page);
        }
        Page<Object> obj= PageHelper.startPage(orderpage, 8);
        List<OrderItem> orderitem=orderItemService.selectbyorderitemid(shopid);
        PageInfo pageInfo = new PageInfo<>(orderitem);
        return new R().addData("orderitem",orderitem).addData("obj",pageInfo);
    }

    /**
     * 根据用户ID查询该用户的所有订单
     * @param userid
     * @param page
     * @return
     */
    @GetMapping("/selectbyuserid")
    public R selectbyuserid(@RequestParam("userid") int userid,@RequestParam("page") String page){
        System.out.println("=== 查询用户订单 ===");
        System.out.println("接收到的 userid: " + userid);
        System.out.println("接收到的 page: " + page);
        int orderpage=0;
        if (page==null){
            orderpage=1;
        }else {
            orderpage=Integer.parseInt(page);
        }
        Page<Object> obj= PageHelper.startPage(orderpage, 8);
        List<OrderItem> orderitem=orderItemService.selectbyuserid(userid);
        System.out.println("查询到的订单数量: " + (orderitem != null ? orderitem.size() : 0));
        PageInfo pageInfo = new PageInfo<>(orderitem);
        return new R().addData("orderitem",orderitem).addData("obj",pageInfo);
    }

    /**
     * 根据订单id，用户名，手机号查询历史订单
     * @param id
     * @param oid
     * @param name
     * @param phone
     * @return
     */
    @PostMapping("/selectorderbylist")
    public R selectshopbyid(@RequestParam("id") int id,@RequestParam("oid") int oid,@RequestParam("name") String name,@RequestParam("phone") String phone,@RequestParam("page") String page) {
        int orderpage=0;
        if (page==null){
            orderpage=1;
        }else {
            orderpage=Integer.parseInt(page);
        }
        Page<Object> obj= PageHelper.startPage(orderpage, 8);
        List<OrderItem> orderitemList=orderItemService.selectOrderBylist(id,oid,name,phone);
        PageInfo pageInfo = new PageInfo<>(orderitemList);
        return new R().addData("orderitemList", orderitemList).addData("obj",pageInfo);
    }

    /**
     * 根据用户ID、订单号、收货人、手机号查询用户订单
     * @param id
     * @param oid
     * @param name
     * @param phone
     * @param page
     * @return
     */
    @PostMapping("/selectorderbyuserlist")
    public R selectorderbyuserlist(@RequestParam("id") int id,@RequestParam("oid") int oid,@RequestParam("name") String name,@RequestParam("phone") String phone,@RequestParam("page") String page) {
        int orderpage=0;
        if (page==null){
            orderpage=1;
        }else {
            orderpage=Integer.parseInt(page);
        }
        Page<Object> obj= PageHelper.startPage(orderpage, 8);
        List<OrderItem> orderitemList=orderItemService.selectOrderBylist(id,oid,name,phone);
        PageInfo pageInfo = new PageInfo<>(orderitemList);
        return new R().addData("orderitemList", orderitemList).addData("obj",pageInfo);
    }
    
    /**
     * 根据店铺ID查询该店铺的所有订单项（店长使用）
     * @param shopid
     * @param page
     * @return
     */
    @GetMapping("/selectbyshopid")
    public R selectbyshopid(@RequestParam("shopid") int shopid,@RequestParam("page") String page){
        System.out.println("=== 店长查询店铺订单项 ===");
        System.out.println("接收到的 shopid: " + shopid);
        System.out.println("接收到的 page: " + page);
        int orderpage=0;
        if (page==null){
            orderpage=1;
        }else {
            orderpage=Integer.parseInt(page);
        }
        Page<Object> obj= PageHelper.startPage(orderpage, 8);
        List<OrderItem> orderitem=orderItemService.selectbyorderitemid(shopid);
        System.out.println("查询到的订单项数量: " + (orderitem != null ? orderitem.size() : 0));
        PageInfo pageInfo = new PageInfo<>(orderitem);
        return new R().addData("orderitem",orderitem).addData("obj",pageInfo);
    }
}
