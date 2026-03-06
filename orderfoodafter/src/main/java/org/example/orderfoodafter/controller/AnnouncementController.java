package org.example.orderfoodafter.controller;

import org.example.orderfoodafter.entity.Announcement;
import org.example.orderfoodafter.service.AnnouncementService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    // 分页查询公告
    @PostMapping("/selectbylike")
    public ResponseEntity<Map<String, Object>> selectByLike(@RequestBody Map<String, Object> params) {
        int page = params.get("page") != null ? (Integer) params.get("page") : 1;
        List<Map<String, Object>> whereList = (List<Map<String, Object>>) params.get("where");

        Page<Announcement> pageParam = new Page<>(page, 8);
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();

        // 根据条件查询
        if (whereList != null) {
            for (Map<String, Object> condition : whereList) {
                String column = (String) condition.get("column");
                String type = (String) condition.get("type");
                Object value = condition.get("value");

                if ("title".equals(column)) {
                    if ("like".equals(type)) {
                        queryWrapper.like(StringUtils.isNotEmpty((String) value), Announcement::getTitle, value);
                    } else if ("eq".equals(type)) {
                        queryWrapper.eq(StringUtils.isNotEmpty((String) value), Announcement::getTitle, value);
                    }
                } else if ("status".equals(column)) {
                    if ("eq".equals(type)) {
                        queryWrapper.eq(value != null, Announcement::getStatus, value);
                    }
                }
            }
        }

        // 默认按发布时间倒序排列
        queryWrapper.orderByDesc(Announcement::getPublishTime);

        Page<Announcement> result = announcementService.page(pageParam, queryWrapper);

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("list", result.getRecords());
        pageInfo.put("total", result.getTotal());
        pageInfo.put("pages", result.getPages());

        response.put("data", Map.of("pageInfo", pageInfo));
        response.put("code", 200);
        response.put("message", "查询成功");

        return ResponseEntity.ok(response);
    }

    // 根据ID获取公告详情
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAnnouncementById(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("data", Map.of("entity", announcement));
            response.put("code", 200);
            response.put("message", "查询成功");
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "公告不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // 添加公告
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addAnnouncement(@RequestBody Announcement announcement) {
        try {
            // 设置创建时间和更新时间
            Date now = new Date();
            announcement.setCreateTime(now);
            announcement.setUpdateTime(now);
            // 如果发布时间为空，则设置为当前时间
            if (announcement.getPublishTime() == null) {
                announcement.setPublishTime(now);
            }
            
            boolean result = announcementService.save(announcement);
            if (result) {
                Map<String, Object> response = new HashMap<>();
                response.put("data", Map.of("status", "添加成功"));
                response.put("code", 200);
                response.put("message", "添加成功");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 500);
                response.put("message", "添加失败");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "添加失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 更新公告
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateAnnouncement(@RequestBody Announcement announcement) {
        try {
            // 设置更新时间
            announcement.setUpdateTime(new Date());
            
            boolean result = announcementService.updateById(announcement);
            if (result) {
                Map<String, Object> response = new HashMap<>();
                response.put("data", Map.of("status", "修改成功"));
                response.put("code", 200);
                response.put("message", "修改成功");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 500);
                response.put("message", "修改失败");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "修改失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 删除公告
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteAnnouncement(@PathVariable Long id) {
        try {
            boolean result = announcementService.removeById(id);
            if (result) {
                Map<String, Object> response = new HashMap<>();
                response.put("data", Map.of("status", "删除成功"));
                response.put("code", 200);
                response.put("message", "删除成功");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 500);
                response.put("message", "删除失败");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}