// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入支付宝API异常类
import com.alipay.api.AlipayApiException;
// 导入支付宝客户端接口
import com.alipay.api.AlipayClient;
// 导入支付宝默认客户端实现
import com.alipay.api.DefaultAlipayClient;
// 导入支付宝签名工具类
import com.alipay.api.internal.util.AlipaySignature;
// 导入支付宝交易页面支付请求类
import com.alipay.api.request.AlipayTradePagePayRequest;
// 导入MyBatis Plus的查询条件包装器
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入Jakarta的HTTP请求类
import jakarta.servlet.http.HttpServletRequest;
// 导入通用返回结果类R
import org.example.orderfoodafter.common.R;
// 导入支付宝配置类
import org.example.orderfoodafter.config.AlipayConfig;
// 导入订单实体类
import org.example.orderfoodafter.entity.Order;
// 导入用户实体类
import org.example.orderfoodafter.entity.User;
// 导入订单服务类
import org.example.orderfoodafter.service.OrderService;
// 导入用户服务类
import org.example.orderfoodafter.service.UserService;
// 导入Spring框架的自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring Web的绑定注解
import org.springframework.web.bind.annotation.PostMapping;
// 导入RequestBody注解
import org.springframework.web.bind.annotation.RequestBody;
// 导入RequestMapping注解
import org.springframework.web.bind.annotation.RequestMapping;
// 导入RestController注解
import org.springframework.web.bind.annotation.RestController;
// 导入BigDecimal精确数值类型
import java.math.BigDecimal;


// 导入Date日期类
import java.util.Date;
// 导入HashMap映射类
import java.util.HashMap;
// 导入Iterator迭代器接口
import java.util.Iterator;
// 导入Map映射接口
import java.util.Map;

/**
 * 支付控制器
 * 负责处理支付宝支付功能，包括生成支付订单、处理支付回调、更新订单状态等操作
 * 
 * @author 李吉隆
 * @date 2025-12-01
 */
// 使用RestController注解标识这是一个REST控制器
@RestController
// 设置请求映射路径为/pay
@RequestMapping("/pay")
// 定义PayController控制器类
/**
 * PayController类
 *
 * @author 李吉隆
 * @date 2026-03-18
 */

public class PayController {

    // 使用Autowired注解自动注入OrderService服务
    @Autowired
    // 声明OrderService服务实例
    private OrderService orderService;

    // 使用Autowired注解自动注入UserService服务
    @Autowired
    // 声明UserService服务实例
    private UserService userService;
    
    /**
     * 为现有订单生成支付宝支付
     * 作者:李吉隆
     * @param payData 支付数据
     * @return 支付表单
     */
    // 使用PostMapping注解映射POST请求到/existing-order路径
    @PostMapping("/existing-order")
    // 定义为现有订单生成支付宝支付的方法，接收请求体参数，返回R类型对象
        /**
     * generateAlipayForExistingOrder
     * 
     * @author 李吉隆
     */
    public R generateAlipayForExistingOrder(@RequestBody Map<String, Object> payData) {
        // 使用try-catch块处理可能的异常
        try {
            // 从支付数据中获取订单ID
            String orderId = payData.get("orderId").toString();
            // 从支付数据中获取订单号
            String orderNo = payData.get("orderNo").toString();
            // 从支付数据中获取支付金额
            String payAmount = payData.get("payAmount").toString();
            // 从支付数据中获取订单类型
            Integer orderType = Integer.parseInt(payData.get("orderType").toString());
            // 从支付数据中获取token
            String token = payData.get("token").toString();

            System.out.println("为现有订单生成支付宝支付，订单ID: " + orderId + ", 订单号: " + orderNo);

            // 立即更新订单状态为已支付（不等待支付宝回调）
                /**
     * Order
     * 
     * @author 李吉隆
     */
            Order order = new Order();
            // 设置订单ID
            order.setId(Long.parseLong(orderId));
            // 设置订单状态为1（已支付）
            order.setStatus((byte)1);
            // 设置支付时间
                /**
     * 设置 setPayTime
     * 
     * @param setPayTime setPayTime
     * @author 李吉隆
     */
            order.setPayTime(new Date());
            // 更新订单状态
            boolean updateResult = orderService.updateById(order);

            // 检查更新结果
            if (!updateResult) {
                // 更新失败，返回错误消息
                return R.error("更新订单状态失败");
            }
            System.out.println("订单状态已更新为已支付，订单ID: " + orderId);

            // 构建订单类型名称
            String orderTypename = "";
            // 判断订单类型
            if (orderType == 1) {
                // 堂食
                orderTypename = "堂食";
            } else if (orderType == 2) {
                // 外卖
                orderTypename = "外卖";
            }

            // 设置订单名称
            String ordername = "御膳房" + orderTypename + "订单";
            // 设置订单描述
            String body = "御膳房祝你用餐愉快";

            // 获得初始化的AlipayClient
                /**
     * DefaultAlipayClient
     * 
     * @author 李吉隆
     */
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
                /**
     * AlipayTradePagePayRequest
     * 
     * @author 李吉隆
     */
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();

            // 在回调URL中传递token，确保登录状态不丢失
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

            // 返回支付宝表单
                /**
     * R
     * 
     * @author 李吉隆
     */
            return new R().addData("alipayForm", alipayForm);

        } catch (Exception e) {
            System.err.println("为现有订单生成支付宝支付失败: " + e.getMessage());
            // 捕获异常，打印异常堆栈
            e.printStackTrace();
            // 返回错误消息
            return R.error("生成支付失败: " + e.getMessage());
        }
    }
    
