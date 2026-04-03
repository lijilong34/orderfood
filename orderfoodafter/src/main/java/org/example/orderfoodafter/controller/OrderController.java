// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入支付宝客户端接口
import com.alipay.api.AlipayClient;
// 导入支付宝默认客户端实现
import com.alipay.api.DefaultAlipayClient;
// 导入支付宝交易页面支付请求类
import com.alipay.api.request.AlipayTradePagePayRequest;
// 导入MyBatis Plus的查询条件包装器
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入MyBatis Plus的更新条件包装器
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
// 导入PageHelper分页助手
import com.github.pagehelper.PageHelper;
// 导入PageInfo分页信息类
import com.github.pagehelper.PageInfo;
// 导入通用返回结果类R
import org.example.orderfoodafter.common.R;
// 导入WhereEntity条件实体类
import org.example.orderfoodafter.common.WhereEntity;
// 导入支付宝配置类
import org.example.orderfoodafter.config.AlipayConfig;
// 导入所有实体类
import org.example.orderfoodafter.entity.*;
// 导入优惠券服务类
import org.example.orderfoodafter.service.CouponService;
// 导入订单项服务类
import org.example.orderfoodafter.service.OrderItemService;
// 导入订单服务类
import org.example.orderfoodafter.service.OrderService;
// 导入商品服务类
import org.example.orderfoodafter.service.ProductService;
// 导入餐桌信息服务类
import org.example.orderfoodafter.service.TableInfoService;
// 导入用户服务类
import org.example.orderfoodafter.service.UserService;
// 导入Spring框架的自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring事务管理注解
import org.springframework.transaction.annotation.Transactional;
// 导入Spring Web的绑定注解
import org.springframework.web.bind.annotation.*;

// 导入BigDecimal精确数值类型
import java.math.BigDecimal;
// 导入LocalDate日期类
import java.time.LocalDate;
// 导入LocalDateTime日期时间类
import java.time.LocalDateTime;
// 导入DateTimeFormatter日期时间格式化类
import java.time.format.DateTimeFormatter;
// 导入Date日期类
import java.util.Date;
// 导入List列表接口
import java.util.List;
// 导入Map映射接口
import java.util.Map;

/**
 * 订单管理控制器
 * 提供订单的创建、查询、更新、删除等功能，支持余额支付和支付宝支付
 * 
 * @author 李梦瑶
 * @date 2025-11-28
 */
// 使用RestController注解标识这是一个REST控制器
@RestController
// 设置请求映射路径为/order
@RequestMapping("/order")
// 定义OrderController控制器类，继承BaseController基础控制器
/**
 * OrderController类
 *
 * @author 李梦瑶
 * @date 2026-03-18
 */

public class OrderController extends BaseController<Order, OrderService> {
    // 使用Autowired注解自动注入OrderService服务
    @Autowired
    // 声明OrderService服务实例
    private OrderService orderService;

    // 使用Autowired注解自动注入OrderItemService服务
    @Autowired
    // 声明OrderItemService服务实例
    private OrderItemService orderItemService;

    // 使用Autowired注解自动注入UserService服务
    @Autowired
    // 声明UserService服务实例
    private UserService userService;

    // 使用Autowired注解自动注入TableInfoService服务
    @Autowired
    // 声明TableInfoService服务实例
    private TableInfoService tableInfoService;

    // 使用Autowired注解自动注入CouponService服务
    @Autowired
    // 声明CouponService服务实例
    private CouponService couponService;

    // 使用Autowired注解自动注入ProductService服务
    @Autowired
    // 声明ProductService服务实例
    private ProductService productService;

    // 定义构造函数，接收OrderService服务参数
/**
 * OrderController方法
 *
 * @author 李梦瑶
 */
    public OrderController(OrderService orderService) {
        // 调用父类构造函数，传入服务实例
        super(orderService);
        // 将服务实例赋值给当前类的成员变量
        this.orderService = orderService;
    }


