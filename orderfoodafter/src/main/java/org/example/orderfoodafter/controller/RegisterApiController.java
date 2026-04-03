// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入Apache HttpClient的HttpClient类，用于发送HTTP请求
import org.apache.commons.httpclient.HttpClient;
// 导入Apache HttpClient的HttpException类，用于处理HTTP异常
import org.apache.commons.httpclient.HttpException;
// 导入Apache HttpClient的NameValuePair类，用于封装请求参数
import org.apache.commons.httpclient.NameValuePair;
// 导入Apache HttpClient的PostMethod类，用于发送POST请求
import org.apache.commons.httpclient.methods.PostMethod;
// 导入dom4j的Document类，用于表示XML文档
import org.dom4j.Document;
// 导入dom4j的DocumentException类，用于处理XML解析异常
import org.dom4j.DocumentException;
// 导入dom4j的DocumentHelper类，用于解析XML字符串
import org.dom4j.DocumentHelper;
// 导入dom4j的Element类，用于表示XML元素
import org.dom4j.Element;
// 导入项目通用的R类，用于统一响应格式
import org.example.orderfoodafter.common.R;
// 导入UpdateUserRedis类，用于更新Redis中的用户信息
import org.example.orderfoodafter.common.UpdateUserRedis;
// 导入Mobile实体类，用于存储短信API的账号密码
import org.example.orderfoodafter.entity.Mobile;
// 导入User实体类
import org.example.orderfoodafter.entity.User;
// 导入UserService服务接口
import org.example.orderfoodafter.service.UserService;
// 导入Spring的Autowired注解，用于自动装配
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring Data Redis的HashOperations类，用于操作Redis哈希结构
import org.springframework.data.redis.core.HashOperations;
// 导入Spring Data Redis的RedisTemplate类，用于操作Redis
import org.springframework.data.redis.core.RedisTemplate;
// 导入Spring MVC的映射注解
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 导入Java IO异常类IOException
import java.io.IOException;
// 导入Java集合类Map
import java.util.Map;
// 导入Java并发包的TimeUnit类，用于时间单位转换
import java.util.concurrent.TimeUnit;

/**
 * 注册控制器
 * 负责处理用户注册功能，包括发送验证码、验证手机号、用户注册等操作
 *
 * @author 李梦瑶
 * @date 2025-11-26
 */
// 使用RequestMapping注解设置该控制器的基础请求路径为/register
@RequestMapping("/register")
// 使用RestController注解标记该类为RESTful控制器，所有方法返回JSON
@RestController
// 定义RegisterApiController类，继承自BaseController基类，泛型为User和UserService
/**
 * RegisterApiController类
 *
 * @author 李梦瑶
 * @date 2026-03-18
 */

public class RegisterApiController extends BaseController<User,UserService> {
    // 使用Autowired注解自动注入UserService服务实例
    @Autowired
    // 声明UserService服务对象
    private UserService userService;
    // 使用Autowired注解自动注入RedisTemplate实例
    @Autowired
    // 声明RedisTemplate对象，用于操作Redis
    RedisTemplate<String,Object> redisTemplate;

    // 定义RegisterApiController的构造函数，接收UserService参数
/**
 * RegisterApiController方法
 *
 * @author 李梦瑶
 */
    public RegisterApiController(UserService userService) {
        // 调用父类BaseController的构造函数，传入userService
        super(userService);
        // 将传入的userService赋值给当前类的userService属性
        this.userService = userService;
    }

