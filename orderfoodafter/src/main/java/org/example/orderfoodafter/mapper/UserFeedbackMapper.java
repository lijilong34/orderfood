package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.UserFeedback;

import java.util.List;

/**
 * 用户反馈Mapper接口
 * 用于对用户反馈表进行数据访问操作，提供用户反馈信息的增删改查功能
 *
 * @author 李梦瑶，周子金
 * @date 2025-12-05
 */
public interface UserFeedbackMapper extends BaseMapper<UserFeedback> {
    List<UserFeedback> selectFeedback(@Param("ew") QueryWrapper<UserFeedback> queryWrapper);
    
    List<UserFeedback> selectFeedbackByCustomWhere(@Param("whereClause") String whereClause);
}