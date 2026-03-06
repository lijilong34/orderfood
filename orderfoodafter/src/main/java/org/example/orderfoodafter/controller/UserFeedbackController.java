package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.common.JwtUtils;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.common.WhereEntity;
import org.example.orderfoodafter.entity.UserFeedback;
import org.example.orderfoodafter.service.UserFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户反馈控制器
 */
@RestController
@RequestMapping("/feedback")
public class UserFeedbackController extends BaseController<UserFeedback, UserFeedbackService> {

    @Autowired
    private UserFeedbackService userFeedbackService;

    @Autowired
    private JwtUtils jwtUtils;

    public UserFeedbackController(UserFeedbackService service) {
        super(service);
        this.userFeedbackService = service;
    }

    /**
     * 用户提交反馈
     * @param feedback 反馈信息
     * @param token JWT令牌
     * @return 提交结果
     */
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitFeedback(
            @RequestBody UserFeedback feedback,
            @RequestHeader("Authorization") String token) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            token = token.substring(7);
            String subject = jwtUtils.getSubject(token);
            Long userId = Long.parseLong(subject);
            
            // 设置用户信息
            feedback.setUserId(userId);
            
            // 设置默认状态为待处理
            if (feedback.getStatus() == null) {
                feedback.setStatus((byte) 0);
            }
            
            // 设置默认优先级为中
            if (feedback.getPriority() == null) {
                feedback.setPriority((byte) 2);
            }
            
            // 设置创建时间
            feedback.setCreateTime(new Date());
            feedback.setUpdateTime(new Date());
            
            // 保存反馈
            boolean result = userFeedbackService.save(feedback);
            
            if (result) {
                response.put("code", 200);
                response.put("message", "反馈提交成功");
                response.put("data", feedback);
            } else {
                response.put("code", 500);
                response.put("message", "反馈提交失败");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "反馈提交失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 分页查询反馈列表
     * @param page 页码
     * @param pageSize 每页大小
     * @param status 状态（可选）
     * @param type 类型（可选）
     * @param shopId 店铺ID（可选）
     * @param token JWT令牌
     * @return 反馈列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getFeedbackList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Byte status,
            @RequestParam(required = false) Byte type,
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 设置分页
            PageHelper.startPage(page, pageSize);
            
            // 构建查询条件
            QueryWrapper<UserFeedback> queryWrapper = new QueryWrapper<>();
            
            // 如果有店铺ID参数，只查询该店铺的反馈
            if (shopId != null) {
                queryWrapper.eq("shop_id", shopId);
            }
            
            // 如果有状态参数，按状态筛选
            if (status != null) {
                queryWrapper.eq("status", status);
            }
            
            // 如果有类型参数，按类型筛选
            if (type != null) {
                queryWrapper.eq("type", type);
            }
            
            // 按创建时间倒序排列
            queryWrapper.orderByDesc("create_time");
            
            // 查询反馈列表
            List<UserFeedback> feedbackList = userFeedbackService.selectFeedback(queryWrapper);
            PageInfo<UserFeedback> pageInfo = new PageInfo<>(feedbackList);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", pageInfo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 按用户ID查询反馈列表
     * @param token JWT令牌
     * @return 反馈列表
     */
    @PostMapping("/selectByUser")
    public R selectFeedbackByUserId(@RequestHeader("Authorization") String token) {
        try {
            // 去掉Bearer前缀
            token = token.substring(7);
            // 从token中获取用户ID
            String subject = jwtUtils.getSubject(token);
            Long userId = Long.parseLong(subject);
            
            // 构建查询条件
            QueryWrapper<UserFeedback> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            
            // 按创建时间倒序排列
            queryWrapper.orderByDesc("create_time");
            
            // 查询反馈列表
            List<UserFeedback> feedbackList = userFeedbackService.selectFeedback(queryWrapper);
            
            return R.ok().addData("list", feedbackList);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询反馈详情
     * @param id 反馈ID
     * @return 反馈详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getFeedbackById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            UserFeedback feedback = userFeedbackService.getById(id);
            
            if (feedback == null) {
                response.put("code", 404);
                response.put("message", "反馈不存在");
                return ResponseEntity.status(404).body(response);
            }
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", feedback);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 更新反馈状态和回复
     * @param request 包含id、status、reply、handler的请求体
     * @param token JWT令牌
     * @return 更新结果
     */
    @PutMapping("/status")
    public ResponseEntity<Map<String, Object>> updateFeedbackStatus(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String token) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取参数
            Long id = request.get("id") != null ? Long.parseLong(request.get("id").toString()) : null;
            Byte status = request.get("status") != null ? Byte.parseByte(request.get("status").toString()) : null;
            String reply = request.get("reply") != null ? request.get("reply").toString() : null;
            String handler = request.get("handler") != null ? request.get("handler").toString() : null;
            
            // 验证必要参数
            if (id == null) {
                response.put("code", 400);
                response.put("message", "反馈ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (status == null) {
                response.put("code", 400);
                response.put("message", "状态不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 检查反馈是否存在
            UserFeedback existingFeedback = userFeedbackService.getById(id);
            if (existingFeedback == null) {
                response.put("code", 404);
                response.put("message", "反馈不存在");
                return ResponseEntity.status(404).body(response);
            }
            
            // 更新反馈信息
            UpdateWrapper<UserFeedback> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", id);
            
            if (status != null) {
                updateWrapper.set("status", status);
            }
            
            if (reply != null) {
                updateWrapper.set("reply", reply);
            }
            
            // 如果有回复内容，设置回复时间
            if (reply != null && !reply.isEmpty()) {
                updateWrapper.set("reply_time", new Date());
            }
            
            if (handler != null) {
                updateWrapper.set("handler", handler);
            }
            
            // 设置更新时间
            updateWrapper.set("update_time", new Date());
            
            boolean result = userFeedbackService.update(updateWrapper);
            
            if (result) {
                response.put("code", 200);
                response.put("message", "更新成功");
            } else {
                response.put("code", 500);
                response.put("message", "更新失败");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 删除反馈
     * @param id 反馈ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteFeedback(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 检查反馈是否存在
            UserFeedback feedback = userFeedbackService.getById(id);
            if (feedback == null) {
                response.put("code", 404);
                response.put("message", "反馈不存在");
                return ResponseEntity.status(404).body(response);
            }
            
            // 删除反馈
            boolean result = userFeedbackService.removeById(id);
            
            if (result) {
                response.put("code", 200);
                response.put("message", "删除成功");
            } else {
                response.put("code", 500);
                response.put("message", "删除失败");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    @PostMapping("/selectfeedbackforadmin")
    public R selectFeedbackForAdmin(@RequestBody Map<String,Object> selectwhere) throws IOException {
    System.out.println(selectwhere);
        List where = (List) selectwhere.get("where");
        int pageno = 1;
        if (selectwhere.get("page") != null) {
            pageno = Integer.parseInt(selectwhere.get("page").toString());
        }
        QueryWrapper queryWrapper = commontUtil.getWhere(where);
        PageHelper.startPage(pageno,10);
        List<UserFeedback> userFeedbackList=userFeedbackService.selectFeedback(queryWrapper);
        PageInfo<UserFeedback> pageInfo = new PageInfo<>(userFeedbackList);
        return new R().addData("pageInfo", pageInfo);
    }
}