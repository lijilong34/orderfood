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
// 导入员工实体类
import org.example.orderfoodafter.entity.Employee;
// 导入员工服务接口
import org.example.orderfoodafter.service.EmployeeService;
// 导入店铺服务接口
import org.example.orderfoodafter.service.ShopService;
// 导入Spring的自动装配注解，用于依赖注入
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring MVC的注解集合，包括@GetMapping、@PostMapping等
import org.springframework.web.bind.annotation.*;

// 导入List集合接口，用于列表操作
import java.util.List;
// 导入Map接口，用于映射操作
import java.util.Map;

/**
 * 员工管理控制器
 * 提供员工的增删改查功能，包括员工暂停、恢复、查询等操作
 *
 * @author 李梦瑶
 * @date 2025-12-08
 */
// 使用@RestController注解标记该类为REST控制器，所有方法返回JSON数据
@RestController
// 使用@RequestMapping注解设置该控制器的所有接口基础路径为/employee
@RequestMapping("/employee")
// 定义EmployeeController类，继承BaseController基类，泛型为Employee和EmployeeService
/**
 * EmployeeController类
 *
 * @author 李梦瑶
 * @date 2026-03-18
 */

public class EmployeeController extends BaseController<Employee, EmployeeService> {

    // 使用@Autowired注解标记构造函数
    @Autowired
    // 定义带参构造函数，接收EmployeeService服务实例参数
/**
 * EmployeeController方法
 *
 * @author 李梦瑶
 */
    public EmployeeController(EmployeeService service) {  // 员工服务实例
        // 调用父类构造函数，传入服务实例
        super(service);
    }

    /**
     * 暂停员工
     */
    // 使用@PostMapping注解映射POST请求到/employee/pause/{id}路径
    @PostMapping("/pause/{id}")
    // 定义暂停员工的方法，返回类型为R，接收路径参数id和可选的请求体params
/**
 * pauseEmployee方法
 *
 * @author 李梦瑶
 */
    public R pauseEmployee(@PathVariable("id") Long id, @RequestBody(required = false) Map<String, Object> params) {  // 员工ID和参数映射
        // 使用try-catch捕获可能的异常
        try {
            // 根据ID查询员工信息
            Employee employee = service.getById(id);
            // 判断员工是否存在
            if (employee != null) {
                // 验证shopId
                // 判断是否包含店铺ID
                if (params != null && params.get("shopId") != null) {
                    // 获取店铺ID并转换为长整型
                    Long shopId = Long.parseLong(params.get("shopId").toString());
                    // 判断店铺ID是否匹配
                    if (!employee.getShopId().equals(shopId)) {
                        // 抛出运行时异常，提示不能操作其他店铺的员工
                            /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
                        throw new RuntimeException("不能操作其他店铺的员工");
                    }
                }
                // 设置工作状态为0（休假/暂停）
                employee.setWorkStatus((byte) 0); // 0表示休假/暂停
                // 更新员工信息
                service.updateById(employee);
                // 返回成功响应
                    /**
     * R
     * 
     * @author 李梦瑶
     */
                return new R().addData("status", "员工已暂停");
            }
            // 抛出运行时异常，提示员工不存在
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("员工不存在");
        } catch (Exception e) {  // 捕获异常
            // 抛出运行时异常，包含错误信息
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("暂停员工失败: " + e.getMessage());
        }
    }

