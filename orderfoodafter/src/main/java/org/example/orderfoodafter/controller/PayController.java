package org.example.orderfoodafter.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.config.AlipayConfig;
import org.example.orderfoodafter.entity.Order;
import org.example.orderfoodafter.entity.User;
import org.example.orderfoodafter.service.OrderService;
import org.example.orderfoodafter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;


import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 支付宝支付回调控制器
 */
@RestController
@RequestMapping("/pay")
public class PayController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 为现有订单生成支付宝支付
     */
    @PostMapping("/existing-order")
    public R generateAlipayForExistingOrder(@RequestBody Map<String, Object> payData) {
        try {
            String orderId = payData.get("orderId").toString();
            String orderNo = payData.get("orderNo").toString();
            String payAmount = payData.get("payAmount").toString();
            Integer orderType = Integer.parseInt(payData.get("orderType").toString());
            String token = payData.get("token").toString();
            
            System.out.println("为现有订单生成支付宝支付，订单ID: " + orderId + ", 订单号: " + orderNo);
            
            // 立即更新订单状态为已支付（不等待支付宝回调）
            Order order = new Order();
            order.setId(Long.parseLong(orderId));
            order.setStatus((byte)1);
            order.setPayTime(new Date());
            boolean updateResult = orderService.updateById(order);
            
            if (!updateResult) {
                return R.error("更新订单状态失败");
            }
            System.out.println("订单状态已更新为已支付，订单ID: " + orderId);
            
            // 构建订单类型名称
            String orderTypename = "";
            if (orderType == 1) {
                orderTypename = "堂食";
            } else if (orderType == 2) {
                orderTypename = "外卖";
            }
            
            String ordername = "御膳房" + orderTypename + "订单";
            String body = "御膳房祝你用餐愉快";
            
            // 获得初始化的AlipayClient
            AlipayClient alipayClient = new DefaultAlipayClient(
                AlipayConfig.gatewayUrl, 
                AlipayConfig.app_id, 
                AlipayConfig.merchant_private_key, 
                "json", 
                AlipayConfig.charset, 
                AlipayConfig.alipay_public_key, 
                AlipayConfig.sign_type
            );

            // 设置请求参数
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            
            // 在回调URL中传递token，确保登录状态不丢失
            String returnUrlWithParams = AlipayConfig.return_url + 
                "?token=" + token + 
                "&orderNo=" + orderNo + 
                "&amount=" + payAmount + 
                "&deliveryMethod=" + orderType;
            
            alipayRequest.setReturnUrl(returnUrlWithParams);
            alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
            
            alipayRequest.setBizContent("{"
                + "\"out_trade_no\":\"" + orderNo + "\","
                + "\"total_amount\":\"" + payAmount + "\","
                + "\"subject\":\"" + ordername + "\","
                + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\""
                + "}");
            
            // 请求
            String alipayForm = alipayClient.pageExecute(alipayRequest).getBody();
            System.out.println("现有订单支付宝表单生成成功");
            
            return new R().addData("alipayForm", alipayForm);
            
        } catch (Exception e) {
            System.err.println("为现有订单生成支付宝支付失败: " + e.getMessage());
            e.printStackTrace();
            return R.error("生成支付失败: " + e.getMessage());
        }
    }
    
    /**
     * 支付宝异步通知处理
     */
    @PostMapping("/alipay/notify")
    public String alipayNotify(HttpServletRequest request) {
        System.out.println("收到支付宝异步通知");
        
        try {
            // 获取支付宝POST过来反馈信息
            Map<String, String> params = new HashMap<String, String>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }
            
            // 验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(
                params, 
                AlipayConfig.alipay_public_key, 
                AlipayConfig.charset, 
                AlipayConfig.sign_type
            );
            
            if (!signVerified) {
                System.err.println("支付宝异步通知签名验证失败");
                return "fail";
            }
            
            // 订单号
            String out_trade_no = params.get("out_trade_no");
            // 支付宝交易号
            String trade_no = params.get("trade_no");
            // 交易状态
            String trade_status = params.get("trade_status");
            // 支付金额
            String total_amount = params.get("total_amount");
            
            System.out.println("支付宝异步通知参数:");
            System.out.println("订单号: " + out_trade_no);
            System.out.println("支付宝交易号: " + trade_no);
            System.out.println("交易状态: " + trade_status);
            System.out.println("支付金额: " + total_amount);
            
            if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)) {
                System.out.println("收到支付宝支付成功通知，订单号: " + out_trade_no);
                
                // 检查是否是充值支付（订单号以RECHARGE_开头）
                if (out_trade_no.startsWith("RECHARGE_")) {
                    System.out.println("=== 充值支付通知 ===");
                    System.out.println("订单号: " + out_trade_no);
                    System.out.println("支付金额: " + total_amount);
                    
                    try {
                        // 解析订单号获取用户ID (格式: RECHARGE_timestamp_userId)
                        String[] parts = out_trade_no.split("_");
                        if (parts.length >= 3) {
                            Long userId = Long.parseLong(parts[2]);
                            BigDecimal rechargeAmount = new BigDecimal(total_amount);
                            
                            // 调用UserService添加余额
                            boolean balanceUpdated = userService.addBalance(userId, rechargeAmount);
                            
                            if (balanceUpdated) {
                                System.out.println("充值成功！用户ID: " + userId + ", 充值金额: " + total_amount);
                            } else {
                                System.err.println("充值失败！用户ID: " + userId + ", 充值金额: " + total_amount);
                            }
                        } else {
                            System.err.println("订单号格式错误，无法解析用户ID: " + out_trade_no);
                        }
                    } catch (Exception e) {
                        System.err.println("处理充值支付异常: " + e.getMessage());
                        e.printStackTrace();
                    }
                    
                    System.out.println("===================");
                } else {
                    // 处理订单支付
                    // 根据订单号查询订单
                    QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("order_no", out_trade_no);
                    Order order = orderService.getOne(queryWrapper);
                    
                    if (order != null) {
                        if (order.getStatus() == 0) {
                            // 更新订单状态为已支付
                            order.setStatus((byte) 1);
                            order.setPayTime(new Date());
                            order.setOrderNo(trade_no);
                            
                            boolean result = orderService.updateById(order);
                            if (result) {
                                System.out.println("支付宝异步通知更新订单状态成功，订单号: " + out_trade_no);
                            } else {
                                System.err.println("支付宝异步通知更新订单状态失败，订单号: " + out_trade_no);
                            }
                        } else {
                            System.out.println("订单已经是支付状态，订单号: " + out_trade_no);
                        }
                    } else {
                        System.err.println("未找到订单，订单号: " + out_trade_no);
                    }
                }
            }
            
            return "success";
        } catch (AlipayApiException e) {
            System.err.println("处理支付宝异步通知异常: " + e.getMessage());
            e.printStackTrace();
            return "fail";
        }
    }
}