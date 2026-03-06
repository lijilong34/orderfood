package org.example.orderfoodafter.controller;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.common.UpdateUserRedis;
import org.example.orderfoodafter.entity.Mobile;
import org.example.orderfoodafter.entity.User;
import org.example.orderfoodafter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 描述：接收验证码控制类
 * 日期：2025/12/6
 */
@RequestMapping("/register")
@RestController
public class RegisterApiController extends BaseController<User,UserService> {
    @Autowired
    private UserService userService;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    public RegisterApiController(UserService userService) {
        super(userService);
        this.userService = userService;
    }

    /**
     * 描述：获取验证码
     * @param phone
     * @return
     */
    @PostMapping("/sendCode")
    public R  takecode(@RequestBody Map<String,String> phone) {
//http://106.ihuyi.com/webservice/sms.php?method=Submit
        String Url = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";
        //http协议请求链接的一个对象
        HttpClient client = new HttpClient();
        //请求方式
        PostMethod method = new PostMethod(Url);

        client.getParams().setContentCharset("UTF-8");
        method.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");

        //获取随机数
        int mobile_code = (int) ((Math.random() * 9 + 1) * 100000); //获取随机数

        //content  内容
        String content = new String("您的验证码是：" + mobile_code + "。请不要把验证码泄露给其他人。");

        String phone1=phone.get("phone");

        NameValuePair[] data = {//提交短信
                new NameValuePair("account", Mobile.account), //查看用户名是登录用户中心->验证码短信->产品总览->APIID
                new NameValuePair("password", Mobile.password),  //查看密码请登录用户中心->验证码短信->产品总览->APIKEY
                //new NameValuePair("password", util.StringUtil.MD5Encode("密码")),
                new NameValuePair("mobile", phone1),
                new NameValuePair("content", content),
        };
        //请求携带的数据
        method.setRequestBody(data);
        redisTemplate.opsForValue().set("userphone:"+phone1,mobile_code+"",5, TimeUnit.MINUTES);
        String SubmitResult = "";
        try{
            //发起请求
            int statusCode = client.executeMethod(method);
            //响应数据
            SubmitResult = method.getResponseBodyAsString();

            System.out.println("响应状态码: " + statusCode);
            System.out.println("响应内容: " + SubmitResult);

            // 检查响应是否为空
            if (SubmitResult == null || SubmitResult.trim().isEmpty()) {
                System.out.println("警告: 短信API返回空响应，状态码: " + statusCode);
                // 即使API响应失败，也返回生成的验证码（调试用）
                return new R().addData("code", mobile_code).addData("warn", "短信API无响应");
            }

            // 尝试解析XML
            Document doc = DocumentHelper.parseText(SubmitResult);
            Element root = doc.getRootElement();

            String code = root.elementText("code");
            String msg = root.elementText("msg");
            String smsid = root.elementText("smsid");

            System.out.println("短信API返回: code=" + code + ", msg=" + msg + ", smsid=" + smsid);

        } catch (HttpException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("HTTP请求异常: " + e.getMessage());
            // 返回验证码（调试用）
            return new R().addData("code", mobile_code).addData("error", "HTTP请求异常");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("IO异常: " + e.getMessage());
            // 返回验证码（调试用）
            return new R().addData("code", mobile_code).addData("error", "IO异常");
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("XML解析异常: " + e.getMessage());
            System.out.println("原始响应内容: " + SubmitResult);
            // 返回验证码（调试用）
            return new R().addData("code", mobile_code).addData("error", "XML解析失败");
        }
        return new R().addData("code", mobile_code);
    }
    @Autowired
    UpdateUserRedis updateUserRedis;
    @PostMapping("/registeruser")
    public R registeruser(@RequestBody Map<String,String> userinfo) {
        String phone = userinfo.get("phone");
        String password = userinfo.get("password");
        int code = Integer.parseInt(userinfo.get("code"));
        String codenumStr = (String) redisTemplate.opsForValue().get("userphone:" + phone);
        if (codenumStr == null || "".equals(codenumStr)) {
            throw new RuntimeException("验证码已失效!");
        }
        int codenum = Integer.parseInt(codenumStr);
        if (codenum != code) {
            throw new RuntimeException("验证码错误!");
        }
        User user = new User();
        user.setPhone(phone);
        user.setPassword(password);
        boolean flag=userService.save(user);
        if(!flag){
            throw new RuntimeException("注册失败，请重试试");
        }
        updateUserRedis.cheangeuserinfoconut();
        return new R().addData("status", "注册成功");
    }
   @PostMapping("/checkphone")
    public R checkusrname(@RequestBody String phone) {
        boolean flag=redisTemplate.hasKey("userlogindata:"+phone);
        if (flag){
            return new R().addData("status","手机号码已存在");
        }
        return new R().addData("status","手机号码不存在");
    }
}

