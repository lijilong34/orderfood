package org.example.orderfoodafter.controller;

import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户数量统计控制器
 */
@RestController
@RequestMapping("/user-count")
public class UserCountController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户总数
     */
    @GetMapping("/total")
    public R getTotalUserCount() {
        try {
            // 获取用户总数
            long totalCount = userService.count();
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalUsers", totalCount);
            
            return R.ok().addData("userCount", result);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取用户数量失败: " + e.getMessage());
        }
    }
}