// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入PageHelper的PageInfo类，用于分页信息封装
import com.github.pagehelper.PageInfo;
// 导入MinIO的MinioClient类，用于操作MinIO对象存储
import io.minio.*;
// 导入项目的DefaultApplicationProperties配置类
import org.example.orderfoodafter.common.DefaultApplicationProperties;
// 导入项目的DefaulteProperties配置类
import org.example.orderfoodafter.common.DefaulteProperties;
// 导入User实体类
import org.example.orderfoodafter.entity.User;
// 导入UserService服务接口
import org.example.orderfoodafter.service.UserService;
// 导入项目通用的R类，用于统一响应格式
import org.example.orderfoodafter.common.R;
// 导入JwtUtils工具类，用于JWT令牌处理
import org.example.orderfoodafter.common.JwtUtils;
// 导入支付宝的AlipayApiException类
import com.alipay.api.AlipayApiException;
// 导入支付宝的AlipayClient接口
import com.alipay.api.AlipayClient;
// 导入支付宝的DefaultAlipayClient实现类
import com.alipay.api.DefaultAlipayClient;
// 导入支付宝的AlipayTradePagePayRequest类
import com.alipay.api.request.AlipayTradePagePayRequest;
// 导入项目的AlipayConfig配置类
import org.example.orderfoodafter.config.AlipayConfig;
// 导入Spring的Autowired注解，用于自动装配
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring的Value注解，用于注入配置值
import org.springframework.beans.factory.annotation.Value;
// 导入Spring MVC的映射注解
import org.springframework.web.bind.annotation.*;
// 导入Spring的MultipartFile类，用于处理文件上传
import org.springframework.web.multipart.MultipartFile;
// 导入Java IO类
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
// 导入Java数学类BigDecimal，用于处理精确的金额计算
import java.math.BigDecimal;
// 导入Java NIO类，用于文件操作
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
// 导入Java集合类
import java.util.*;

/**
 * 用户控制器
 * 负责用户信息的查询、更新、充值、头像上传等功能管理
 *
 * @author 周子金
 * @date 2025-11-20
 */
// 使用RequestMapping注解设置该控制器的基础请求路径为/user
@RequestMapping("/user")
// 使用RestController注解标记该类为RESTful控制器，所有方法返回JSON
@RestController
// 定义UserController类，继承自BaseController基类，泛型为User和UserService
/**
 * UserController类
 *
 * @author 周子金
 * @date 2026-03-18
 */

public class UserController extends BaseController<User,UserService> {
    // 使用Autowired注解自动注入UserService服务实例
    @Autowired
    // 声明UserService服务对象
    private UserService userService;
    // 使用Autowired注解自动注入JwtUtils工具实例
    @Autowired
    // 声明JwtUtils工具对象
    private JwtUtils jwtUtils;

   // 使用Autowired注解自动注入DefaulteProperties配置实例
   @Autowired
   // 声明DefaulteProperties配置对象
   private DefaulteProperties defaulteProperties;

    // 定义UserController的构造函数，接收UserService参数
/**
 * UserController方法
 *
 * @author 周子金
 */
    public UserController(UserService userService) {
        // 调用父类BaseController的构造函数，传入userService
        super(userService);
        // 将传入的userService赋值给当前类的userService属性
        this.userService = userService;
    }

    /**
     * 获取当前用户信息（通过token）
     * @param token
     * @return
     * @throws Exception
     */
    // 使用GetMapping注解映射GET请求到/getCurrentUser路径
    @GetMapping("/getCurrentUser")
    // 定义获取当前用户信息的方法，接收Authorization请求头，返回R响应对象
        /**
     * 获取 getCurrentUser
     * 
     * @return getCurrentUser
     * @author 周子金
     */
    public R getCurrentUser(@RequestHeader("Authorization") String token) throws Exception {
        // 去掉Bearer前缀
        token = token.substring(7);
        // 从token中获取用户ID
        String subject = jwtUtils.getSubject(token);
        // 将用户ID转换为Long类型
        Long userId = Long.parseLong(subject);

        // 根据用户ID查询用户信息
        User user = userService.getById(userId);

        // 返回成功响应，包含用户实体信息
            /**
     * R
     * 
     * @author 周子金
     */
        return new R().addData("entity", user);
    }

