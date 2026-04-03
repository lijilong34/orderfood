// 定义员工菜单权限控制器的包路径
package org.example.orderfoodafter.controller;
// 导入MyBatis Plus的查询条件包装器类
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入通用响应结果类
import org.example.orderfoodafter.common.R;
// 导入员工菜单实体类
import org.example.orderfoodafter.entity.EmployeeMenus;
// 导入员工菜单服务接口
import org.example.orderfoodafter.service.EmployeeMenusService;
// 导入Spring的自动装配注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring的POST请求映射注解
import org.springframework.web.bind.annotation.PostMapping;
// 导入Spring的请求体参数注解
import org.springframework.web.bind.annotation.RequestBody;
// 导入Spring的请求映射注解
import org.springframework.web.bind.annotation.RequestMapping;
// 导入Spring的REST控制器注解
import org.springframework.web.bind.annotation.RestController;
// 导入Java的List集合类
import java.util.List;
// 导入Java的Map接口
import java.util.Map;

/**
 * 员工菜单权限控制器
 * 负责管理员工的菜单权限，包括为员工分配菜单、删除菜单权限等操作
 * 
 * @author 李梦瑶
 * @date 2025-12-10
 */
// 标记该类为REST控制器，所有方法返回JSON格式数据
@RestController
// 定义该控制器的请求路径前缀为/EmployeeMenus
@RequestMapping("/EmployeeMenus")
// 定义员工菜单权限控制器类，继承基础控制器，指定实体类型为EmployeeMenus，服务类型为EmployeeMenusService
/**
 * EmployeeMenusController类
 *
 * @author 李梦瑶
 * @date 2026-03-18
 */

public class EmployeeMenusController extends BaseController<EmployeeMenus,EmployeeMenusService>{
   // 自动注入员工菜单服务，使用依赖注入方式获取实例
   @Autowired
    private EmployeeMenusService employeeMenusService;
   // 构造函数，接收员工菜单服务作为参数
/**
 * EmployeeMenusController方法
 *
 * @author 李梦瑶
 */
   public EmployeeMenusController(EmployeeMenusService employeeMenusService) {
       // 调用父类构造函数，传入服务实例
       super(employeeMenusService);
       // 将传入的服务实例赋值给本地变量
       this.employeeMenusService = employeeMenusService;
   }
   // 定义处理删除员工菜单权限的POST请求接口，路径为/delEmployeeMenus
   @PostMapping("/delEmployeeMenus")
    // 定义删除员工菜单权限的方法，接收查询条件Map参数，可能抛出异常
/**
 * delEmployeeMenus方法
 *
 * @author 李梦瑶
 */
    public R delEmployeeMenus(@RequestBody Map<String,Object> selectwhere) throws Exception{
       // 从请求参数中获取查询条件列表
       List where = (List) selectwhere.get("where");
       // 使用通用工具类将查询条件列表转换为MyBatis Plus的查询包装器对象
       QueryWrapper queryWrapper = commontUtil.getWhere(where);
       // 调用员工菜单服务的remove方法，根据查询条件删除员工菜单权限
       boolean flag=employeeMenusService.remove(queryWrapper);
       // 判断删除是否成功
       if(flag){
           // 如果删除成功，返回成功状态消息
               /**
     * R
     * 
     * @author 李梦瑶
     */
           return new R().addData("status","删除成功");
       }
       // 如果删除失败，抛出运行时异常
           /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
       throw new RuntimeException("删除失败");
   }
   // 定义处理添加员工菜单权限的POST请求接口，路径为/addEmployeeMenus
   @PostMapping("/addEmployeeMenus")
    // 定义添加员工菜单权限的方法，接收包含员工ID和菜单ID列表的Map参数，可能抛出异常
/**
 * addEmployeeMenus方法
 *
 * @author 李梦瑶
 */
    public R addEmployeeMenus(@RequestBody Map<String,Object> map) throws Exception{
     // 调用员工菜单服务的addEmployeeMenus方法，将员工ID和菜单ID列表关联保存
     boolean flag=employeeMenusService.addEmployeeMenus(Integer.parseInt(map.get("employeeId")+""),(List)map.get("menuIds"));
     // 判断添加是否成功
     if(flag){
         // 如果添加成功，返回成功状态消息
             /**
     * R
     * 
     * @author 李梦瑶
     */
         return new R().addData("status","添加成功");
     }
     // 如果添加失败，抛出运行时异常
         /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
     throw new RuntimeException("添加失败");
   }
}