package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.orderfoodafter.entity.Complaint;

import java.util.List;

/**
 * 投诉Service接口
 * 提供投诉相关的业务逻辑处理功能，包括投诉信息的增删改查等操作
 *
 * @author 熊杨博
 * @date 2026-01-08
 */
public interface ComplaintService extends IService<Complaint> {
    List<Complaint> selectComplaint(QueryWrapper<Complaint> queryWrapper);
}