    /**
     * 支付宝异步通知处理
     * 作者:李吉隆
     * @param request HTTP请求
     * @return 处理结果
     */
    // 使用PostMapping注解映射POST请求到/alipay/notify路径
    @PostMapping("/alipay/notify")
    // 定义处理支付宝异步通知的方法，接收HTTP请求参数，返回字符串
        /**
     * alipayNotify
     * 
     * @author 李吉隆
     */
    public String alipayNotify(HttpServletRequest request) {
        System.out.println("收到支付宝异步通知");

        // 使用try-catch块处理可能的异常
        try {
            // 获取支付宝POST过来反馈信息
            Map<String, String> params = new HashMap<String, String>();
            // 获取请求参数Map
            Map<String, String[]> requestParams = request.getParameterMap();
            // 遍历请求参数
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
                // 获取参数名
                String name = (String) iter.next();
                // 获取参数值数组
                String[] values = (String[]) requestParams.get(name);
                // 初始化值字符串
                String valueStr = "";
                // 拼接参数值
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                // 将参数名和值添加到params Map中
                params.put(name, valueStr);
            }

            // 验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(
                params,
                AlipayConfig.alipay_public_key,
                AlipayConfig.charset,
                AlipayConfig.sign_type
            );

            // 检查签名验证结果
            if (!signVerified) {
                System.err.println("支付宝异步通知签名验证失败");
                // 返回失败消息
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
            
            // 判断交易状态是否为成功或完成
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
                        // 检查订单号格式是否正确
                        if (parts.length >= 3) {
                            // 解析用户ID
                            Long userId = Long.parseLong(parts[2]);
                            // 解析充值金额
                                /**
     * BigDecimal
     * 
     * @author 李吉隆
     */
                            BigDecimal rechargeAmount = new BigDecimal(total_amount);

                            // 调用UserService添加余额
                            boolean balanceUpdated = userService.addBalance(userId, rechargeAmount);

                            // 检查充值结果
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
                        // 捕获异常，打印异常堆栈
                        e.printStackTrace();
                    }

                    System.out.println("===================");
                } else {
                    // 处理订单支付
                    // 根据订单号查询订单
                    QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
                    // 设置查询条件：订单号等于out_trade_no
                    queryWrapper.eq("order_no", out_trade_no);
                    // 查询订单
                    Order order = orderService.getOne(queryWrapper);

                    // 检查订单是否存在
                    if (order != null) {
                        // 检查订单状态是否为待支付
                        if (order.getStatus() == 0) {
                            // 更新订单状态为已支付
                            order.setStatus((byte) 1);
                            // 设置支付时间
                                /**
     * 设置 setPayTime
     * 
     * @param setPayTime setPayTime
     * @author 李吉隆
     */
                            order.setPayTime(new Date());
                            // 设置支付宝交易号
                            order.setOrderNo(trade_no);

                            // 更新订单状态
                            boolean result = orderService.updateById(order);
                            // 检查更新结果
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

            // 返回成功消息
            return "success";
        } catch (AlipayApiException e) {
            System.err.println("处理支付宝异步通知异常: " + e.getMessage());
            // 捕获异常，打印异常堆栈
            e.printStackTrace();
            // 返回失败消息
            return "fail";
        }
    }
}