// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入MyBatis Plus的QueryWrapper类，用于构建查询条件
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入MyBatis Plus的UpdateWrapper类，用于构建更新条件
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
// 导入PageHelper的PageHelper类，用于分页
import com.github.pagehelper.PageHelper;
// 导入PageHelper的PageInfo类，用于分页信息封装
import com.github.pagehelper.PageInfo;
// 导入项目的JwtUtils工具类
import org.example.orderfoodafter.common.JwtUtils;
// 导入项目通用的R类，用于统一响应格式
import org.example.orderfoodafter.common.R;
// 导入项目的WhereEntity类
import org.example.orderfoodafter.common.WhereEntity;
// 导入UserFeedback实体类
import org.example.orderfoodafter.entity.UserFeedback;
// 导入UserFeedbackService服务接口
import org.example.orderfoodafter.service.UserFeedbackService;
// 导入Spring的Autowired注解，用于自动装配
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring HTTP的ResponseEntity类
import org.springframework.http.ResponseEntity;
// 导入Spring MVC的映射注解
import org.springframework.web.bind.annotation.*;

// 导入Java IO异常类IOException
import java.io.IOException;
// 导入Java日期类Date
import java.util.Date;
// 导入Java集合类HashMap
import java.util.HashMap;
// 导入Java集合类List
import java.util.List;
// 导入Java集合类Map
import java.util.Map;

/**
 * 用户反馈控制器
 * 负责管理用户的反馈功能，包括反馈提交、查询、回复、状态管理等操作
 *
 * @author 周子金
 * @date 2025-12-05
 */
// 使用RestController注解标记该类为RESTful控制器，所有方法返回JSON
@RestController
// 使用RequestMapping注解设置该控制器的基础请求路径为/feedback
@RequestMapping("/feedback")
// 定义UserFeedbackController类，继承自BaseController基类，泛型为UserFeedback和UserFeedbackService
/**
 * UserFeedbackController类
 *
 * @author 李梦瑶
 * @date 2026-03-18
 */

public class UserFeedbackController extends BaseController<UserFeedback, UserFeedbackService> {

    // 使用Autowired注解自动注入UserFeedbackService服务实例
    @Autowired
    // 声明UserFeedbackService服务对象
    private UserFeedbackService userFeedbackService;

    // 使用Autowired注解自动注入JwtUtils工具实例
    @Autowired
    // 声明JwtUtils工具对象
    private JwtUtils jwtUtils;

    // 定义UserFeedbackController的构造函数，接收UserFeedbackService参数
/**
 * UserFeedbackController方法
 *
 * @author 李梦瑶
 */
    public UserFeedbackController(UserFeedbackService service) {
        // 调用父类BaseController的构造函数，传入service
        super(service);
        // 将传入的service赋值给当前类的userFeedbackService属性
        this.userFeedbackService = service;
    }

