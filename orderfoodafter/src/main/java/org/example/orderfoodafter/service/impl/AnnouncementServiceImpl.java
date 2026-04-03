package org.example.orderfoodafter.service.impl;

import org.example.orderfoodafter.entity.Announcement;
import org.example.orderfoodafter.mapper.AnnouncementMapper;
import org.example.orderfoodafter.service.AnnouncementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 公告Service实现类
 * 实现公告相关的业务逻辑处理功能
 * 
 * @author 李吉隆
 * @date 2025-11-17
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {
}