    /**
     * 恢复员工工作
     */
    // 使用@PostMapping注解映射POST请求到/employee/resume/{id}路径
    @PostMapping("/resume/{id}")
    // 定义恢复员工工作的方法，返回类型为R，接收路径参数id和可选的请求体params
/**
 * resumeEmployee方法
 *
 * @author 李梦瑶
 */
    public R resumeEmployee(@PathVariable("id") Long id, @RequestBody(required = false) Map<String, Object> params) {  // 员工ID和参数映射
        // 使用try-catch捕获可能的异常
        try {
            // 根据ID查询员工信息
            Employee employee = service.getById(id);
            // 判断员工是否存在
            if (employee != null) {
                // 验证shopId
                // 判断是否包含店铺ID
                if (params != null && params.get("shopId") != null) {
                    // 获取店铺ID并转换为长整型
                    Long shopId = Long.parseLong(params.get("shopId").toString());
                    // 判断店铺ID是否匹配
                    if (!employee.getShopId().equals(shopId)) {
                        // 抛出运行时异常，提示不能操作其他店铺的员工
                            /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
                        throw new RuntimeException("不能操作其他店铺的员工");
                    }
                }
                // 设置工作状态为1（在岗）
                employee.setWorkStatus((byte) 1); // 1表示在岗
                // 更新员工信息
                service.updateById(employee);
                // 返回成功响应
                    /**
     * R
     * 
     * @author 李梦瑶
     */
                return new R().addData("status", "员工已恢复工作");
            }
            // 抛出运行时异常，提示员工不存在
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("员工不存在");
        } catch (Exception e) {  // 捕获异常
            // 抛出运行时异常，包含错误信息
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("恢复员工工作失败: " + e.getMessage());
        }
    }
    /*
    连接商店表查询员工
     */
    // 使用@PostMapping注解映射POST请求到/employee/selectemployee路径
    @PostMapping("/selectemployee")
    // 定义查询员工的方法，返回类型为R，接收查询条件映射
/**
 * selectemployee方法
 *
 * @author 李梦瑶
 */
    public R selectemployee(@RequestBody Map<String, Object> selectwhere)throws Exception{  // 查询条件映射
        // 获取where条件列表
        List where = (List) selectwhere.get("where");

        // 调用commontUtil的getWhere方法构建查询包装器
        QueryWrapper queryWrapper = commontUtil.getWhere(where);

        // 添加shopId过滤条件
        // 判断是否包含店铺ID
        if (selectwhere.get("shopId") != null) {
            // 添加店铺ID等于条件
            queryWrapper.eq("shop_id", selectwhere.get("shopId"));
        }

        // 判断是否需要分页
        if (selectwhere.get("page") != null) {
            // 获取页码并转换为整数
            int page = Integer.parseInt(selectwhere.get("page").toString());

            // 启动分页，每页6条记录
            PageHelper.startPage(page, 6);
        }

        // 调用服务层方法查询员工列表
        List<Employee> employeeList=service.selectemployee(queryWrapper);
        // 创建分页信息对象
        PageInfo pageInfo = new PageInfo<>(employeeList);

        // 返回包含分页信息的响应
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("pageInfo", pageInfo);
    }

    /**
     * 重写select方法，添加shopId过滤，确保只查询当前店铺的员工
     */
    // 使用@PostMapping注解映射POST请求到/employee/select路径
    @PostMapping("/select")
    // 定义查询员工的方法，返回类型为R，接收查询条件映射
        /**
     * select
     * 
     * @author 李梦瑶
     */
    public R select(@RequestBody Map<String, Object> selectwhere) throws Exception {  // 查询条件映射
        // 获取where条件列表
        List where = (List) selectwhere.get("where");

        // 调用commontUtil的getWhere方法构建查询包装器
        QueryWrapper queryWrapper = commontUtil.getWhere(where);

        // 添加shopId过滤条件
        // 判断是否包含店铺ID
        if (selectwhere.get("shopId") != null) {
            // 添加店铺ID等于条件
            queryWrapper.eq("shop_id", selectwhere.get("shopId"));
        }

        // 判断是否需要分页
        if (selectwhere.get("page") != null) {
            // 获取页码并转换为整数
            int page = Integer.parseInt(selectwhere.get("page").toString());
            // 启动分页，每页10条记录
            PageHelper.startPage(page, 10);
        }

        // 调用服务层方法查询员工列表
        List<Employee> list = service.list(queryWrapper);
        // 创建分页信息对象
        PageInfo pageInfo = new PageInfo<>(list);

        // 返回包含分页信息的响应
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("pageInfo", pageInfo);
    }
    // 使用@PostMapping注解映射POST请求到/employee/selectemployeebyemployee路径
    @PostMapping("/selectemployeebyemployee")
    // 定义根据员工信息查询的方法，返回类型为R，接收参数映射
/**
 * selectemployeebyemployee方法
 *
 * @author 李梦瑶
 */
    public R selectemployeebyemployee(@RequestBody Map<String, Object> params){  // 参数映射
        // 获取用户名
        String username = params.get("username") != null ? params.get("username").toString() : null;
        // 获取密码
        String password = params.get("password") != null ? params.get("password").toString() : null;
        // 获取店铺ID
        Long shopId = params.get("shopId") != null ? Long.parseLong(params.get("shopId").toString()) : null;

        // 创建查询包装器对象
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        // 添加用户名等于条件
        queryWrapper.eq("username", username);
        // 添加密码等于条件
        queryWrapper.eq("password", password);

        // 如果提供了shopId，则验证员工是否属于该店铺
        // 判断店铺ID是否不为null
        if (shopId != null) {
            // 添加店铺ID等于条件
            queryWrapper.eq("shop_id", shopId);
        }

        // 调用服务层方法查询单个员工
        Employee employee = service.getOne(queryWrapper);
        // 返回包含员工对象的响应
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("employee", employee);
    }

