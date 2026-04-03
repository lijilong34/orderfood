package org.example.orderfoodafter.service;

import org.example.orderfoodafter.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * 消息Service接口
 * 提供消息相关的业务逻辑处理功能，包括消息信息的增删改查等操作
 *
 * @author 李梦瑶
 * @date 2025-12-15
 */
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
