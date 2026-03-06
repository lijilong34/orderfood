package org.example.orderfoodafter.controller;

import com.github.pagehelper.PageInfo;
import io.minio.*;
import org.example.orderfoodafter.common.DefaultApplicationProperties;
import org.example.orderfoodafter.common.DefaulteProperties;
import org.example.orderfoodafter.entity.User;
import org.example.orderfoodafter.service.UserService;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.common.JwtUtils;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import org.example.orderfoodafter.config.AlipayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 描述:查询用户接口
 * 姓名:李吉隆
 * 日期:2025/12/4
 */
@RequestMapping("/user")
@RestController
public class UserController extends BaseController<User,UserService> {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    
   @Autowired
   private DefaulteProperties defaulteProperties;

    public UserController(UserService userService) {
        super(userService);
        this.userService = userService;
    }
    
    /**
     * 获取当前用户信息（通过token）
     * @param token
     * @return
     * @throws Exception
     */
    @GetMapping("/getCurrentUser")
    public R getCurrentUser(@RequestHeader("Authorization") String token) throws Exception {
        // 去掉Bearer前缀
        token = token.substring(7);
        // 从token中获取用户ID
        String subject = jwtUtils.getSubject(token);
        Long userId = Long.parseLong(subject);
        
        // 根据用户ID查询用户信息
        User user = userService.getById(userId);
        
        return new R().addData("entity", user);
    }

    /**
     * 用户充值接口
     * @param payData 支付数据
     * @param token 认证token
     * @return
     */
    @PostMapping("/recharge")
    public R userRecharge(@RequestBody Map<String, Object> payData, @RequestHeader("Authorization") String token) {
        try {
            Double amount = Double.parseDouble(payData.get("amount").toString());
            String type = payData.get("type").toString();
            
            System.out.println("用户充值请求，金额: " + amount + ", 类型: " + type);
            
            // 验证token
            token = token.substring(7);
            String subject = jwtUtils.getSubject(token);
            Long userId = Long.parseLong(subject);
            
            // 构建充值订单号
            String orderNo = "RECHARGE_" + System.currentTimeMillis() + "_" + userId;
            String ordername = "御膳房账户充值";
            String body = "用户充值金额" + amount + "元";
            
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
            
            // 在回调URL中传递token和订单信息
            String returnUrlWithParams = AlipayConfig.return_url + 
                "?token=" + token + 
                "&orderNo=" + orderNo + 
                "&amount=" + amount + 
                "&type=recharge";
            
            alipayRequest.setReturnUrl(returnUrlWithParams);
            alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
            
            alipayRequest.setBizContent("{"
                + "\"out_trade_no\":\"" + orderNo + "\","
                + "\"total_amount\":\"" + amount + "\","
                + "\"subject\":\"" + ordername + "\","
                + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\""
                + "}");
            
            // 请求
            String alipayForm = alipayClient.pageExecute(alipayRequest).getBody();
            System.out.println("充值支付宝表单生成成功");
            
            return new R().addData("alipayForm", alipayForm);
            
        } catch (Exception e) {
            System.err.println("用户充值失败: " + e.getMessage());
            e.printStackTrace();
            return R.error("充值失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户余额
     * @param token
     * @return
     * @throws Exception
     */
    @GetMapping("/getBalance")
    public R getUserBalance(@RequestHeader("Authorization") String token) throws Exception {
        // 去掉Bearer前缀
        token = token.substring(7);
        // 从token中获取用户ID
        String subject = jwtUtils.getSubject(token);
        Long userId = Long.parseLong(subject);
        
        // 根据用户ID查询用户信息
        User user = userService.getById(userId);
        
        if (user != null) {
            return new R().addData("balance", user.getBalance());
        } else {
            return R.error("用户不存在");
        }
    }


    @PostMapping("/updateBalance")
    public R updateUserBalance(@RequestBody Map<String, Object> data) {
        try {
            Long userId = Long.parseLong(data.get("userId").toString());
            Double amount = Double.parseDouble(data.get("amount").toString());
            
            BigDecimal putAmount = new BigDecimal(amount.toString());
            boolean result = userService.addBalance(userId, putAmount);
            
            if (result) {
                return R.ok().addData("message", "余额更新成功");
            } else {
                return R.error("余额更新失败");
            }
        } catch (Exception e) {
            return R.error("更新余额失败");
        }
    }

    /**
     * 上传头像
     * @param file 头像文件
     * @param token 认证token
     * @return
     */
    @PostMapping("/uploadAvatar")
    public R uploadAvatar(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) {
        try {
            // 验证token
            token = token.substring(7);
            String subject = jwtUtils.getSubject(token);
            Long userId = Long.parseLong(subject);

            // 验证文件
            if (file.isEmpty()) {
                return R.error("请选择文件");
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return R.error("只能上传图片文件");
            }

            // 验证文件大小（2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return R.error("图片大小不能超过2MB");
            }

            // 获取文件扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 生成新文件名
            String newFilename = "avatar_" + userId + "_" + System.currentTimeMillis() + extension;

            // 确保上传目录存在
            Path uploputir = Paths.get(defaulteProperties.getUploadfilepath());
            System.out.println("上传目录: " + uploputir.toAbsolutePath());
            System.out.println("目录是否存在: " + Files.exists(uploputir));
            if (!Files.exists(uploputir)) {
                Files.createDirectories(uploputir);
            }

            // 保存文件
            Path filePath = uploputir.resolve(newFilename);
            System.out.println("保存文件路径: " + filePath.toAbsolutePath());
            file.transferTo(filePath.toFile());
            System.out.println("文件保存成功，文件是否存在: " + Files.exists(filePath));

            // 构建访问路径（只保存文件名）
//            String avatarUrl = "@/assets/" + newFilename;

            // 更新用户头像
            User user = userService.getById(userId);
            user.setAvatar(newFilename);
            userService.updateById(user);


            return R.ok().addData("avatar", newFilename);

        } catch (IOException e) {
            e.printStackTrace();
            return R.error("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("上传失败: " + e.getMessage());
        }
    }
    @Autowired
    MinioClient minioClient;

    @Autowired
    DefaultApplicationProperties defaultApplicationProperties;



    @GetMapping("/delfile")
    public R delFile(@RequestParam("file") MultipartFile file) throws Exception {
        //id
        String filename="2026/1/5/"+"6.png";
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(defaultApplicationProperties.getBucket()).object(filename).build());
        return new R();
    }

}