    /**
     * 用户充值接口
     * @param payData 支付数据
     * @param token 认证token
     * @return
     */
    // 使用PostMapping注解映射POST请求到/recharge路径
    @PostMapping("/recharge")
    // 定义用户充值的方法，接收Map参数和Authorization请求头，返回R响应对象
        /**
     * userRecharge
     * 
     * @author 周子金
     */
    public R userRecharge(@RequestBody Map<String, Object> payData, @RequestHeader("Authorization") String token) {
        // 使用try-catch块捕获异常
        try {
            // 获取充值金额并转换为double类型
            Double amount = Double.parseDouble(payData.get("amount").toString());
            // 获取充值类型
            String type = payData.get("type").toString();

            // 在控制台输出充值请求信息
            System.out.println("用户充值请求，金额: " + amount + ", 类型: " + type);

            // 验证token
            // 去掉Bearer前缀
            token = token.substring(7);
            // 从token中获取用户ID
            String subject = jwtUtils.getSubject(token);
            // 将用户ID转换为Long类型
            Long userId = Long.parseLong(subject);

            // 构建充值订单号
            String orderNo = "RECHARGE_" + System.currentTimeMillis() + "_" + userId;
            // 设置订单名称
            String ordername = "御膳房账户充值";
            // 设置订单描述
            String body = "用户充值金额" + amount + "元";

            // 获得初始化的AlipayClient
                /**
     * DefaultAlipayClient
     * 
     * @author 周子金
     */
            AlipayClient alipayClient = new DefaultAlipayClient(
                // 支付宝网关地址
                AlipayConfig.gatewayUrl,
                // 应用ID
                AlipayConfig.app_id,
                // 商户私钥
                AlipayConfig.merchant_private_key,
                // 请求格式
                "json",
                // 字符编码
                AlipayConfig.charset,
                // 支付宝公钥
                AlipayConfig.alipay_public_key,
                // 签名类型
                AlipayConfig.sign_type
            );

            // 设置请求参数
                /**
     * AlipayTradePagePayRequest
     * 
     * @author  周子金
     */
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();

            // 在回调URL中传递token和订单信息
            String returnUrlWithParams = AlipayConfig.return_url +
                "?token=" + token +
                "&orderNo=" + orderNo +
                "&amount=" + amount +
                "&type=recharge";

            // 设置同步跳转地址
            alipayRequest.setReturnUrl(returnUrlWithParams);
            // 设置异步通知地址
            alipayRequest.setNotifyUrl(AlipayConfig.notify_url);

            // 设置业务参数
            alipayRequest.setBizContent("{"
                + "\"out_trade_no\":\"" + orderNo + "\","
                + "\"total_amount\":\"" + amount + "\","
                + "\"subject\":\"" + ordername + "\","
                + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\""
                + "}");

            // 发起请求
            String alipayForm = alipayClient.pageExecute(alipayRequest).getBody();
            // 在控制台输出成功信息
            System.out.println("充值支付宝表单生成成功");

            // 返回成功响应，包含支付宝表单
                /**
     * R
     * 
     * @author 周子金
     */
            return new R().addData("alipayForm", alipayForm);

        } catch (Exception e) {
            // 在标准错误输出中打印错误信息
            System.err.println("用户充值失败: " + e.getMessage());
            // 打印异常堆栈信息
            e.printStackTrace();
            // 抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 周子金
     */
            throw new RuntimeException("充值失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户余额
     * @param token
     * @return
     * @throws Exception
     */
    // 使用GetMapping注解映射GET请求到/getBalance路径
    @GetMapping("/getBalance")
    // 定义获取用户余额的方法，接收Authorization请求头，返回R响应对象
        /**
     * 获取 getUserBalance
     * 
     * @return getUserBalance
     * @author 周子金
     */
    public R getUserBalance(@RequestHeader("Authorization") String token) throws Exception {
        // 使用try-catch块捕获异常
        try {
            // 去掉Bearer前缀
            token = token.substring(7);
            // 从token中获取用户ID
            String subject = jwtUtils.getSubject(token);
            // 将用户ID转换为Long类型
            Long userId = Long.parseLong(subject);

            // 根据用户ID查询用户信息
            User user = userService.getById(userId);

            // 判断用户是否存在
            if (user != null) {
                // 如果用户存在，返回成功响应，包含用户余额
                    /**
     * R
     * 
     * @author 周子金
     */
                return new R().addData("balance", user.getBalance());
            } else {
                // 如果用户不存在，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 周子金
     */
                throw new RuntimeException("用户不存在");
            }
        } catch (Exception e) {
            // 捕获异常，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 周子金
     */
            throw new RuntimeException("获取用户余额失败: " + e.getMessage());
        }
    }


    // 使用PostMapping注解映射POST请求到/updateBalance路径
    @PostMapping("/updateBalance")
    // 定义更新用户余额的方法，接收Map参数，返回R响应对象
/**
 * updateUserBalance方法
 *
 * @author 周子金
 */
    public R updateUserBalance(@RequestBody Map<String, Object> data) {
        // 使用try-catch块捕获异常
        try {
            // 获取用户ID并转换为Long类型
            Long userId = Long.parseLong(data.get("userId").toString());
            // 获取金额并转换为double类型
            Double amount = Double.parseDouble(data.get("amount").toString());

            // 将金额转换为BigDecimal类型
                /**
     * BigDecimal
     * 
     * @author 周子金
     */
            BigDecimal putAmount = new BigDecimal(amount.toString());
            // 调用service层添加用户余额
            boolean result = userService.addBalance(userId, putAmount);

            // 判断更新是否成功
            if (result) {
                // 更新成功，返回成功响应，包含成功消息
                    /**
     * R
     * 
     * @author 李梦瑶
     */
                return new R().addData("message", "余额更新成功");
            } else {
                // 更新失败，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
                throw new RuntimeException("余额更新失败");
            }
        } catch (Exception e) {
            // 捕获异常，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("更新余额失败");
        }
    }

    /**
     * 上传头像
     * @param file 头像文件
     * @param token 认证token
     * @return
     */
    // 使用PostMapping注解映射POST请求到/uploadAvatar路径
    @PostMapping("/uploadAvatar")
    // 定义上传头像的方法，接收MultipartFile参数和Authorization请求头，返回R响应对象
        /**
     * uploadAvatar
     * 
     * @author 李梦瑶
     */
    public R uploadAvatar(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) {
        // 使用try-catch块捕获异常
        try {
            // 验证token
            // 去掉Bearer前缀
            token = token.substring(7);
            // 从token中获取用户ID
            String subject = jwtUtils.getSubject(token);
            // 将用户ID转换为Long类型
            Long userId = Long.parseLong(subject);

            // 验证文件
            // 判断文件是否为空
            if (file.isEmpty()) {
                // 如果文件为空，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
                throw new RuntimeException("请选择文件");
            }

            // 验证文件类型
            // 获取文件内容类型
            String contentType = file.getContentType();
            // 判断文件类型是否为图片
            if (contentType == null || !contentType.startsWith("image/")) {
                // 如果不是图片，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
                throw new RuntimeException("只能上传图片文件");
            }

            // 验证文件大小（2MB）
            // 判断文件大小是否超过2MB
            if (file.getSize() > 2 * 1024 * 1024) {
                // 如果超过限制，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
                throw new RuntimeException("图片大小不能超过2MB");
            }

            // 获取文件扩展名
            // 获取文件原始名称
            String originalFilename = file.getOriginalFilename();
            // 初始化扩展名为空字符串
            String extension = "";
            // 判断文件名是否包含点号
            if (originalFilename != null && originalFilename.contains(".")) {
                // 获取文件扩展名（点号后的部分）
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 生成新文件名
            // 使用"avatar_"前缀、用户ID、时间戳和扩展名组合成新文件名
            String newFilename = "avatar_" + userId + "_" + System.currentTimeMillis() + extension;

            // 确保上传目录存在
            // 获取上传目录路径
            Path uploputir = Paths.get(defaulteProperties.getUploadfilepath());
            // 在控制台输出上传目录的绝对路径
            System.out.println("上传目录: " + uploputir.toAbsolutePath());
            // 在控制台输出目录是否存在
            System.out.println("目录是否存在: " + Files.exists(uploputir));
            // 判断目录是否存在
            if (!Files.exists(uploputir)) {
                // 如果目录不存在，创建目录
                Files.createDirectories(uploputir);
            }

            // 保存文件
            // 构建文件保存路径
            Path filePath = uploputir.resolve(newFilename);
            // 在控制台输出文件保存的绝对路径
            System.out.println("保存文件路径: " + filePath.toAbsolutePath());
            // 将文件保存到指定路径
            file.transferTo(filePath.toFile());
            // 在控制台输出文件是否保存成功
            System.out.println("文件保存成功，文件是否存在: " + Files.exists(filePath));

            // 构建访问路径（只保存文件名）
//            String avatarUrl = "@/assets/" + newFilename;

            // 更新用户头像
            // 根据用户ID查询用户信息
            User user = userService.getById(userId);
            // 设置用户头像为文件名
            user.setAvatar(newFilename);
            // 更新用户信息
            userService.updateById(user);


            // 返回成功响应，包含头像文件名
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("avatar", newFilename);

        } catch (IOException e) {
            // 捕获IO异常，打印异常堆栈信息
            e.printStackTrace();
            // 抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常，打印异常堆栈信息
            e.printStackTrace();
            // 抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("上传失败: " + e.getMessage());
        }
    }
    // 使用Autowired注解自动注入MinioClient实例
    @Autowired
    // 声明MinioClient对象
    MinioClient minioClient;

    // 使用Autowired注解自动注入DefaultApplicationProperties配置实例
    @Autowired
    // 声明DefaultApplicationProperties配置对象
    DefaultApplicationProperties defaultApplicationProperties;



    // 使用GetMapping注解映射GET请求到/delfile路径
    @GetMapping("/delfile")
    // 定义删除文件的方法，接收MultipartFile参数，返回R响应对象
/**
 * delFile方法
 *
 * @author 李梦瑶
 */
    public R delFile(@RequestParam("file") MultipartFile file) throws Exception {
        // 设置文件路径
        String filename="2026/1/5/"+"6.png";
        // 调用MinIO客户端删除指定文件
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(defaultApplicationProperties.getBucket()).object(filename).build());
        // 返回成功响应
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R();
    }

}