// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入Page分页类
import com.github.pagehelper.Page;
// 导入PageHelper分页助手
import com.github.pagehelper.PageHelper;
// 导入PageInfo分页信息类
import com.github.pagehelper.PageInfo;
// 导入通用返回结果类R
import org.example.orderfoodafter.common.R;
// 导入订单实体类
import org.example.orderfoodafter.entity.Order;
// 导入订单项实体类
import org.example.orderfoodafter.entity.OrderItem;
// 导入商店实体类
import org.example.orderfoodafter.entity.Shop;
// 导入订单项服务类
import org.example.orderfoodafter.service.OrderItemService;
// 导入订单服务类
import org.example.orderfoodafter.service.OrderService;
// 导入Spring框架的自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring Web的绑定注解
import org.springframework.web.bind.annotation.*;

// 导入List列表接口
import java.util.List;
// 导入Map映射接口
import java.util.Map;

/**
 * 订单项控制器
 * 负责管理订单项的详细信息，包括订单项的查询、统计等功能
 * 
 * @author 李吉隆
 * @date 2025-11-25
 */
// 使用RestController注解标识这是一个REST控制器
@RestController
// 设置请求映射路径为/orderitem
@RequestMapping("/orderitem")
// 定义OrderItemController控制器类，继承BaseController基础控制器
/**
 * OrderItemController类
 *
 * @author 李吉隆
 * @date 2026-03-18
 */

public class OrderItemController  extends BaseController<OrderItem, OrderItemService>{
    // 使用Autowired注解自动注入OrderItemService服务
    @Autowired
    // 声明OrderItemService服务实例
    private OrderItemService orderItemService;

    // 定义构造函数，接收OrderItemService服务参数
/**
 * OrderItemController方法
 *
 * @author 李吉隆
 */
    public OrderItemController(OrderItemService orderitemservice) {
        // 调用父类构造函数，传入服务实例
        super(orderitemservice);
        // 将服务实例赋值给当前类的成员变量
        this.orderItemService = orderitemservice;
    }

    /**
     * 根据商店ID查询历史订单
     * 作者:李吉隆
     * @param shopid 商店ID
     * @param page 页码
     * @return 订单项列表
     */
    // 使用GetMapping注解映射GET请求到/selectbyorderitemid路径
    @GetMapping("/selectbyorderitemid")
    // 定义根据商店ID查询历史订单的方法，接收请求参数，返回R类型对象
        /**
     * selectbyorderid
     * 
     * @author 李吉隆
     */
    public R selectbyorderid(@RequestParam("shopid") int shopid,@RequestParam("page") String page){
        // 初始化订单页码为0
        int orderpage=0;
        // 判断页码是否为空
        if (page==null){
            // 页码为空，设置为1
            orderpage=1;
        }else {
            // 页码不为空，转换为整数
            orderpage=Integer.parseInt(page);
        }
        // 启动分页，每页8条记录
        Page<Object> obj= PageHelper.startPage(orderpage, 8);
        // 调用服务层根据商店ID查询订单项
        List<OrderItem> orderitem=orderItemService.selectbyorderitemid(shopid);
        // 创建分页信息对象
        PageInfo pageInfo = new PageInfo<>(orderitem);
        // 返回订单项列表和分页信息
            /**
     * R
     * 
     * @author 李吉隆
     */
        return new R().addData("orderitem",orderitem).addData("obj",pageInfo);
    }

    /**
     * 根据用户ID查询该用户的所有订单
     * 作者:李吉隆
     * @param userid 用户ID
     * @param page 页码
     * @return 订单项列表
     */
    // 使用GetMapping注解映射GET请求到/selectbyuserid路径
    @GetMapping("/selectbyuserid")
    // 定义根据用户ID查询订单的方法，接收请求参数，返回R类型对象
        /**
     * selectbyuserid
     * 
     * @author 李吉隆
     */
    public R selectbyuserid(@RequestParam("userid") int userid,@RequestParam("page") String page){
        System.out.println("=== 查询用户订单 ===");
        System.out.println("接收到的 userid: " + userid);
        System.out.println("接收到的 page: " + page);
        // 初始化订单页码为0
      int orderpage=0;
        // 判断页码是否为空
        if (page==null){
            // 页码为空，设置为1
            orderpage=1;
        }else {
            // 页码不为空，转换为整数
           orderpage=Integer.parseInt(page);
        }
        // 启动分页，每页8条记录
       PageHelper.startPage(orderpage,8);
        // 调用服务层根据用户ID查询订单项
       List<OrderItem> orderitem=orderItemService.selectbyorderitemid(userid);
        System.out.println("查询到的订单数量: " + (orderitem != null ? orderitem.size() : 0));
        // 创建分页信息对象
        PageInfo<OrderItem> pageInfo=new PageInfo<>(orderitem);
        // 返回订单项列表和分页信息
            /**
     * R
     * 
     * @author 李吉隆
     */
      return new R().addData("orderitem",pageInfo);
    }

