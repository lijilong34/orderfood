package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.UserFeedback;

import java.util.List;

/**
 * 用户反馈Mapper接口
 */
public interface UserFeedbackMapper extends BaseMapper<UserFeedback> {
    List<UserFeedback> selectFeedback(@Param("ew") QueryWrapper<UserFeedback> queryWrapper);
    
    List<UserFeedback> selectFeedbackByCustomWhere(@Param("whereClause") String whereClause);
}