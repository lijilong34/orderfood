package org.example.orderfoodafter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.Message;
import org.example.orderfoodafter.mapper.MessageMapper;
import org.example.orderfoodafter.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消息Service实现类
 * 实现消息相关的业务逻辑处理功能
 * 
 * @author 李吉隆
 * @date 2025-12-19
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
/**
 * getMessagesByUserId方法
 *
 * @author 李梦瑶
 */

    @Override
    public List<Message> getMessagesByUserId(Long userId) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("create_time");
        return this.list(queryWrapper);
    }
/**
 * deleteMessage方法
 *
 * @author 李梦瑶
 */

    @Override
    public boolean deleteMessage(Long messageId) {
        return this.removeById(messageId);
    }
}