    /**
     * 获取验证码
     * 作者:李梦瑶
     * @param phone 手机号
     * @return 验证码
     */
    // 使用PostMapping注解映射POST请求到/sendCode路径
    @PostMapping("/sendCode")
    // 定义获取验证码的方法，接收Map参数并返回R响应对象
        /**
     * takecode
     * 
     * @author 李梦瑶
     */
    public R  takecode(@RequestBody Map<String,String> phone) {
        // 定义短信API的URL地址
        String Url = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";
        // 创建HttpClient对象，用于发送HTTP请求
            /**
     * HttpClient
     * 
     * @author 李梦瑶
     */
        HttpClient client = new HttpClient();
        // 创建PostMethod对象，指定请求的URL地址
            /**
     * PostMethod
     * 
     * @author 李梦瑶
     */
        PostMethod method = new PostMethod(Url);

        // 设置HTTP请求的字符编码为UTF-8
        client.getParams().setContentCharset("UTF-8");
        // 设置请求头Content-Type，指定内容类型和字符编码
        method.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");

        // 生成6位随机验证码
        int mobile_code = (int) ((Math.random() * 9 + 1) * 100000);

        // 构建短信内容
            /**
     * String
     * 
     * @author 李梦瑶
     */
        String content = new String("您的验证码是：" + mobile_code + "。请不要把验证码泄露给其他人。");

        // 从参数中获取手机号
        String phone1=phone.get("phone");

        // 构建请求数据，包含短信API的账号、密码、手机号和短信内容
        NameValuePair[] data = {
                // 设置短信API的账号
                    /**
     * NameValuePair
     * 
     * @author 李梦瑶
     */
                new NameValuePair("account", Mobile.account),
                // 设置短信API的密码
                    /**
     * NameValuePair
     * 
     * @author 李梦瑶
     */
                new NameValuePair("password", Mobile.password),
                // 设置接收短信的手机号
                    /**
     * NameValuePair
     * 
     * @author 李梦瑶
     */
                new NameValuePair("mobile", phone1),
                // 设置短信内容
                    /**
     * NameValuePair
     * 
     * @author 李梦瑶
     */
                new NameValuePair("content", content),
        };
        // 将请求数据设置到POST方法中
        method.setRequestBody(data);
        // 将验证码存储到Redis中，键为"userphone:"+手机号，过期时间为5分钟
        redisTemplate.opsForValue().set("userphone:"+phone1,mobile_code+"",5, TimeUnit.MINUTES);
        // 初始化响应结果字符串
        String SubmitResult = "";
        // 使用try-catch块捕获HTTP请求过程中的异常
        try{
            // 执行HTTP请求，获取响应状态码
            int statusCode = client.executeMethod(method);
            // 获取响应体的内容
            SubmitResult = method.getResponseBodyAsString();

            // 在控制台输出响应状态码
            System.out.println("响应状态码: " + statusCode);
            // 在控制台输出响应内容
            System.out.println("响应内容: " + SubmitResult);

            // 检查响应是否为空
            if (SubmitResult == null || SubmitResult.trim().isEmpty()) {
                // 在控制台输出警告信息
                System.out.println("警告: 短信API返回空响应，状态码: " + statusCode);
                // 即使API响应失败，也返回生成的验证码（调试用）
                    /**
     * R
     * 
     * @author 李梦瑶
     */
                return new R().addData("code", mobile_code).addData("warn", "短信API无响应");
            }

            // 尝试解析XML响应
            Document doc = DocumentHelper.parseText(SubmitResult);
            // 获取XML文档的根元素
            Element root = doc.getRootElement();

            // 获取根元素中code元素的文本内容
            String code = root.elementText("code");
            // 获取根元素中msg元素的文本内容
            String msg = root.elementText("msg");
            // 获取根元素中smsid元素的文本内容
            String smsid = root.elementText("smsid");

            // 在控制台输出短信API返回的信息
            System.out.println("短信API返回: code=" + code + ", msg=" + msg + ", smsid=" + smsid);

        } catch (HttpException e) {
            // 捕获HTTP异常
            // 打印异常堆栈信息
            e.printStackTrace();
            // 在控制台输出HTTP请求异常信息
            System.out.println("HTTP请求异常: " + e.getMessage());
            // 返回验证码（调试用）
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("code", mobile_code).addData("error", "HTTP请求异常");
        } catch (IOException e) {
            // 捕获IO异常
            // 打印异常堆栈信息
            e.printStackTrace();
            // 在控制台输出IO异常信息
            System.out.println("IO异常: " + e.getMessage());
            // 返回验证码（调试用）
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("code", mobile_code).addData("error", "IO异常");
        } catch (DocumentException e) {
            // 捕获XML解析异常
            // 打印异常堆栈信息
            e.printStackTrace();
            // 在控制台输出XML解析异常信息
            System.out.println("XML解析异常: " + e.getMessage());
            // 在控制台输出原始响应内容
            System.out.println("原始响应内容: " + SubmitResult);
            // 返回验证码（调试用）
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("code", mobile_code).addData("error", "XML解析失败");
        }
        // 返回成功响应，包含验证码
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("code", mobile_code);
    }
    // 使用Autowired注解自动注入UpdateUserRedis实例
    @Autowired
    // 声明UpdateUserRedis对象，用于更新Redis中的用户信息
    UpdateUserRedis updateUserRedis;
    /**
     * 用户注册
     * 作者:李梦瑶
     * @param userinfo 用户信息
     * @return 注册结果
     */
    // 使用PostMapping注解映射POST请求到/registeruser路径
    @PostMapping("/registeruser")
    // 定义用户注册的方法，接收Map参数并返回R响应对象
        /**
     * registeruser
     * 
     * @author 李梦瑶
     */
    public R registeruser(@RequestBody Map<String,String> userinfo) {
        // 从参数中获取手机号
        String phone = userinfo.get("phone");
        // 从参数中获取密码
        String password = userinfo.get("password");
        // 从参数中获取验证码并转换为整数
        int code = Integer.parseInt(userinfo.get("code"));
        // 从Redis中获取存储的验证码
        String codenumStr = (String) redisTemplate.opsForValue().get("userphone:" + phone);
        // 判断验证码是否已失效
        if (codenumStr == null || "".equals(codenumStr)) {
            // 如果验证码为空，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("验证码已失效!");
        }
        // 将存储的验证码字符串转换为整数
        int codenum = Integer.parseInt(codenumStr);
        // 判断用户输入的验证码是否正确
        if (codenum != code) {
            // 如果验证码不匹配，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("验证码错误!");
        }
        // 创建新的User对象实例
            /**
     * User
     * 
     * @author 李梦瑶
     */
        User user = new User();
        // 设置User对象的手机号属性
        user.setPhone(phone);
        // 设置User对象的密码属性
        user.setPassword(password);
        // 调用service层的save方法保存用户信息
        boolean flag=userService.save(user);
        // 判断用户是否注册成功
        if(!flag){
            // 如果注册失败，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("注册失败，请重试试");
        }
        // 调用updateUserRedis的方法更新Redis中的用户数量
        updateUserRedis.cheangeuserinfoconut();
        // 返回成功响应，包含注册成功状态
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("status", "注册成功");
    }
   /**
     * 检查手机号是否存在
     * 作者:李梦瑶
     * @param phone 手机号
     * @return 检查结果
     */
    // 使用PostMapping注解映射POST请求到/checkphone路径
    @PostMapping("/checkphone")
    // 定义检查手机号是否存在的方法，接收String参数并返回R响应对象
        /**
     * checkusrname
     * 
     * @author 李梦瑶
     */
    public R checkusrname(@RequestBody String phone) {
        // 检查Redis中是否存在该手机号的登录数据
        boolean flag=redisTemplate.hasKey("userlogindata:"+phone);
        // 判断手机号是否已存在
        if (flag){
            // 如果手机号已存在，返回存在状态
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("status","手机号码已存在");
        }
        // 如果手机号不存在，返回不存在状态
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("status","手机号码不存在");
    }
}