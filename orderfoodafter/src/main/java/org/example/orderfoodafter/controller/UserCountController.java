// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入项目通用的R类，用于统一响应格式
import org.example.orderfoodafter.common.R;
// 导入UserService服务接口
import org.example.orderfoodafter.service.UserService;
// 导入Spring的Autowired注解，用于自动装配
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring MVC的映射注解
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 导入Java集合类HashMap
import java.util.HashMap;
// 导入Java集合类Map
import java.util.Map;

/**
 * 用户数量统计控制器
 * 负责统计用户数量信息，提供用户总数等统计数据
 *
 * @author 李吉隆
 * @date 2026-02-01
 */
// 使用RestController注解标记该类为RESTful控制器，所有方法返回JSON
@RestController
// 使用RequestMapping注解设置该控制器的基础请求路径为/user-count
@RequestMapping("/user-count")
// 定义UserCountController类
/**
 * UserCountController类
 *
 * @author 李吉隆
 * @date 2026-03-18
 */

public class UserCountController {

    // 使用Autowired注解自动注入UserService服务实例
    @Autowired
    // 声明UserService服务对象
    private UserService userService;

    /**
     * 获取用户总数
     */
    // 使用GetMapping注解映射GET请求到/total路径
    @GetMapping("/total")
    // 定义获取用户总数的方法，返回R响应对象
        /**
     * 获取 getTotalUserCount
     * 
     * @return getTotalUserCount
     * @author 李吉隆
     */
    public R getTotalUserCount() {
        // 使用try-catch块捕获异常
        try {
            // 获取用户总数
            long totalCount = userService.count();

            // 创建结果Map
            Map<String, Object> result = new HashMap<>();
            // 将用户总数添加到结果中
            result.put("totalUsers", totalCount);

            // 返回成功响应，包含用户数量信息
            return R.ok().addData("userCount", result);
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("获取用户数量失败: " + e.getMessage());
        }
    }
}