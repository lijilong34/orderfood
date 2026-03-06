package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.orderfoodafter.entity.Complaint;

import java.util.List;

/**
 * 投诉Service
 */
public interface ComplaintService extends IService<Complaint> {
    List<Complaint> selectComplaint(QueryWrapper<Complaint> queryWrapper);
}