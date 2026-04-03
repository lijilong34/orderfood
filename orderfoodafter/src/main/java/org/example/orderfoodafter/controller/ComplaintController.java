// 定义当前类的包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入MyBatis-Plus的查询包装器类，用于构建查询条件
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入PageHelper分页工具类，用于分页查询
import com.github.pagehelper.PageHelper;
// 导入PageInfo分页信息类，用于存储分页结果
import com.github.pagehelper.PageInfo;
// 导入统一响应类R，用于封装API响应
import org.example.orderfoodafter.common.R;
// 导入投诉实体类
import org.example.orderfoodafter.entity.Complaint;
// 导入商品实体类
import org.example.orderfoodafter.entity.Product;
// 导入投诉服务接口
import org.example.orderfoodafter.service.ComplaintService;
// 导入商品服务接口
import org.example.orderfoodafter.service.ProductService;
// 导入Spring的自动装配注解，用于依赖注入
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring MVC的注解集合，包括@GetMapping、@PostMapping等
import org.springframework.web.bind.annotation.*;

// 导入工具类集合
import java.util.*;
// 导入Stream流处理相关类
import java.util.stream.Collectors;

/**
 * 投诉管理控制器
 * 负责管理用户的投诉功能，包括投诉提交、查询、处理、状态管理等操作
 *
 * @author 熊杨博
 * @date 2026-01-08
 */
// 使用@RestController注解标记该类为REST控制器，所有方法返回JSON数据
@RestController
// 使用@RequestMapping注解设置该控制器的所有接口基础路径为/complaint
@RequestMapping("/complaint")
// 定义ComplaintController类，继承BaseController基类，泛型为Complaint和ComplaintService


public class ComplaintController extends BaseController<Complaint, ComplaintService> {

    // 使用@Autowired注解自动注入ComplaintService服务实例
    @Autowired
    // 声明投诉服务接口的私有成员变量
    private ComplaintService complaintService;

    // 使用@Autowired注解自动注入ProductService服务实例
    @Autowired
    // 声明商品服务接口的私有成员变量
    private ProductService productService;

    // 定义带参构造函数，接收ComplaintService服务实例参数

    public ComplaintController(ComplaintService service) {  // 投诉服务实例
        // 调用父类构造函数，传入服务实例
        super(service);
        // 将传入的服务实例赋值给成员变量
        this.complaintService = service;
    }

    /**
     * 添加投诉
     * 作者:熊杨博
     * @param token 用户令牌
     * @param complaint 投诉信息
     * @return 添加结果
     */
    // 使用@PostMapping注解映射POST请求到/complaint/addComplaint路径
    @PostMapping("/addComplaint")
    // 定义添加投诉的方法，返回类型为R，接收请求头token和请求体complaint
        /**
     * addComplaint
     * 
     * @author 熊杨博
     */
    public R addComplaint(@RequestHeader("Authorization") String token,@RequestBody Complaint complaint) {  // 用户令牌和投诉信息
        // 使用try-catch捕获可能的异常
        try {
            // 设置默认状态为待处理
            // 判断投诉状态是否为null
            if (complaint.getStatus() == null) {
                // 设置状态为0（待处理）
                complaint.setStatus((byte) 0);
            }
            // 去除token的前缀"Bearer "
            token = token.substring(7);

            // 从token中获取用户信息
            String subject = jwtUtils.getSubject(token);

            // 将用户信息转换为长整型
            Long userId = Long.parseLong(subject);

            // 设置用户ID
            complaint.setUserId(userId);

            // 设置创建时间
            complaint.setCreateTime(new Date());

            // 设置更新时间
            complaint.setUpdateTime(new Date());

            // 保存投诉
            // 调用服务层的save方法保存投诉
            boolean result = complaintService.save(complaint);

            // 判断保存是否成功
            if (result) {
                // 返回成功响应

                return new R().addData("status", "投诉提交成功");
            } else {
                // 抛出运行时异常，提示投诉提交失败

                throw new RuntimeException("投诉提交失败");
            }
        } catch (Exception e) {  // 捕获异常

            // 抛出运行时异常，包含错误信息
            throw new RuntimeException("投诉提交失败: " + e.getMessage());
        }
    }
    /**
     * 删除投诉
     * 作者:熊杨博
     * @param id 投诉ID
     * @return 删除结果
     */
    // 使用@DeleteMapping注解映射DELETE请求到/complaint/{id}路径
    @DeleteMapping("/{id}")
    // 定义删除投诉的方法，返回类型为R，接收路径参数id

