package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.orderfoodafter.entity.Complaint;

import java.util.List;

/**
 * 投诉Mapper接口
 * 用于对投诉表进行数据访问操作，提供投诉信息的增删改查功能
 *
 * @author 熊杨博
 * @date 2026-01-08
 */
@Mapper
public interface ComplaintMapper extends BaseMapper<Complaint> {
    List<Complaint> selectComplaint(QueryWrapper<Complaint> queryWrapper);
}