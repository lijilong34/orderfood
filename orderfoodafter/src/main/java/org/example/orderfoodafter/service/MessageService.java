package org.example.orderfoodafter.service;

import org.example.orderfoodafter.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface MessageService extends IService<Message> {

    /**
     * 根据用户ID获取消息列表
     * @param userId 用户ID
     * @return 消息列表
     */
    List<Message> getMessagesByUserId(Long userId);

    /**
     * 删除消息
     * @param messageId 消息ID
     * @return 是否成功
     */
    boolean deleteMessage(Long messageId);
}
