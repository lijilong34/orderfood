// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入JWT工具类
import org.example.orderfoodafter.common.JwtUtils;
// 导入通用返回结果类R
import org.example.orderfoodafter.common.R;
// 导入消息实体类
import org.example.orderfoodafter.entity.Message;
// 导入消息服务类
import org.example.orderfoodafter.service.MessageService;
// 导入Spring框架的自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring Web的绑定注解
import org.springframework.web.bind.annotation.*;

// 导入Java集合框架的List接口
import java.util.List;

/**
 * 消息通知控制器
 * 负责管理系统中的消息通知功能，包括获取用户消息列表、删除消息等操作
 * 
 * @author 李梦瑶
 * @date 2025-12-15
 */
// 设置请求映射路径为/message
@RequestMapping("/message")
// 使用RestController注解标识这是一个REST控制器
@RestController
// 定义MessageController控制器类，继承BaseController基础控制器
/**
 * MessageController类
 *
 * @author 李梦瑶
 * @date 2026-03-18
 */

public class MessageController extends BaseController<Message, MessageService> {
   // 定义构造函数，接收MessageService服务参数
/**
 * MessageController方法
 *
 * @author 李梦瑶
 */
   public MessageController(MessageService service) {
       // 调用父类构造函数，传入服务实例
       super(service);
       // 将服务实例赋值给当前类的成员变量
       this.service = service;
   }
   // 使用Autowired注解自动注入MessageService服务
   @Autowired
   // 声明MessageService服务实例
   private MessageService messageService;

   // 使用Autowired注解自动注入JwtUtils工具类
   @Autowired
   // 声明JwtUtils工具实例
   private JwtUtils jwtUtils;

    /**
     * 获取当前用户的消息列表
     * 作者:李梦瑶
     * @param token 用户令牌
     * @return 消息列表
     */
    // 使用PostMapping注解映射POST请求到/list路径
    @PostMapping("/list")
    // 定义获取消息列表的方法，从请求头获取token，返回R类型对象
        /**
     * 获取 getMessageList
     * 
     * @return getMessageList
     * @author 李梦瑶
     */
    public R getMessageList(@RequestHeader("Authorization") String token) {
        // 使用try-catch块处理可能的异常
        try {
            // 去掉Bearer前缀
            token = token.substring(7);
            // 从token中获取用户ID
            String subject = jwtUtils.getSubject(token);
            // 将用户ID转换为Long类型
            Long userId = Long.parseLong(subject);

            // 调用服务层根据用户ID获取消息列表
            List<Message> messageList = messageService.getMessagesByUserId(userId);
            // 返回消息列表
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("data", messageList);
        } catch (Exception e) {
            // 捕获异常，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("获取消息列表失败");
        }
    }

    /**
     * 删除消息
     * 作者:李梦瑶
     * @param messageId 消息ID
     * @return 删除结果
     */
    // 使用GetMapping注解映射GET请求到/delete/{messageId}路径
    @GetMapping("/delete/{messageId}")
    // 定义删除消息的方法，从路径变量获取消息ID，返回R类型对象
/**
 * deleteMessage方法
 *
 * @author 李梦瑶
 */
    public R deleteMessage(@PathVariable Long messageId) {
        // 使用try-catch块处理可能的异常
        try {
            // 调用服务层删除消息
            boolean result = messageService.deleteMessage(messageId);
            // 判断删除结果
            if (result) {
                // 删除成功，返回成功消息
                    /**
     * R
     * 
     * @author 李梦瑶
     */
                return new R().addData("status", "删除成功");
            } else {
                // 删除失败，抛出异常
                    /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
                throw new RuntimeException("删除失败");
            }
        } catch (Exception e) {
            // 捕获异常，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("删除失败");
        }
    }
}