package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.orderfoodafter.entity.UserFeedback;

import java.util.List;

/**
 * 用户反馈Service接口
 * 提供用户反馈相关的业务逻辑处理功能，包括用户反馈信息的增删改查等操作
 *
 * @author 李梦瑶
 * @date 2025-12-05
 */
public interface UserFeedbackService extends IService<UserFeedback> {
    List<UserFeedback> selectFeedback(QueryWrapper<UserFeedback> queryWrapper);
}