package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.orderfoodafter.entity.Complaint;

import java.util.List;

/**
 * 投诉Mapper
 */
@Mapper
public interface ComplaintMapper extends BaseMapper<Complaint> {
    List<Complaint> selectComplaint(QueryWrapper<Complaint> queryWrapper);
}