package org.example.orderfoodafter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.UserFeedback;
import org.example.orderfoodafter.mapper.UserFeedbackMapper;
import org.example.orderfoodafter.service.UserFeedbackService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户反馈服务实现类
 */
@Service
public class UserFeedbackServiceImpl extends ServiceImpl<UserFeedbackMapper, UserFeedback> implements UserFeedbackService {
    @Override
    public List<UserFeedback> selectFeedback(QueryWrapper<UserFeedback> queryWrapper) {
        List<UserFeedback> userFeedbackList = baseMapper.selectFeedback(queryWrapper);
        return userFeedbackList;
    }
}