    /**
     * 根据商店ID查询订单
     * 作者:李梦瑶
     * @param shopid 商店ID
     * @return 订单列表
     */
    // 使用GetMapping注解映射GET请求到selectbyorderid路径
    @GetMapping("selectbyorderid")
    // 定义根据商店ID查询订单的方法，接收请求参数，返回R类型对象
        /**
     * selectbyorderid
     * 
     * @author 熊杨博
     */
    public R selectbyorderid(@RequestParam("shopid") int shopid){
        // 调用服务层根据商店ID查询订单
        List<Order> orderlist=orderService.selectbyorderidlist(shopid);
        // 返回订单列表
            /**
     * R
     * 
     * @author 熊杨博
     */
        return new R().addData("orderlist",orderlist);
    }
    
    /**
     * 创建订单
     * 作者:李梦瑶
     * @param orderData 订单数据
     * @param token 用户令牌
     * @return 创建结果
     */
    // 使用PostMapping注解映射POST请求到/create路径
    @PostMapping("/create")
    // 使用Transactional注解开启事务管理
    @Transactional
    // 定义创建订单的方法，接收请求体参数和请求头参数，返回R类型对象
        /**
     * createOrder
     * 
     * @author 熊杨博
     */
    public R createOrder(@RequestBody Map<String, Object> orderData, @RequestHeader("Authorization") String token) {
        // 使用try-catch块处理可能的异常
        try {
            // 添加详细调试日志
            System.out.println("=== 订单创建请求开始 ===");
            System.out.println("接收到的订单数据: " + orderData);
            System.out.println("订单数据包含的键: " + orderData.keySet());
            // 遍历订单数据，打印每个键值对
            for (String key : orderData.keySet()) {
                System.out.println("  " + key + " = " + orderData.get(key) + " (类型: " + (orderData.get(key) != null ? orderData.get(key).getClass().getSimpleName() : "null") + ")");
            }

            // 去掉Bearer前缀
            token = token.substring(7);
            // 从token中获取用户ID
            String subject = jwtUtils.getSubject(token);
            // 将用户ID转换为Long类型
            Long userId = Long.parseLong(subject);
            System.out.println("用户ID: " + userId);

            // 解析订单数据 - 添加空值检查
            Object shopIdObj = orderData.get("shopId");
            // 检查商店ID是否为空
            if (shopIdObj == null) {
                // 抛出异常，提示缺少必要参数
                    /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                throw new RuntimeException("缺少必要参数: shopId");
            }
            // 将商店ID转换为Long类型
            Long shopId = Long.parseLong(shopIdObj.toString());

            Object totalAmountObj = orderData.get("totalAmount");
            // 检查总金额是否为空
            if (totalAmountObj == null) {
                // 抛出异常，提示缺少必要参数
                    /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                throw new RuntimeException("缺少必要参数: totalAmount");
            }
            // 将总金额转换为BigDecimal类型
                /**
     * BigDecimal
     * 
     * @author 熊杨博
     */
            BigDecimal totalAmount = new BigDecimal(totalAmountObj.toString());

            Object payAmountObj = orderData.get("payAmount");
            // 检查支付金额是否为空
            if (payAmountObj == null) {
                // 抛出异常，提示缺少必要参数
                    /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                throw new RuntimeException("缺少必要参数: payAmount");
            }
            // 将支付金额转换为BigDecimal类型
                /**
     * BigDecimal
     * 
     * @author 熊杨博
     */
            BigDecimal payAmount = new BigDecimal(payAmountObj.toString());

            Object discountAmountObj = orderData.get("discountAmount");
            // 检查折扣金额是否为空
            if (discountAmountObj == null) {
                // 抛出异常，提示缺少必要参数
                    /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                throw new RuntimeException("缺少必要参数: discountAmount");
            }
            // 将折扣金额转换为BigDecimal类型
                /**
     * BigDecimal
     * 
     * @author 熊杨博
     */
            BigDecimal discountAmount = new BigDecimal(discountAmountObj.toString());

            Object payTypeObj = orderData.get("payType");
            // 检查支付方式是否为空
            if (payTypeObj == null) {
                // 抛出异常，提示缺少必要参数
                    /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                throw new RuntimeException("缺少必要参数: payType");
            }
            // 将支付方式转换为Integer类型
            Integer payType = Integer.parseInt(payTypeObj.toString());

            Object orderTypeObj = orderData.get("orderType");
            // 检查订单类型是否为空
            if (orderTypeObj == null) {
                // 抛出异常，提示缺少必要参数
                    /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                throw new RuntimeException("缺少必要参数: orderType");
            }
            // 将订单类型转换为Integer类型
            Integer orderType = Integer.parseInt(orderTypeObj.toString());

            // 获取备注信息，为空则使用空字符串
            String remark = orderData.get("remark") != null ? orderData.get("remark").toString() : "";

            // 获取收货人，为空则使用空字符串
            String receiver = orderData.get("receiver") != null ? orderData.get("receiver").toString() : "";
            // 获取手机号，为空则使用空字符串
            String phone = orderData.get("phone") != null ? orderData.get("phone").toString() : "";
            // 获取地址，为空则使用空字符串
            String address = orderData.get("address") != null ? orderData.get("address").toString() : "";
            // 获取餐桌ID，为空则使用null
            Long tableId = orderData.get("tableId") != null ? Long.parseLong(orderData.get("tableId").toString()) : null;
            // 获取优惠券ID，为空则使用null
            Long couponId = orderData.get("couponId") != null ? Long.parseLong(orderData.get("couponId").toString()) : null;

            // 获取订单明细列表
            List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");

            // 余额支付需要扣除余额
            if (payType == 1) {
                // 根据用户ID查询用户信息
                User user = userService.getById(userId);
                // 检查余额是否足够
                if (user.getBalance().compareTo(payAmount) < 0) {
                    // 余额不足，抛出异常
                        /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                    throw new RuntimeException("余额不足");
                }
                // 扣除用户余额
                user.setBalance(user.getBalance().subtract(payAmount));
                // 更新用户信息
                userService.updateById(user);
            }
            // 生成订单号，使用当前时间戳
            String orderNo=String.valueOf(System.currentTimeMillis());
            // 创建订单对象
                /**
     * Order
     * 
     * @author 熊杨博
     */
            Order order = new Order();
            // 设置订单号
            order.setOrderNo(orderNo);
            // 设置用户ID
            order.setUserId(userId);
            // 设置商店ID
            order.setShopId(shopId);
            // 设置总金额
            order.setTotalAmount(totalAmount);
            // 设置支付金额
            order.setPayAmount(payAmount);
            // 设置折扣金额
            order.setDiscountAmount(discountAmount);
            // 设置支付方式
            order.setPayType((byte) payType.intValue());
            // 设置订单类型
            order.setOrderType((byte) orderType.intValue());
            // 设置订单状态为0（待支付）
            order.setStatus((byte) 0);
            // 设置收货人
            order.setReceiver(receiver);
            // 设置手机号
            order.setPhone(phone);
            // 设置地址
            order.setAddress(address);
            // 设置餐桌ID
            order.setTableId(tableId);
            // 设置备注
            order.setRemark(remark);
            // 设置创建时间
                /**
     * 设置 setCreateTime
     * 
     * @param setCreateTime setCreateTime
     * @author 熊杨博
     */
            order.setCreateTime(new Date());
            // 设置更新时间
                /**
     * 设置 setUpdateTime
     * 
     * @param setUpdateTime setUpdateTime
     * @author 熊杨博
     */
            order.setUpdateTime(new Date());

            // 保存订单到数据库
            boolean saveResult = orderService.save(order);
            // 检查保存结果
            if (!saveResult) {
                // 保存失败，抛出异常
                    /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                throw new RuntimeException("订单创建失败");
            }
            // 创建查询条件包装器
            QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
            // 设置查询条件：订单号等于orderNo
            orderQueryWrapper.eq("order_no", orderNo);
            // 根据订单号查询订单
            Order order1=orderService.getOne(orderQueryWrapper);

            // 获取订单ID
            Long orderId = order1.getId();
            System.out.println("订单保存成功,订单ID: " + orderId);

            // 如果是堂食订单,更新餐桌状态为已预订
            if (orderType == 1 && tableId != null) {
                // 根据餐桌ID查询餐桌信息
                TableInfo tableInfo = tableInfoService.getById(tableId);
                // 检查餐桌是否存在
                if (tableInfo != null) {
                    // 设置餐桌状态为2（已预订）
                    tableInfo.setStatus((byte) 2);
                    // 更新餐桌信息
                    tableInfoService.updateById(tableInfo);
                }
            }

            // 创建订单明细
            for (Map<String, Object> item : items) {
                // 创建订单明细对象
                    /**
     * OrderItem
     * 
     * @author 熊杨博
     */
                OrderItem orderItem = new OrderItem();
                System.out.println("准备插入订单明细,order_id: " + orderId);
                // 设置订单ID
                orderItem.setOrderId(orderId);

                // 添加空值检查，防止NullPointerException
                Object productIdObj = item.get("productId");
                // 检查商品ID是否为空
                if (productIdObj == null) {
                    // 抛出异常，提示缺少必要字段
                        /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                    throw new RuntimeException("订单明细缺少必要字段: productId");
                }
                // 设置商品ID
                orderItem.setProductId(Long.parseLong(productIdObj.toString()));

                Object quantityObj = item.get("quantity");
                // 检查数量是否为空
                if (quantityObj == null) {
                    // 抛出异常，提示缺少必要字段
                        /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                    throw new RuntimeException("订单明细缺少必要字段: quantity");
                }
                // 设置数量
                orderItem.setQuantity(Integer.parseInt(quantityObj.toString()));

                Object priceObj = item.get("price");
                // 检查价格是否为空
                if (priceObj == null) {
                    // 抛出异常，提示缺少必要字段
                        /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                    throw new RuntimeException("订单明细缺少必要字段: price");
                }
                // 设置价格
                    /**
     * 设置 setPrice
     * 
     * @param setPrice setPrice
     * @author 熊杨博
     */
                orderItem.setPrice(new BigDecimal(priceObj.toString()));

                Object amountObj = item.get("amount");
                // 检查金额是否为空
                if (amountObj == null) {
                    // 抛出异常，提示缺少必要字段
                        /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                    throw new RuntimeException("订单明细缺少必要字段: amount");
                }
                // 设置金额
                    /**
     * 设置 setAmount
     * 
     * @param setAmount setAmount
     * @author 熊杨博
     */
                orderItem.setAmount(new BigDecimal(amountObj.toString()));

                // 设置备注，为空则使用空字符串
                orderItem.setRemark(item.get("remark") != null ? item.get("remark").toString() : "");

                System.out.println("订单明细对象: orderId=" + orderItem.getOrderId() + ", productId=" + orderItem.getProductId());
                // 保存订单明细到数据库
                boolean result = orderItemService.save(orderItem);
                // 检查保存结果
                if (!result) {
                    // 保存失败，抛出异常
                        /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                    throw new RuntimeException("订单明细插入失败");
                }

                // 更新商品库存和销量
                Long productId = orderItem.getProductId();
                Integer quantity = orderItem.getQuantity();
                // 根据商品ID查询商品信息
                Product product = productService.getById(productId);
                // 检查商品是否存在
                if (product != null) {
                    // 检查商品库存
                    if (product.getStock() != null && product.getStock() > 0) {
                        // 计算新的库存
                        int newStock = product.getStock() - quantity;
                        // 检查库存是否足够
                        if (newStock < 0) {
                            // 库存不足，抛出异常
                                /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                            throw new RuntimeException("商品库存不足: " + product.getName());
                        }
                        // 设置新的库存
                        product.setStock(newStock);
                    }
                    // 增加销量
                    int currentSales = product.getSales() != null ? product.getSales() : 0;
                    // 设置新的销量
                    product.setSales(currentSales + quantity);
                    // 更新商品信息
                    productService.updateById(product);
                    System.out.println("商品库存和销量更新成功: productId=" + productId + ", stock=" + product.getStock() + ", sales=" + product.getSales());
                }
            }

            // 如果使用了优惠券，更新优惠券状态为已使用
            if (couponId != null) {
                System.out.println("使用优惠券，优惠券ID: " + couponId);
                // 调用服务层使用优惠券
                boolean useResult = couponService.useCoupon(userId, couponId);
                // 检查使用结果
                if (useResult) {
                    System.out.println("优惠券使用成功");
                } else {
                    System.err.println("优惠券使用失败，优惠券ID: " + couponId);
                }
            }
            // 初始化支付宝表单为null
            String alipayForm = null;
            // 如果是支付宝支付
            if (payType == 2) {
                // 初始化订单类型名称
                String orderTypename="";
                // 根据订单类型设置名称
                if (orderType==1){
                    orderTypename="堂食";
                }else if (orderType==2){
                    orderTypename="外卖";
                }
                // 设置订单名称
                String ordername="御膳房"+orderTypename+"订单";
                // 设置订单描述
                String body="御膳房祝你用餐愉快";
                //获得初始化的AlipayClient
                    /**
     * DefaultAlipayClient
     * 
     * @author 熊杨博
     */
                AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

                //设置请求参数
                    /**
     * AlipayTradePagePayRequest
     * 
     * @author 熊杨博
     */
                AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
                // 在回调URL中传递token和订单信息，确保登录状态不丢失
                String returnUrlWithParams = AlipayConfig.return_url +
                    "?token=" + token +
                    "&orderNo=" + orderNo +
                    "&amount=" + payAmount +
                    "&deliveryMethod=" + orderType;
                // 设置返回URL
                alipayRequest.setReturnUrl(returnUrlWithParams);
                // 设置异步通知URL
                alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
                // 设置业务内容
                alipayRequest.setBizContent("{\"out_trade_no\":\""+ orderNo +"\","
                        + "\"total_amount\":\""+ payAmount +"\","
                        + "\"subject\":\""+ ordername +"\","
                        + "\"body\":\""+ body +"\","
                        + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
                //请求
                alipayForm = alipayClient.pageExecute(alipayRequest).getBody();
                System.out.println("支付后结果"+alipayForm);
            }

            // 创建返回结果对象
                /**
     * R
     * 
     * @author 熊杨博
     */
            R result = new R().addData("orderId", order.getId()).addData("orderNo", order.getOrderNo());
            // 如果支付宝表单不为空，添加到返回结果中
            if (alipayForm != null) {
                result.addData("alipayForm", alipayForm);
            }
            // 返回结果
            return result;
        } catch (Exception e) {
            // 捕获异常，打印异常堆栈
            e.printStackTrace();
            // 抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
            throw new RuntimeException("订单创建失败: " + e.getMessage());
        }
    }
    /**
     * 更新订单状态为已支付
     * 作者:李梦瑶
     * @param orderid 订单ID
     * @return 更新结果
     */
    // 使用GetMapping注解映射GET请求到/updateorderbyorderid/{orderid}路径
    @GetMapping("/updateorderbyorderid/{orderid}")
    // 定义更新订单状态的方法，从路径变量获取订单ID，返回R类型对象
/**
 * updateorderbyorderid方法
 *
 * @author 李梦瑶
 */
    public R updateorderbyorderid(@PathVariable("orderid") Long orderid){
        // 使用try-catch块处理可能的异常
        try {
            System.out.println("收到订单状态更新请求，订单ID: " + orderid);

            // 先获取当前订单状态
            Order existingOrder = orderService.getById(orderid);
            // 检查订单是否存在
            if (existingOrder == null) {
                System.err.println("订单不存在，订单ID: " + orderid);
                // 抛出异常，提示订单不存在
                    /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                throw new RuntimeException("订单不存在");
            }

            // 检查订单是否已经是已支付状态
            if (existingOrder.getStatus() == 1) {
                System.out.println("订单已经是已支付状态，无需重复更新，订单ID: " + orderid);
                // 返回成功消息
                    /**
     * R
     * 
     * @author 熊杨博
     */
                return new R().addData("status", "订单已经是已支付状态");
            }

            // 创建订单对象
                /**
     * Order
     * 
     * @author 熊杨博
     */
            Order order=new Order();
            // 设置订单ID
            order.setId(orderid);
            // 设置订单状态为1（已支付）
            order.setStatus((byte)1);
            // 设置支付时间
                /**
     * 设置 setPayTime
     * 
     * @param setPayTime setPayTime
     * @author 熊杨博
     */
            order.setPayTime(new Date());

            // 更新订单状态
            boolean result = orderService.updateById(order);

            // 根据订单ID查询订单
            Order order1=orderService.getById(orderid);
            //获取用户id
            long userid=order1.getUserId();
            // 根据用户ID查询用户信息
            User user=userService.getById(userid);
            // 获取当前成长值
            int growthvalue=user.getGrowthValue();
            // 增加成长值
            growthvalue=growthvalue+4;
            // 设置新的成长值
            user.setGrowthValue(growthvalue);
            // 根据ID更新用户记录
            boolean flag=userService.updateById(user);
            // 检查更新结果
            if (!result||!flag) {
                System.err.println("订单状态更新失败，订单ID: " + orderid);
                // 抛出异常，提示更新失败
                    /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                throw new RuntimeException("订单状态刷新失败");
            }

            System.out.println("订单状态更新成功，订单ID: " + orderid);
            // 返回成功消息
                /**
     * R
     * 
     * @author 熊杨博
     */
            return new R().addData("status", "订单状态刷新成功");
        } catch (Exception e) {
            System.err.println("更新订单状态异常: " + e.getMessage());
            // 捕获异常，打印异常堆栈
            e.printStackTrace();
            // 抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
            throw new RuntimeException("更新订单状态异常: " + e.getMessage());
        }
    }
    /**
     * 根据条件查询商店订单（包含今天日期筛选）
     * 作者:李梦瑶
     * @param selectwhere 查询条件
     * @return 订单列表
     * @throws Exception
     */
    // 使用PostMapping注解映射POST请求到/selectorderbyid路径
    @PostMapping("/selectorderbyid")
    // 定义查询商店订单的方法，接收请求体参数，返回R类型对象
        /**
     * selectshopbyid
     * 
     * @author 熊杨博
     */
    public R selectshopbyid(@RequestBody Map<String, Object> selectwhere) throws Exception{
        // 从查询条件中获取where列表
        List where = (List) selectwhere.get("where");

        // 添加今天的日期筛选（使用服务器本地时区）
        java.util.Date now = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 获取今天的开始和结束时间
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        // 设置日历时间为当前时间
        calendar.setTime(now);
        // 设置小时为0
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        // 设置分钟为0
        calendar.set(java.util.Calendar.MINUTE, 0);
        // 设置秒为0
        calendar.set(java.util.Calendar.SECOND, 0);
        // 格式化开始时间
        String startTime = sdf.format(calendar.getTime());

        // 设置小时为23
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
        // 设置分钟为59
        calendar.set(java.util.Calendar.MINUTE, 59);
        // 设置秒为59
        calendar.set(java.util.Calendar.SECOND, 59);
        // 格式化结束时间
        String endTime = sdf.format(calendar.getTime());

        // 创建开始时间条件实体
            /**
     * WhereEntity
     * 
     * @author 熊杨博
     */
        WhereEntity whereEntity = new WhereEntity();
        // 设置列名为order.create_time
        whereEntity.setColumn("order.create_time");
        // 设置条件类型为大于等于
        whereEntity.setType("ge");
        // 设置值为开始时间
        whereEntity.setValue(startTime);

        // 创建结束时间条件实体
            /**
     * WhereEntity
     * 
     * @author 熊杨博
     */
        WhereEntity whereEntity1 = new WhereEntity();
        // 设置列名为order.create_time
        whereEntity1.setColumn("order.create_time");
        // 设置条件类型为小于等于
        whereEntity1.setType("le");
        // 设置值为结束时间
        whereEntity1.setValue(endTime);

        // 将条件实体添加到where列表
        where.add(whereEntity);
        where.add(whereEntity1);

        // 构建查询条件包装器
        QueryWrapper queryWrapper = commontUtil.getWhere(where);
        // 检查是否包含分页参数
        if (selectwhere.get("page") != null) {
            // 获取页码并转换为整数
            int page = Integer.parseInt(selectwhere.get("page").toString());
            // 启动分页，每页8条记录
            PageHelper.startPage(page, 8);
        }
        // 调用服务层查询商店订单
        List<Order> shopList = orderService.selectOrderByshopid(queryWrapper);
        // 创建分页信息对象
        PageInfo pageInfo = new PageInfo<>(shopList);
        // 返回分页数据
            /**
     * R
     * 
     * @author 熊杨博
     */
        return new R().addData("pageInfo", pageInfo);
    }

    /**
     * 根据条件查询商店订单（不包含日期限制，用于历史订单查询）
     * 作者:李梦瑶
     * @param selectwhere 查询条件
     * @return 订单列表
     * @throws Exception
     */
    // 使用PostMapping注解映射POST请求到/selectorderbyid-nodate路径
    @PostMapping("/selectorderbyid-nodate")
    // 定义查询商店订单的方法，不限制日期，接收请求体参数，返回R类型对象
        /**
     * selectshopbyidNoDate
     * 
     * @author 熊杨博
     */
    public R selectshopbyidNoDate(@RequestBody Map<String, Object> selectwhere) throws Exception{
        // 从查询条件中获取where列表
        List where = (List) selectwhere.get("where");

        // 构建查询条件包装器
        QueryWrapper queryWrapper = commontUtil.getWhere(where);
        // 检查是否包含分页参数
        if (selectwhere.get("page") != null) {
            // 获取页码并转换为整数
            int page = Integer.parseInt(selectwhere.get("page").toString());
            // 启动分页，每页8条记录
            PageHelper.startPage(page, 8);
        }
        // 调用服务层查询商店订单
        List<Order> shopList = orderService.selectOrderByshopid(queryWrapper);
        // 创建分页信息对象
        PageInfo pageInfo = new PageInfo<>(shopList);
        // 返回分页数据
            /**
     * R
     * 
     * @author 熊杨博
     */
        return new R().addData("pageInfo", pageInfo);
    }

    /**
     * 删除订单
     * 作者:李梦瑶
     * @param orderId 订单ID
     * @return 删除结果
     */
    // 使用GetMapping注解映射GET请求到/delete/{orderId}路径
    @GetMapping("/delete/{orderId}")
    // 定义删除订单的方法，从路径变量获取订单ID，返回R类型对象
/**
 * deleteOrder方法
 *
 * @author 李梦瑶
 */
    public R deleteOrder(@PathVariable("orderId") Long orderId) {
        // 使用try-catch块处理可能的异常
        try {
            // 检查订单是否存在
            Order order = orderService.getById(orderId);
            // 判断订单是否存在
            if (order == null) {
                // 订单不存在，抛出异常
                    /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                throw new RuntimeException("订单不存在");
            }

            // 删除订单相关的订单项
            QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
            // 设置查询条件：订单ID等于orderId
            itemWrapper.eq("order_id", orderId);
            // 删除订单项
            orderItemService.remove(itemWrapper);

            // 删除订单
            boolean result = orderService.removeById(orderId);
            // 判断删除结果
            if (result) {
                // 删除成功，返回成功消息
                    /**
     * R
     * 
     * @author 熊杨博
     */
                return new  R().addData("message", "订单删除成功");
            } else {
                // 删除失败，抛出异常
                    /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                throw new RuntimeException("订单删除失败");
            }
        } catch (Exception e) {
            // 捕获异常，打印异常堆栈
            e.printStackTrace();
            // 抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
            throw new RuntimeException("删除订单异常: " + e.getMessage());
        }
    }

}