    /**
     * 重写add方法，设置shopId，确保员工关联到当前店铺
     */
    // 使用@PostMapping注解映射POST请求到/employee/add路径
    @PostMapping("/add")
    // 定义添加员工的方法，返回类型为R，接收员工对象
        /**
     * add
     * 
     * @author 李梦瑶
     */
    public R add(@RequestBody Employee employee) throws Exception {  // 员工对象
        // 如果没有设置shopId，使用默认值（从请求参数中获取或使用其他方式）
        // 判断店铺ID是否为null
        if (employee.getShopId() == null) {
            // 这里可以根据实际需求设置默认shopId
            // 例如：employee.setShopId(1L);
            // 或者从session中获取
        }
        // 调用服务层的save方法保存员工
        boolean flag = service.save(employee);
        // 判断保存是否成功
        if (flag) {
            // 返回成功响应
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("status", "添加成功");
        }
        // 抛出运行时异常，提示添加失败
            /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
        throw new RuntimeException("添加失败");
    }

    /**
     * 重写update方法，验证shopId，确保只能更新当前店铺的员工
     */
    // 使用@PostMapping注解映射POST请求到/employee/update路径
    @PostMapping("/update")
    // 定义更新员工的方法，返回类型为R，接收员工对象
        /**
     * update
     * 
     * @author 李梦瑶
     */
    public R update(@RequestBody Employee employee) throws Exception {  // 员工对象
        // 先查询原员工信息
        Employee existingEmployee = service.getById(employee.getId());
        // 判断员工是否存在
        if (existingEmployee == null) {
            // 抛出运行时异常，提示员工不存在
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("员工不存在");
        }

        // 验证shopId是否匹配（如果请求中提供了shopId）
        // 判断店铺ID是否不为null且与原员工店铺ID不匹配
        if (employee.getShopId() != null && !employee.getShopId().equals(existingEmployee.getShopId())) {
            // 抛出运行时异常，提示不能修改员工的所属店铺
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("不能修改员工的所属店铺");
        }

        // 保持原有的shopId
        // 设置店铺ID为原员工的店铺ID
        employee.setShopId(existingEmployee.getShopId());

        // 调用服务层的updateById方法更新员工
        boolean flag = service.updateById(employee);
        // 判断更新是否成功
        if (flag) {
            // 返回成功响应
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("status", "修改成功");
        }
        // 抛出运行时异常，提示修改失败
            /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
        throw new RuntimeException("修改失败");
    }

    /**
     * 重写selectbyid方法，验证shopId，确保只能查询当前店铺的员工
     */
    // 使用@GetMapping注解映射GET请求到/employee/selectbyidWithShop/{id}路径
    @GetMapping("/selectbyidWithShop/{id}")
    // 定义根据ID查询员工的方法，返回类型为R，接收路径参数id和可选的请求参数shopId
/**
 * selectbyidWithShop方法
 *
 * @author 李梦瑶
 */
    public R selectbyidWithShop(@PathVariable("id") Long id, @RequestParam(required = false) Long shopId) throws Exception {  // 员工ID和店铺ID
        // 根据ID查询员工信息
        Employee employee = service.getById(id);
        // 判断员工是否存在
        if (employee == null) {
            // 抛出运行时异常，提示员工不存在
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("员工不存在");
        }

        // 验证shopId
        // 判断店铺ID是否不为null且与员工店铺ID不匹配
        if (shopId != null && !employee.getShopId().equals(shopId)) {
            // 抛出运行时异常，提示不能查询其他店铺的员工
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("不能查询其他店铺的员工");
        }

        // 返回包含员工对象的响应
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("entity", employee);
    }
// EmployeeController类定义结束
}
