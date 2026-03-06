package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.orderfoodafter.entity.UserFeedback;

import java.util.List;

/**
 * 用户反馈服务接口
 */
public interface UserFeedbackService extends IService<UserFeedback> {
    List<UserFeedback> selectFeedback(QueryWrapper<UserFeedback> queryWrapper);
}