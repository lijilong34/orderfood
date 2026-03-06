package org.example.orderfoodafter.controller;

import org.example.orderfoodafter.common.JwtUtils;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Message;
import org.example.orderfoodafter.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息通知控制器
 */
@RequestMapping("/message")
@RestController
public class MessageController extends BaseController<Message, MessageService> {
   public MessageController(MessageService service) {
       super(service);
       this.service = service;
   }
    @Autowired
    private MessageService messageService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 获取当前用户的消息列表
     */
    @PostMapping("/list")
    public R getMessageList(@RequestHeader("Authorization") String token) {
        try {
            // 去掉Bearer前缀
            token = token.substring(7);
            // 从token中获取用户ID
            String subject = jwtUtils.getSubject(token);
            Long userId = Long.parseLong(subject);

            List<Message> messageList = messageService.getMessagesByUserId(userId);
            return R.ok().addData("data", messageList);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取消息列表失败");
        }
    }

    /**
     * 删除消息
     */
    @GetMapping("/delete/{messageId}")
    public R deleteMessage(@PathVariable Long messageId) {
        try {
            boolean result = messageService.deleteMessage(messageId);
            if (result) {
                return R.ok();
            } else {
                return R.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("删除失败");
        }
    }
}