// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入MyBatis Plus的查询条件包装器
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入PageHelper分页助手
import com.github.pagehelper.PageHelper;
// 导入PageInfo分页信息类
import com.github.pagehelper.PageInfo;
// 导入通用工具类
import org.example.orderfoodafter.common.CommontUtil;
// 导入JWT工具类
import org.example.orderfoodafter.common.JwtUtils;
// 导入通用返回结果类R
import org.example.orderfoodafter.common.R;
// 导入WhereEntity条件实体类
import org.example.orderfoodafter.common.WhereEntity;
// 导入订单实体类
import org.example.orderfoodafter.entity.Order;
// 导入订单项实体类
import org.example.orderfoodafter.entity.OrderItem;
// 导入订单项服务类
import org.example.orderfoodafter.service.OrderItemService;
// 导入订单服务类
import org.example.orderfoodafter.service.OrderService;
// 导入Spring框架的自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring Web的绑定注解
import org.springframework.web.bind.annotation.*;

// 导入Java集合框架的List接口
import java.util.List;
// 导入Java集合框架的Map接口
import java.util.Map;

/**
 * 用户订单控制器
 * 负责管理用户的订单信息，包括订单查询、订单详情、订单取消等功能
 * 
 * @author 李梦瑶
 * @date 2025-12-02
 */
// 使用RestController注解标识这是一个REST控制器
@RestController
// 定义OrderbyUserController控制器类
/**
 * OrderbyUserController类
 *
 * @author 李梦瑶
 * @date 2026-03-18
 */

public class OrderbyUserController {
    // 使用Autowired注解自动注入OrderService服务
    @Autowired
    // 声明OrderService服务实例
    private OrderService orderService;
    // 使用Autowired注解自动注入OrderItemService服务
    @Autowired
    // 声明OrderItemService服务实例
    private OrderItemService orderItemService;
    // 使用Autowired注解自动注入CommontUtil工具类
    @Autowired
    // 声明CommontUtil工具实例
    CommontUtil commontUtil;
    // 使用Autowired注解自动注入JwtUtils工具类
    @Autowired
    // 声明JwtUtils工具实例
    JwtUtils jwtUtils;
    
    /**
     * 根据用户查询订单列表
     * 作者:李梦瑶
     * @param token 用户令牌
     * @param selectwhere 查询条件
     * @return 订单分页数据
     * @throws Exception
     */
    // 使用PostMapping注解映射POST请求到/orderbyUser路径
    @PostMapping("/orderbyUser")
    // 定义根据用户查询订单列表的方法，从请求头获取token，接收请求体参数，返回R类型对象
        /**
     * orderbyUser
     * 
     * @author 李梦瑶
     */
    public R orderbyUser(@RequestHeader("Authorization") String token,@RequestBody Map<String,Object> selectwhere) throws Exception{
        // 从查询条件中获取where列表
        List where = (List) selectwhere.get("where");
        // 不再添加user_id条件到where列表，因为SQL中已经硬编码了WHERE o.user_id = #{userId}
        // 去掉Bearer前缀
        token=token.substring(7);
        // 从token中获取用户ID
        String subject = jwtUtils.getSubject(token);
        // 构建查询条件包装器
        QueryWrapper queryWrapper = commontUtil.getWhere(where);
        // 检查是否包含分页参数
        if (selectwhere.get("page") != null) {
            // 获取页码并转换为整数
            int page = Integer.parseInt(selectwhere.get("page").toString());

            // 启动分页，每页5条记录
            PageHelper.startPage(page, 5);
        }
        // 调用服务层根据用户查询订单
        List<Order> orders=orderService.selectorderbyuser(queryWrapper, subject);
        // 创建分页信息对象
        PageInfo<Order> pageInfo=new PageInfo<>(orders);
        // 返回分页数据
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("pageInfo",pageInfo);
    }
    
    /**
     * 根据订单ID查询订单详情
     * 作者:李梦瑶
     * @param orderid 订单ID
     * @return 订单详情列表
     */
    // 使用GetMapping注解映射GET请求到/selectorderitembyorderid/{orderid}路径
    @GetMapping("/selectorderitembyorderid/{orderid}")
    // 定义根据订单ID查询订单详情的方法，从路径变量获取订单ID，返回R类型对象
/**
 * selectorderitembyorderid方法
 *
 * @author 李梦瑶
 */
    public R selectorderitembyorderid(@PathVariable("orderid") int orderid){
        // 调用服务层根据订单ID查询订单详情
        List<OrderItem> orderItemList=orderItemService.selectorderitembyorderid(orderid);
        // 返回订单详情列表
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("orderItemList",orderItemList);
    }
    /**
     * 取消订单
     * 作者:李梦瑶
     * @param orderid 订单ID
     * @return 取消结果
     */
    // 使用GetMapping注解映射GET请求到/Cancelorderbyorder/{orderid}路径
    @GetMapping("/Cancelorderbyorder/{orderid}")
    // 定义取消订单的方法，从路径变量获取订单ID，返回R类型对象
/**
 * Cancelorderbyorder方法
 *
 * @author 李梦瑶
 */
    public R Cancelorderbyorder(@PathVariable("orderid") long orderid){
        // 创建订单对象
            /**
     * Order
     * 
     * @author 李梦瑶
     */
        Order order=new Order();
        // 设置订单ID
        order.setId(orderid);
        // 设置订单状态为3（已取消）
        order.setStatus((byte) 3);
        // 调用服务层更新订单
        boolean flag=orderService.updateById(order);
        // 判断更新结果
        if (!flag){
            // 更新失败，抛出异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("取消失败");
        }
        // 返回成功消息
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("status","取消成功");
    }
}