    public R deleteComplaint(@PathVariable Long id) {  // 投诉ID
        // 使用try-catch捕获可能的异常
        try {
            // 根据ID查询投诉信息
            Complaint complaint = complaintService.getById(id);

            // 判断投诉是否存在
            if (complaint == null) {

                // 抛出运行时异常，提示投诉不存在
                throw new RuntimeException("投诉不存在");
            }

            // 判断状态：待处理(status=0)的情况下禁止删除
            // 判断投诉状态是否为待处理
            if (complaint.getStatus() != null && complaint.getStatus() == 0) {

                // 抛出运行时异常，提示待处理状态的投诉不能删除
                throw new RuntimeException("待处理状态的投诉不能删除");
            }

            // 根据ID删除投诉
            boolean result = complaintService.removeById(id);
            // 判断删除是否成功
            if (result) {
                // 返回成功响应
                return new R().addData("status","删除成功");
            } else {
                // 抛出运行时异常，提示删除失败
                throw new RuntimeException("删除失败");
            }
        } catch (Exception e) {  // 捕获异常
            // 抛出运行时异常，包含错误信息
            throw new RuntimeException("删除失败: " + e.getMessage());
        }
    }
    /**
     * 获取投诉列表
     * 作者:熊杨博
     * @param params 查询参数
     * @return 投诉列表
     */
    // 使用@PostMapping注解映射POST请求到/complaint/selectcomplaint路径
    @PostMapping("/selectcomplaint")
    // 定义获取投诉列表的方法，返回类型为R，接收查询参数映射
        /**
     * 获取 getComplaintList
     * 
     * @return getComplaintList
     * @author 熊杨博
     */
    public R getComplaintList(@RequestBody Map<String, Object> params) {  // 查询参数映射
        // 使用try-catch捕获可能的异常
        try {
            // 获取页码，默认为1
            int page = params.containsKey("page") ? Integer.parseInt(params.get("page").toString()) : 1;
            // 获取每页大小，默认为10
            int pageSize = params.containsKey("pageSize") ? Integer.parseInt(params.get("pageSize").toString()) : 10;
            // 获取店铺ID，默认为0
            int shopId = params.containsKey("shopId") ? Integer.parseInt(params.get("shopId").toString()) : 0;

            // 启动分页
            PageHelper.startPage(page, pageSize);

            // 创建查询包装器对象
            QueryWrapper<Complaint> queryWrapper = new QueryWrapper<>();
            // 添加店铺ID等于条件
            queryWrapper.eq("shop_id", shopId);
            // 按创建时间降序排列
            queryWrapper.orderByDesc("create_time");

            // 调用服务层方法查询投诉列表
            List<Complaint> complaints = complaintService.selectComplaint(queryWrapper);
            // 创建分页信息对象
            PageInfo<Complaint> pageInfo = new PageInfo<>(complaints);
            // 返回包含分页信息的响应
                /**
     * R
     * 
     * @author 熊杨博
     */
            return new R().addData("pageInfo", pageInfo);
        } catch (Exception e) {  // 捕获异常
            // 抛出运行时异常，包含错误信息
                /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
            throw new RuntimeException("获取投诉列表失败: " + e.getMessage());
        }
    }



