package org.example.orderfoodafter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.UserFeedback;
import org.example.orderfoodafter.mapper.UserFeedbackMapper;
import org.example.orderfoodafter.service.UserFeedbackService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户反馈Service实现类
 * 实现用户反馈相关的业务逻辑处理功能
 * 
 * @author 李吉隆
 * @date 2025-12-09
 */
@Service
public class UserFeedbackServiceImpl extends ServiceImpl<UserFeedbackMapper, UserFeedback> implements UserFeedbackService {
/**
 * selectFeedback方法
 *
 * @author 李梦瑶
 */
    @Override
    public List<UserFeedback> selectFeedback(QueryWrapper<UserFeedback> queryWrapper) {
        List<UserFeedback> userFeedbackList = baseMapper.selectFeedback(queryWrapper);
        return userFeedbackList;
    }
}