    /**
     * 根据订单id，用户名，手机号查询历史订单
     * @param id 商店ID
     * @param oid 订单ID
     * @param name 用户名
     * @param phone 手机号
     * @return 订单项列表
     */
    // 使用PostMapping注解映射POST请求到/selectorderbylist路径
    @PostMapping("/selectorderbylist")
    // 定义根据多个条件查询订单的方法，接收请求参数，返回R类型对象
        /**
     * selectshopbyid
     * 
     * @author 李吉隆
     */
    public R selectshopbyid(@RequestParam("id") int id,@RequestParam("oid") int oid,@RequestParam("name") String name,@RequestParam("phone") String phone,@RequestParam("page") String page) {
        // 初始化订单页码为0
        int orderpage=0;
        // 判断页码是否为空
        if (page==null){
            // 页码为空，设置为1
            orderpage=1;
        }else {
            // 页码不为空，转换为整数
            orderpage=Integer.parseInt(page);
        }
        // 启动分页，每页8条记录
        Page<Object> obj= PageHelper.startPage(orderpage, 8);
        // 调用服务层根据多个条件查询订单项
        List<OrderItem> orderitemList=orderItemService.selectOrderBylist(id,oid,name,phone);
        // 创建分页信息对象
        PageInfo pageInfo = new PageInfo<>(orderitemList);
        // 返回订单项列表和分页信息
            /**
     * R
     * 
     * @author 李吉隆
     */
        return new R().addData("orderitemList", orderitemList).addData("obj",pageInfo);
    }

    /**
     * 根据用户ID、订单号、收货人、手机号查询用户订单
     * @param id 用户ID
     * @param oid 订单ID
     * @param name 收货人
     * @param phone 手机号
     * @param page 页码
     * @return 订单项列表
     */
    // 使用PostMapping注解映射POST请求到/selectorderbyuserlist路径
    @PostMapping("/selectorderbyuserlist")
    // 定义根据多个条件查询用户订单的方法，接收请求参数，返回R类型对象
        /**
     * selectorderbyuserlist
     * 
     * @author 李吉隆
     */
    public R selectorderbyuserlist(@RequestParam("id") int id,@RequestParam("oid") int oid,@RequestParam("name") String name,@RequestParam("phone") String phone,@RequestParam("page") String page) {
        // 初始化订单页码为0
        int orderpage=0;
        // 判断页码是否为空
        if (page==null){
            // 页码为空，设置为1
            orderpage=1;
        }else {
            // 页码不为空，转换为整数
            orderpage=Integer.parseInt(page);
        }
        // 启动分页，每页8条记录
        Page<Object> obj= PageHelper.startPage(orderpage, 8);
        // 调用服务层根据多个条件查询订单项
        List<OrderItem> orderitemList=orderItemService.selectOrderBylist(id,oid,name,phone);
        // 创建分页信息对象
        PageInfo pageInfo = new PageInfo<>(orderitemList);
        // 返回订单项列表和分页信息
            /**
     * R
     * 
     * @author 李吉隆
     */
        return new R().addData("orderitemList", orderitemList).addData("obj",pageInfo);
    }
    
    /**
     * 根据店铺ID查询该店铺的所有订单项（店长使用）
     * @param shopid 店铺ID
     * @param page 页码
     * @return 订单项列表
     */
    // 使用GetMapping注解映射GET请求到/selectbyshopid路径
    @GetMapping("/selectbyshopid")
    // 定义根据店铺ID查询订单项的方法，接收请求参数，返回R类型对象
        /**
     * selectbyshopid
     * 
     * @author 李吉隆
     */
    public R selectbyshopid(@RequestParam("shopid") int shopid,@RequestParam("page") String page){
        System.out.println("=== 店长查询店铺订单项 ===");
        System.out.println("接收到的 shopid: " + shopid);
        System.out.println("接收到的 page: " + page);
        // 初始化订单页码为0
       int orderpage=0;
        // 判断页码是否为空
        if (page==null){
            // 页码为空，设置为1
          orderpage=1;
        }else {
            // 页码不为空，转换为整数
           orderpage=Integer.parseInt(page);
        }
        // 启动分页，每页8条记录
        Page<Object> obj=PageHelper.startPage(orderpage,8);
        // 调用服务层根据店铺ID查询订单项
        List<OrderItem> orderitem=orderItemService.selectbyorderitemid(shopid);
        System.out.println("查询到的订单项数量: " + (orderitem != null ? orderitem.size() : 0));
        // 创建分页信息对象
        PageInfo<OrderItem> pageInfo=new PageInfo<>(orderitem);
        // 返回订单项列表和分页信息
            /**
     * R
     * 
     * @author 李吉隆
     */
       return new R().addData("orderitem",orderitem).addData("obj",pageInfo);
    }
}
