package org.example.orderfoodafter.service.impl;

import org.example.orderfoodafter.entity.Announcement;
import org.example.orderfoodafter.mapper.AnnouncementMapper;
import org.example.orderfoodafter.service.AnnouncementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {
}