    /**
     * 处理投诉
     * 作者:熊杨博
     * @param params 处理参数（包含投诉ID、状态、处理人等）
     * @return 处理结果
     */
    // 使用@PutMapping注解映射PUT请求到/complaint/handle路径
    @PutMapping("/handle")
    // 定义处理投诉的方法，返回类型为R，接收处理参数映射
        /**
     * handleComplaint
     * 
     * @author 熊杨博
     */
    public R handleComplaint(@RequestBody Map<String, Object> params) {  // 处理参数映射
        // 使用try-catch捕获可能的异常
        try {
            // 获取投诉ID
            Long id = Long.parseLong(params.get("id").toString());
            // 获取状态，默认为1
            Integer status = params.containsKey("status") ? Integer.parseInt(params.get("status").toString()) : 1;
            // 获取处理人
            String handler = params.containsKey("handler") ? params.get("handler").toString() : null;

            // 根据ID查询投诉信息
            Complaint complaint = complaintService.getById(id);
            // 判断投诉是否存在
            if (complaint == null) {
                // 抛出运行时异常，提示投诉不存在
                    /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                throw new RuntimeException("投诉不存在");
            }

            // 设置投诉状态
            complaint.setStatus(status.byteValue());
            // 设置更新时间
                /**
     * 设置 setUpdateTime
     * 
     * @param setUpdateTime setUpdateTime
     * @author 熊杨博
     */
            complaint.setUpdateTime(new Date());

            // 更新投诉信息
            boolean result = complaintService.updateById(complaint);
            // 判断更新是否成功
            if (result) {
                // 返回成功响应
                    /**
     * R
     * 
     * @author 熊杨博
     */
                return new R().addData("status","处理成功");
            } else {
                // 抛出运行时异常，提示处理失败
                    /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                throw new RuntimeException("处理失败");
            }
        } catch (Exception e) {  // 捕获异常
            // 抛出运行时异常，包含错误信息
                /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
            throw new RuntimeException("处理失败: " + e.getMessage());
        }
    }

    /**
     * 修改投诉状态
     * 作者:熊杨博
     * @param params 修改参数（包含投诉ID和状态）
     * @return 修改结果
     */
    // 使用@PostMapping注解映射POST请求到/complaint/updateStatus路径
    @PostMapping("/updateStatus")
    // 定义修改投诉状态的方法，返回类型为R，接收修改参数映射
        /**
     * updateStatus
     * 
     * @author 熊杨博
     */
    public R updateStatus(@RequestBody Map<String, Object> params) {  // 修改参数映射
        // 使用try-catch捕获可能的异常
        try {
            // 获取投诉ID
            Long id = Long.parseLong(params.get("id").toString());
            // 获取状态
            Integer status = Integer.parseInt(params.get("status").toString());

            // 根据ID查询投诉信息
            Complaint complaint = complaintService.getById(id);
            // 判断投诉是否存在
            if (complaint == null) {
                // 抛出运行时异常，提示投诉不存在
                    /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                throw new RuntimeException("投诉不存在");
            }

            // 设置投诉状态
            complaint.setStatus(status.byteValue());
            // 设置更新时间
                /**
     * 设置 setUpdateTime
     * 
     * @param setUpdateTime setUpdateTime
     * @author 熊杨博
     */
            complaint.setUpdateTime(new Date());



            // 更新投诉信息
            boolean result = complaintService.updateById(complaint);
            // 判断更新是否成功
            if (result) {
                // 返回成功响应
                    /**
     * R
     * 
     * @author 熊杨博
     */
                return new R().addData("status","状态修改成功");
            } else {
                // 抛出运行时异常，提示状态修改失败
                    /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
                throw new RuntimeException("状态修改失败");
            }
        } catch (Exception e) {  // 捕获异常
            // 抛出运行时异常，包含错误信息
                /**
     * RuntimeException
     * 
     * @author 熊杨博
     */
            throw new RuntimeException("状态修改失败: " + e.getMessage());
        }
    }
// ComplaintController类定义结束
}