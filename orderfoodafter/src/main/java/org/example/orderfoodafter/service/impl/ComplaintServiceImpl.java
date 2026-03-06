package org.example.orderfoodafter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.Complaint;
import org.example.orderfoodafter.mapper.ComplaintMapper;
import org.example.orderfoodafter.service.ComplaintService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 投诉Service实现
 */
@Service
public class ComplaintServiceImpl extends ServiceImpl<ComplaintMapper, Complaint> implements ComplaintService {
    @Override
    public List<Complaint> selectComplaint(QueryWrapper<Complaint> queryWrapper) {
        List<Complaint> complaints = baseMapper.selectComplaint(queryWrapper);
        return complaints;
    }
}