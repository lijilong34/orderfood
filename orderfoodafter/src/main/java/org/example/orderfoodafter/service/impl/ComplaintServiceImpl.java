package org.example.orderfoodafter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.Complaint;
import org.example.orderfoodafter.mapper.ComplaintMapper;
import org.example.orderfoodafter.service.ComplaintService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 投诉Service实现类
 * 实现投诉相关的业务逻辑处理功能
 * 
 * @author 李吉隆
 * @date 2026-01-12
 */
@Service
public class ComplaintServiceImpl extends ServiceImpl<ComplaintMapper, Complaint> implements ComplaintService {
/**
 * selectComplaint方法
 *
 * @author 李梦瑶
 */
    @Override
    public List<Complaint> selectComplaint(QueryWrapper<Complaint> queryWrapper) {
        List<Complaint> complaints = baseMapper.selectComplaint(queryWrapper);
        return complaints;
    }
}