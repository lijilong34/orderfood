package org.example.orderfoodafter.controller;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.common.WhereEntity;
import org.example.orderfoodafter.config.AlipayConfig;
import org.example.orderfoodafter.entity.*;
import org.example.orderfoodafter.service.CouponService;
import org.example.orderfoodafter.service.OrderItemService;
import org.example.orderfoodafter.service.OrderService;
import org.example.orderfoodafter.service.ProductService;
import org.example.orderfoodafter.service.TableInfoService;
import org.example.orderfoodafter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description(描述):
 * @author(作者): 35938
 * @date(日期): 2025/12/9 上午9:46
 * @version(版本):2024.1.2
 */
@RestController
@RequestMapping("/order")
public class OrderController extends BaseController<Order, OrderService> {
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderItemService orderItemService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TableInfoService tableInfoService;
    
    @Autowired
    private CouponService couponService;
    
    @Autowired
    private ProductService productService;
    
    public OrderController(OrderService orderService) {
        super(orderService);
        this.orderService = orderService;
    }


    /**
     * 根据商店id查询
     * @param shopid
     * @return
     */

    @GetMapping("selectbyorderid")
    public R selectbyorderid(@RequestParam("shopid") int shopid){
        List<Order> orderlist=orderService.selectbyorderidlist(shopid);
        return new R().addData("orderlist",orderlist);
    }
    