    /**
     * 用户提交反馈
     * @param feedback 反馈信息
     * @param token JWT令牌
     * @return 提交结果
     */
    // 使用PostMapping注解映射POST请求到/submit路径
    @PostMapping("/submit")
    // 定义提交反馈的方法，接收UserFeedback对象和Authorization请求头，返回ResponseEntity
    public ResponseEntity<Map<String, Object>> submitFeedback(
            @RequestBody UserFeedback feedback,
            @RequestHeader("Authorization") String token) {

        // 创建响应Map
        Map<String, Object> response = new HashMap<>();

        // 使用try-catch块捕获异常
        try {
            // 去掉token前缀"Bearer "
            token = token.substring(7);
            // 从token中获取用户ID
            String subject = jwtUtils.getSubject(token);
            // 将用户ID转换为Long类型
            Long userId = Long.parseLong(subject);

            // 设置用户信息
            feedback.setUserId(userId);

            // 设置默认状态为待处理
            // 判断反馈状态是否为空
            if (feedback.getStatus() == null) {
                // 如果为空，设置为0（待处理）
                feedback.setStatus((byte) 0);
            }

            // 设置默认优先级为中
            // 判断优先级是否为空
            if (feedback.getPriority() == null) {
                // 如果为空，设置为2（中）
                feedback.setPriority((byte) 2);
            }

            // 设置创建时间为当前时间
                /**
     * 设置 setCreateTime
     * 
     * @param setCreateTime setCreateTime
     * @author 李梦瑶
     */
            feedback.setCreateTime(new Date());
            // 设置更新时间为当前时间
                /**
     * 设置 setUpdateTime
     * 
     * @param setUpdateTime setUpdateTime
     * @author 李梦瑶
     */
            feedback.setUpdateTime(new Date());

            // 保存反馈
            boolean result = userFeedbackService.save(feedback);

            // 判断保存是否成功
            if (result) {
                // 保存成功，设置响应状态码
                response.put("code", 200);
                // 设置响应消息
                response.put("message", "反馈提交成功");
                // 添加反馈数据到响应中
                response.put("data", feedback);
            } else {
                // 保存失败，设置响应状态码
                response.put("code", 500);
                // 设置响应消息
                response.put("message", "反馈提交失败");
            }

            // 返回成功响应
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 设置响应状态码
            response.put("code", 500);
            // 设置响应消息，包含错误信息
            response.put("message", "反馈提交失败: " + e.getMessage());
            // 返回服务器错误响应
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
    // 使用GetMapping注解映射GET请求到/list路径
    @GetMapping("/list")
    // 定义查询反馈列表的方法，接收多个请求参数和Authorization请求头，返回ResponseEntity
    public ResponseEntity<Map<String, Object>> getFeedbackList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Byte status,
            @RequestParam(required = false) Byte type,
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        // 创建响应Map
        Map<String, Object> response = new HashMap<>();

        // 使用try-catch块捕获异常
        try {
            // 设置分页参数
            PageHelper.startPage(page, pageSize);

            // 构建查询条件
            QueryWrapper<UserFeedback> queryWrapper = new QueryWrapper<>();

            // 如果有店铺ID参数，只查询该店铺的反馈
            // 判断shopId是否不为空
            if (shopId != null) {
                // 设置查询条件：shop_id等于传入的shopId
                queryWrapper.eq("shop_id", shopId);
            }

            // 如果有状态参数，按状态筛选
            // 判断status是否不为空
            if (status != null) {
                // 设置查询条件：status等于传入的status
                queryWrapper.eq("status", status);
            }

            // 如果有类型参数，按类型筛选
            // 判断type是否不为空
            if (type != null) {
                // 设置查询条件：type等于传入的type
                queryWrapper.eq("type", type);
            }

            // 按创建时间倒序排列
            queryWrapper.orderByDesc("create_time");

            // 查询反馈列表
            List<UserFeedback> feedbackList = userFeedbackService.selectFeedback(queryWrapper);
            // 创建分页信息对象
            PageInfo<UserFeedback> pageInfo = new PageInfo<>(feedbackList);

            // 设置响应状态码
            response.put("code", 200);
            // 设置响应消息
            response.put("message", "查询成功");
            // 添加分页信息到响应中
            response.put("data", pageInfo);

            // 返回成功响应
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 设置响应状态码
            response.put("code", 500);
            // 设置响应消息，包含错误信息
            response.put("message", "查询失败: " + e.getMessage());
            // 返回服务器错误响应
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 按用户ID查询反馈列表
     * @param token JWT令牌
     * @return 反馈列表
     */
    // 使用PostMapping注解映射POST请求到/selectByUser路径
    @PostMapping("/selectByUser")
    // 定义按用户ID查询反馈列表的方法，接收Authorization请求头，返回R响应对象
        /**
     * selectFeedbackByUserId
     * 
     * @author 李梦瑶
     */
    public R selectFeedbackByUserId(@RequestHeader("Authorization") String token) {
        // 使用try-catch块捕获异常
        try {
            // 去掉Bearer前缀
            token = token.substring(7);
            // 从token中获取用户ID
            String subject = jwtUtils.getSubject(token);
            // 将用户ID转换为Long类型
            Long userId = Long.parseLong(subject);

            // 构建查询条件
            QueryWrapper<UserFeedback> queryWrapper = new QueryWrapper<>();
            // 设置查询条件：user_id等于当前用户ID
            queryWrapper.eq("user_id", userId);

            // 按创建时间倒序排列
            queryWrapper.orderByDesc("create_time");

            // 查询反馈列表
            List<UserFeedback> feedbackList = userFeedbackService.selectFeedback(queryWrapper);

            // 返回成功响应，包含反馈列表
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("list", feedbackList);
        } catch (Exception e) {
            // 捕获异常，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询反馈详情
     * @param id 反馈ID
     * @return 反馈详情
     */
    // 使用GetMapping注解映射GET请求到/{id}路径
    @GetMapping("/{id}")
    // 定义查询反馈详情的方法，接收路径参数id，返回ResponseEntity
/**
 * getFeedbackById方法
 *
 * @author 李梦瑶
 */
    public ResponseEntity<Map<String, Object>> getFeedbackById(@PathVariable Long id) {
        // 创建响应Map
        Map<String, Object> response = new HashMap<>();

        // 使用try-catch块捕获异常
        try {
            // 根据ID查询反馈信息
            UserFeedback feedback = userFeedbackService.getById(id);

            // 判断反馈是否存在
            if (feedback == null) {
                // 如果反馈不存在，设置响应状态码
                response.put("code", 404);
                // 设置响应消息
                response.put("message", "反馈不存在");
                // 返回未找到响应
                return ResponseEntity.status(404).body(response);
            }

            // 设置响应状态码
            response.put("code", 200);
            // 设置响应消息
            response.put("message", "查询成功");
            // 添加反馈数据到响应中
            response.put("data", feedback);

            // 返回成功响应
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 设置响应状态码
            response.put("code", 500);
            // 设置响应消息，包含错误信息
            response.put("message", "查询失败: " + e.getMessage());
            // 返回服务器错误响应
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 更新反馈状态和回复
     * @param request 包含id、status、reply、handler的请求体
     * @param token JWT令牌
     * @return 更新结果
     */
    // 使用PutMapping注解映射PUT请求到/status路径
    @PutMapping("/status")
    // 定义更新反馈状态的方法，接收Map参数和Authorization请求头，返回ResponseEntity
    public ResponseEntity<Map<String, Object>> updateFeedbackStatus(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String token) {

        // 创建响应Map
        Map<String, Object> response = new HashMap<>();

        // 使用try-catch块捕获异常
        try {
            // 获取参数
            // 获取反馈ID并转换为Long类型
            Long id = request.get("id") != null ? Long.parseLong(request.get("id").toString()) : null;
            // 获取状态并转换为Byte类型
            Byte status = request.get("status") != null ? Byte.parseByte(request.get("status").toString()) : null;
            // 获取回复内容
            String reply = request.get("reply") != null ? request.get("reply").toString() : null;
            // 获取处理人
            String handler = request.get("handler") != null ? request.get("handler").toString() : null;

            // 验证必要参数
            // 判断反馈ID是否为空
            if (id == null) {
                // 如果反馈ID为空，设置响应状态码
                response.put("code", 400);
                // 设置响应消息
                response.put("message", "反馈ID不能为空");
                // 返回错误请求响应
                return ResponseEntity.badRequest().body(response);
            }

            // 判断状态是否为空
            if (status == null) {
                // 如果状态为空，设置响应状态码
                response.put("code", 400);
                // 设置响应消息
                response.put("message", "状态不能为空");
                // 返回错误请求响应
                return ResponseEntity.badRequest().body(response);
            }

            // 检查反馈是否存在
            UserFeedback existingFeedback = userFeedbackService.getById(id);
            // 判断反馈是否存在
            if (existingFeedback == null) {
                // 如果反馈不存在，设置响应状态码
                response.put("code", 404);
                // 设置响应消息
                response.put("message", "反馈不存在");
                // 返回未找到响应
                return ResponseEntity.status(404).body(response);
            }

            // 更新反馈信息
            // 创建更新条件构建器
            UpdateWrapper<UserFeedback> updateWrapper = new UpdateWrapper<>();
            // 设置更新条件：id等于传入的id
            updateWrapper.eq("id", id);

            // 判断状态是否不为空
            if (status != null) {
                // 设置更新字段：status等于传入的status
                updateWrapper.set("status", status);
            }

            // 判断回复内容是否不为空
            if (reply != null) {
                // 设置更新字段：reply等于传入的reply
                updateWrapper.set("reply", reply);
            }

            // 如果有回复内容，设置回复时间
            // 判断reply是否不为空且不为空字符串
            if (reply != null && !reply.isEmpty()) {
                // 设置更新字段：reply_time等于当前时间
                    /**
     * 设置 set
     * 
     * @param set set
     * @author 李梦瑶
     */
                updateWrapper.set("reply_time", new Date());
            }

            // 判断处理人是否不为空
            if (handler != null) {
                // 设置更新字段：handler等于传入的handler
                updateWrapper.set("handler", handler);
            }

            // 设置更新时间
                /**
     * 设置 set
     * 
     * @param set set
     * @author 李梦瑶
     */
            updateWrapper.set("update_time", new Date());

            // 执行更新操作
            boolean result = userFeedbackService.update(updateWrapper);

            // 判断更新是否成功
            if (result) {
                // 更新成功，设置响应状态码
                response.put("code", 200);
                // 设置响应消息
                response.put("message", "更新成功");
            } else {
                // 更新失败，设置响应状态码
                response.put("code", 500);
                // 设置响应消息
                response.put("message", "更新失败");
            }

            // 返回成功响应
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 设置响应状态码
            response.put("code", 500);
            // 设置响应消息，包含错误信息
            response.put("message", "更新失败: " + e.getMessage());
            // 返回服务器错误响应
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 删除反馈
     * @param id 反馈ID
     * @return 删除结果
     */
    // 使用DeleteMapping注解映射DELETE请求到/{id}路径
    @DeleteMapping("/{id}")
    // 定义删除反馈的方法，接收路径参数id，返回ResponseEntity
/**
 * deleteFeedback方法
 *
 * @author 李梦瑶
 */
    public ResponseEntity<Map<String, Object>> deleteFeedback(@PathVariable Long id) {
        // 创建响应Map
        Map<String, Object> response = new HashMap<>();

        // 使用try-catch块捕获异常
        try {
            // 检查反馈是否存在
            UserFeedback feedback = userFeedbackService.getById(id);
            // 判断反馈是否存在
            if (feedback == null) {
                // 如果反馈不存在，设置响应状态码
                response.put("code", 404);
                // 设置响应消息
                response.put("message", "反馈不存在");
                // 返回未找到响应
                return ResponseEntity.status(404).body(response);
            }

            // 删除反馈
            boolean result = userFeedbackService.removeById(id);

            // 判断删除是否成功
            if (result) {
                // 删除成功，设置响应状态码
                response.put("code", 200);
                // 设置响应消息
                response.put("message", "删除成功");
            } else {
                // 删除失败，设置响应状态码
                response.put("code", 500);
                // 设置响应消息
                response.put("message", "删除失败");
            }

            // 返回成功响应
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 设置响应状态码
            response.put("code", 500);
            // 设置响应消息，包含错误信息
            response.put("message", "删除失败: " + e.getMessage());
            // 返回服务器错误响应
            return ResponseEntity.status(500).body(response);
        }
    }
    // 使用PostMapping注解映射POST请求到/selectfeedbackforadmin路径
    @PostMapping("/selectfeedbackforadmin")
    // 定义管理员查询反馈的方法，接收Map参数，返回R响应对象
/**
 * selectFeedbackForAdmin方法
 *
 * @author 李梦瑶
 */
    public R selectFeedbackForAdmin(@RequestBody Map<String,Object> selectwhere) throws IOException {
    // 在控制台输出查询条件
    System.out.println(selectwhere);
        // 获取查询条件列表
        List where = (List) selectwhere.get("where");
        // 初始化页码为1
        int pageno = 1;
        // 判断是否包含page参数
        if (selectwhere.get("page") != null) {
            // 将page参数转换为整数
            pageno = Integer.parseInt(selectwhere.get("page").toString());
        }
        // 调用通用工具构建查询条件
        QueryWrapper queryWrapper = commontUtil.getWhere(where);
        // 设置分页参数
        PageHelper.startPage(pageno,10);
        // 查询反馈列表
        List<UserFeedback> userFeedbackList=userFeedbackService.selectFeedback(queryWrapper);
        // 创建分页信息对象
        PageInfo<UserFeedback> pageInfo = new PageInfo<>(userFeedbackList);
        // 返回成功响应，包含分页信息
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("pageInfo", pageInfo);
    }
}