    /**
     * 创建订单
     */
    @PostMapping("/create")
    @Transactional
    public R createOrder(@RequestBody Map<String, Object> orderData, @RequestHeader("Authorization") String token) {
        try {
            // 添加详细调试日志
            System.out.println("=== 订单创建请求开始 ===");
            System.out.println("接收到的订单数据: " + orderData);
            System.out.println("订单数据包含的键: " + orderData.keySet());
            for (String key : orderData.keySet()) {
                System.out.println("  " + key + " = " + orderData.get(key) + " (类型: " + (orderData.get(key) != null ? orderData.get(key).getClass().getSimpleName() : "null") + ")");
            }

            token = token.substring(7);
            String subject = jwtUtils.getSubject(token);
            Long userId = Long.parseLong(subject);
            System.out.println("用户ID: " + userId);
            
            // 解析订单数据 - 添加空值检查
            Object shopIdObj = orderData.get("shopId");
            if (shopIdObj == null) {
                return R.error("缺少必要参数: shopId");
            }
            Long shopId = Long.parseLong(shopIdObj.toString());

            Object totalAmountObj = orderData.get("totalAmount");
            if (totalAmountObj == null) {
                return R.error("缺少必要参数: totalAmount");
            }
            BigDecimal totalAmount = new BigDecimal(totalAmountObj.toString());

            Object payAmountObj = orderData.get("payAmount");
            if (payAmountObj == null) {
                return R.error("缺少必要参数: payAmount");
            }
            BigDecimal payAmount = new BigDecimal(payAmountObj.toString());

            Object discountAmountObj = orderData.get("discountAmount");
            if (discountAmountObj == null) {
                return R.error("缺少必要参数: discountAmount");
            }
            BigDecimal discountAmount = new BigDecimal(discountAmountObj.toString());

            Object payTypeObj = orderData.get("payType");
            if (payTypeObj == null) {
                return R.error("缺少必要参数: payType");
            }
            Integer payType = Integer.parseInt(payTypeObj.toString());

            Object orderTypeObj = orderData.get("orderType");
            if (orderTypeObj == null) {
                return R.error("缺少必要参数: orderType");
            }
            Integer orderType = Integer.parseInt(orderTypeObj.toString());

            String remark = orderData.get("remark") != null ? orderData.get("remark").toString() : "";

            String receiver = orderData.get("receiver") != null ? orderData.get("receiver").toString() : "";
            String phone = orderData.get("phone") != null ? orderData.get("phone").toString() : "";
            String address = orderData.get("address") != null ? orderData.get("address").toString() : "";
            Long tableId = orderData.get("tableId") != null ? Long.parseLong(orderData.get("tableId").toString()) : null;
            Long couponId = orderData.get("couponId") != null ? Long.parseLong(orderData.get("couponId").toString()) : null;
            
            List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");
            
            // 余额支付需要扣除余额
            if (payType == 1) {
                User user = userService.getById(userId);
                if (user.getBalance().compareTo(payAmount) < 0) {
                    return R.error("余额不足");
                }
                user.setBalance(user.getBalance().subtract(payAmount));
                userService.updateById(user);
            }
            String orderNo=String.valueOf(System.currentTimeMillis());
            // 创建订单
            Order order = new Order();
            order.setOrderNo(orderNo);
            order.setUserId(userId);
            order.setShopId(shopId);
            order.setTotalAmount(totalAmount);
            order.setPayAmount(payAmount);
            order.setDiscountAmount(discountAmount);
            order.setPayType((byte) payType.intValue());
            order.setOrderType((byte) orderType.intValue());
            order.setStatus((byte) 0);
            order.setReceiver(receiver);
            order.setPhone(phone);
            order.setAddress(address);
            order.setTableId(tableId);
            order.setRemark(remark);
            order.setCreateTime(new Date());
            order.setUpdateTime(new Date());
            
            boolean saveResult = orderService.save(order);
            if (!saveResult) {
                return R.error("订单创建失败");
            }
            QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
            orderQueryWrapper.eq("order_no", orderNo);
            Order order1=orderService.getOne(orderQueryWrapper);

            Long orderId = order1.getId();
            System.out.println("订单保存成功,订单ID: " + orderId);
            
            // 如果是堂食订单,更新餐桌状态为已预订
            if (orderType == 1 && tableId != null) {
                TableInfo tableInfo = tableInfoService.getById(tableId);
                if (tableInfo != null) {
                    tableInfo.setStatus((byte) 2);
                    tableInfoService.updateById(tableInfo);
                }
            }
            
            // 创建订单明细
            for (Map<String, Object> item : items) {
                OrderItem orderItem = new OrderItem();
                System.out.println("准备插入订单明细,order_id: " + orderId);
                orderItem.setOrderId(orderId);
                
                // 添加空值检查，防止NullPointerException
                Object productIdObj = item.get("productId");
                if (productIdObj == null) {
                    throw new RuntimeException("订单明细缺少必要字段: productId");
                }
                orderItem.setProductId(Long.parseLong(productIdObj.toString()));
                
                Object quantityObj = item.get("quantity");
                if (quantityObj == null) {
                    throw new RuntimeException("订单明细缺少必要字段: quantity");
                }
                orderItem.setQuantity(Integer.parseInt(quantityObj.toString()));
                
                Object priceObj = item.get("price");
                if (priceObj == null) {
                    throw new RuntimeException("订单明细缺少必要字段: price");
                }
                orderItem.setPrice(new BigDecimal(priceObj.toString()));
                
                Object amountObj = item.get("amount");
                if (amountObj == null) {
                    throw new RuntimeException("订单明细缺少必要字段: amount");
                }
                orderItem.setAmount(new BigDecimal(amountObj.toString()));
                
                orderItem.setRemark(item.get("remark") != null ? item.get("remark").toString() : "");
                
                System.out.println("订单明细对象: orderId=" + orderItem.getOrderId() + ", productId=" + orderItem.getProductId());
                boolean result = orderItemService.save(orderItem);
                if (!result) {
                    throw new RuntimeException("订单明细插入失败");
                }
                
                // 更新商品库存和销量
                Long productId = orderItem.getProductId();
                Integer quantity = orderItem.getQuantity();
                Product product = productService.getById(productId);
                if (product != null) {
                    if (product.getStock() != null && product.getStock() > 0) {
                        int newStock = product.getStock() - quantity;
                        if (newStock < 0) {
                            throw new RuntimeException("商品库存不足: " + product.getName());
                        }
                        product.setStock(newStock);
                    }
                    // 增加销量
                    int currentSales = product.getSales() != null ? product.getSales() : 0;
                    product.setSales(currentSales + quantity);
                    productService.updateById(product);
                    System.out.println("商品库存和销量更新成功: productId=" + productId + ", stock=" + product.getStock() + ", sales=" + product.getSales());
                }
            }
            
            // 如果使用了优惠券，更新优惠券状态为已使用
            if (couponId != null) {
                System.out.println("使用优惠券，优惠券ID: " + couponId);
                boolean useResult = couponService.useCoupon(userId, couponId);
                if (useResult) {
                    System.out.println("优惠券使用成功");
                } else {
                    System.err.println("优惠券使用失败，优惠券ID: " + couponId);
                }
            }
            String alipayForm = null;
            if (payType == 2) {
                String orderTypename="";
                if (orderType==1){
                    orderTypename="堂食";
                }else if (orderType==2){
                    orderTypename="外卖";
                }
                String ordername="御膳房"+orderTypename+"订单";
                String body="御膳房祝你用餐愉快";
                //获得初始化的AlipayClient
                AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

                //设置请求参数
                AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
                // 在回调URL中传递token和订单信息，确保登录状态不丢失
                String returnUrlWithParams = AlipayConfig.return_url + 
                    "?token=" + token + 
                    "&orderNo=" + orderNo + 
                    "&amount=" + payAmount + 
                    "&deliveryMethod=" + orderType;
                alipayRequest.setReturnUrl(returnUrlWithParams);
                alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
                alipayRequest.setBizContent("{\"out_trade_no\":\""+ orderNo +"\","
                        + "\"total_amount\":\""+ payAmount +"\","
                        + "\"subject\":\""+ ordername +"\","
                        + "\"body\":\""+ body +"\","
                        + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
                //请求
                alipayForm = alipayClient.pageExecute(alipayRequest).getBody();
                System.out.println("支付后结果"+alipayForm);
            }
            
            R result = new R().addData("orderId", order.getId()).addData("orderNo", order.getOrderNo());
            if (alipayForm != null) {
                result.addData("alipayForm", alipayForm);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("订单创建失败: " + e.getMessage());
        }
    }
    @GetMapping("/updateorderbyorderid/{orderid}")
    public R updateorderbyorderid(@PathVariable("orderid") Long orderid){
        try {
            System.out.println("收到订单状态更新请求，订单ID: " + orderid);
            
            // 先获取当前订单状态
            Order existingOrder = orderService.getById(orderid);
            if (existingOrder == null) {
                System.err.println("订单不存在，订单ID: " + orderid);
                return R.error("订单不存在");
            }
            
            // 检查订单是否已经是已支付状态
            if (existingOrder.getStatus() == 1) {
                System.out.println("订单已经是已支付状态，无需重复更新，订单ID: " + orderid);
                return new R().addData("status", "订单已经是已支付状态");
            }
            
            Order order=new Order();
            order.setId(orderid);
            order.setStatus((byte)1);
            order.setPayTime(new Date());

            boolean result = orderService.updateById(order);

            Order order1=orderService.getById(orderid);
            //获取用户id
            long userid=order1.getUserId();
            User user=userService.getById(userid);
            int growthvalue=user.getGrowthValue();
            growthvalue=growthvalue+4;
            // 设置新的成长值
            user.setGrowthValue(growthvalue);
            // 根据ID更新用户记录
            boolean flag=userService.updateById(user);
            if (!result||!flag) {
                System.err.println("订单状态更新失败，订单ID: " + orderid);
                return R.error("订单状态刷新失败");
            }
            
            System.out.println("订单状态更新成功，订单ID: " + orderid);
            return new R().addData("status", "订单状态刷新成功");
        } catch (Exception e) {
            System.err.println("更新订单状态异常: " + e.getMessage());
            e.printStackTrace();
            return R.error("更新订单状态异常: " + e.getMessage());
        }
    }
    /**
     * 根据id查询商店订单
     *
     * @param
     * @return
     */
    @PostMapping("/selectorderbyid")
    public R selectshopbyid(@RequestBody Map<String, Object> selectwhere) throws Exception{
        List where = (List) selectwhere.get("where");
        
        // 添加今天的日期筛选（使用服务器本地时区）
        java.util.Date now = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // 获取今天的开始和结束时间
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        String startTime = sdf.format(calendar.getTime());
        
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
        calendar.set(java.util.Calendar.MINUTE, 59);
        calendar.set(java.util.Calendar.SECOND, 59);
        String endTime = sdf.format(calendar.getTime());
        
        WhereEntity whereEntity = new WhereEntity();
        whereEntity.setColumn("order.create_time");
        whereEntity.setType("ge");
        whereEntity.setValue(startTime);
        
        WhereEntity whereEntity1 = new WhereEntity();
        whereEntity1.setColumn("order.create_time");
        whereEntity1.setType("le");
        whereEntity1.setValue(endTime);
        
        where.add(whereEntity);
        where.add(whereEntity1);
        
        QueryWrapper queryWrapper = commontUtil.getWhere(where);
        if (selectwhere.get("page") != null) {
            int page = Integer.parseInt(selectwhere.get("page").toString());
            PageHelper.startPage(page, 8);
        }
        List<Order> shopList = orderService.selectOrderByshopid(queryWrapper);
        PageInfo pageInfo = new PageInfo<>(shopList);
        return new R().addData("pageInfo", pageInfo);
    }

    /**
     * 根据条件查询商店订单（不包含日期限制，用于历史订单查询）
     *
     * @param selectwhere
     * @return
     */
    @PostMapping("/selectorderbyid-nodate")
    public R selectshopbyidNoDate(@RequestBody Map<String, Object> selectwhere) throws Exception{
        List where = (List) selectwhere.get("where");
        
        QueryWrapper queryWrapper = commontUtil.getWhere(where);
        if (selectwhere.get("page") != null) {
            int page = Integer.parseInt(selectwhere.get("page").toString());
            PageHelper.startPage(page, 8);
        }
        List<Order> shopList = orderService.selectOrderByshopid(queryWrapper);
        PageInfo pageInfo = new PageInfo<>(shopList);
        return new R().addData("pageInfo", pageInfo);
    }

    /**
     * 删除订单
     */
    @GetMapping("/delete/{orderId}")
    public R deleteOrder(@PathVariable("orderId") Long orderId) {
        try {
            // 检查订单是否存在
            Order order = orderService.getById(orderId);
            if (order == null) {
                return R.error("订单不存在");
            }
            
            // 删除订单相关的订单项
            QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("order_id", orderId);
            orderItemService.remove(itemWrapper);
            
            // 删除订单
            boolean result = orderService.removeById(orderId);
            if (result) {
                return new  R().addData("message", "订单删除成功");
            } else {
                return R.error("订单删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("删除订单异常: " + e.getMessage());
        }
    }

}
