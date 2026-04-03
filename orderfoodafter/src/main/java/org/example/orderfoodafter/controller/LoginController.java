// 定义登录控制器的包路径
package org.example.orderfoodafter.controller;
// 导入MyBatis Plus的查询条件包装器类
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入JWT工具类
import org.example.orderfoodafter.common.JwtUtils;
// 导入通用响应结果类
import org.example.orderfoodafter.common.R;
// 导入更新用户Redis工具类
import org.example.orderfoodafter.common.UpdateUserRedis;
// 导入员工实体类
import org.example.orderfoodafter.entity.Employee;
// 导入用户实体类
import org.example.orderfoodafter.entity.Shop;
import org.example.orderfoodafter.entity.User;
// 导入员工服务接口
import org.example.orderfoodafter.service.EmployeeService;
// 导入用户服务接口
import org.example.orderfoodafter.service.UserService;
// 导入Spring的自动装配注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring的Redis模板类
import org.springframework.data.redis.core.RedisTemplate;
// 导入Spring的POST请求映射注解
import org.springframework.web.bind.annotation.PostMapping;
// 导入Spring的请求体参数注解
import org.springframework.web.bind.annotation.RequestBody;
// 导入Spring的REST控制器注解
import org.springframework.web.bind.annotation.RestController;
// 导入Java的Map接口
import java.util.Map;

/**
 * 登录控制器
 * 负责管理员和用户的登录验证功能，包括身份验证、令牌生成等
 * 
 * @author 赵康乐
 * @date 2025-11-18
 */
// 标记该类为REST控制器，所有方法返回JSON格式数据
@RestController
// 定义登录控制器类
/**
 * LoginController类
 *
 * @author 赵康乐
 * @date 2026-03-18
 */

public class LoginController {
// 自动注入员工服务，使用依赖注入方式获取实例
@Autowired
EmployeeService employeeService;
// 自动注入用户服务，使用依赖注入方式获取实例
@Autowired
UserService userService;
// 自动注入店铺服务，使用依赖注入方式获取实例
@Autowired
private org.example.orderfoodafter.service.ShopService shopService;
// 自动注入Redis模板，使用依赖注入方式获取实例
@Autowired
RedisTemplate redisTemplate;


/**
     * 管理员登录
     * 作者:赵康乐
     * @param loginlist 登录信息（包含用户名、密码、身份）
     * @return 登录结果（包含token、用户名、店铺ID）
     */
//管理人员登录方法
// 定义处理管理员登录的POST请求接口，路径为/managerlogin
@PostMapping("/managerlogin")
// 定义管理员登录的方法，接收登录信息Map对象
    /**
     * managerLogin
     * 
     * @author 赵康乐
     */
public R managerLogin(@RequestBody Map<String, Object> loginlist) {
    // 使用try-catch捕获可能的异常
    try {
        // 从登录信息中获取string
        // 用户名username
        String username = (String) loginlist.get("username");
        // 从登录信息中获取
        // 密码password
        String password = (String) loginlist.get("password");
        // 从登录信息中获取int
        // 身份标识identity（0=管理员，1=店长）
        int identity = (int) loginlist.get("Identity"); // 前端传递的身份（0=管理员，1=店长）

        // 创建查询条件包装器对象QW<Employee> a=ew QW<>();
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        // 设置查询条件：用户名等于传入的用户名a.eq
        queryWrapper.eq("username", username);
        // 设置查询条件：密码等于传入的密码a.eq
        queryWrapper.eq("password", password); // 注意：生产环境必须加密密码！

        // 核心修复1：按所选身份，强制匹配对应的shop_id规则
        // 判断身份是否为 管理员==0
        if (identity == 0) {
            // 管理员：a.eq必须shop_id=0
            queryWrapper.eq("shop_id", 0);
        // 判断身份是否为店长==1
        } else if (identity == 1) {
            // 店长：a.gt必须shop_id>0（排除管理员）
            queryWrapper.gt("shop_id", 0);
        } else {
            // 非法身份，直接返回失败
      /**t
     * RuntimeException
     * @author 赵康乐
     */
            throw new RuntimeException("身份选择异常，请重新选择！");
        }

        // a.last添加 LIMIT 1 防止查询返回多条记录导致错误
        queryWrapper.last("LIMIT 1");

        // 根据查询条件Em查询员工信息
        Employee employee = employeeService.getOne(queryWrapper);
        // 判断员工是否存在==空
        if (employee == null) {
            // 核心修复2：错误提示更精准，避免用户猜账号
      /**
     * RuntimeException
     * @author 赵康乐
     */
            throw new RuntimeException("用户名、密码或身份不匹配，请重试！");
        }

        // 如果是店长登录，检查店铺状态
        if (identity == 1 && employee.getShopId() != null && employee.getShopId() > 0) {
            // 根据店铺ID查询店铺信息shop
          Shop shop = shopService.getById(employee.getShopId());
            // 判断店铺状态是否为3（未通过审核）
            if (shop != null && shop.getStatus() != null && shop.getStatus() == 3) {
                // 如果店铺未通过审核，抛出异常
                    /**
     * RuntimeException
     * 
     * @author 赵康乐
     */
                throw new RuntimeException("所属店铺还未通过审核，无法登录！");
            }
        }

        // 生成JWT令牌（保留原有逻辑）
            /**
     * JwtUtils
     * 
     * @author 赵康乐
     */
        String token = new JwtUtils().generateToken(employee.getId() + "");
        // 返回令牌、用户名和店铺ID
            /**
     * R
     * 
     * @author 赵康乐
     */
        return new R().addData("token", token).addData("username", username).addData("shopid", employee.getShopId());
    // 捕获异常
    } catch (Exception e) {
        // 抛出运行时异常
            /**
     * RuntimeException
     * 
     * @author 赵康乐
     */
        throw new RuntimeException("登录失败: " + e.getMessage());
    }
}
/**
     * 用户登录
     * 作者:赵康乐
     * @param user 用户信息（包含手机号和密码）
     * @return 登录结果（包含token和用户信息）
     */
//用户登录
// 定义处理用户登录的POST请求接口，路径为/UserLogin
@PostMapping("/UserLogin")
// 定义用户登录的方法，接收用户实体对象
    /**
     * userLogin
     * 
     * @author 赵康乐
     */
public R userLogin(@RequestBody User user){
    // String获取用户的
    // 手机号phone
    String phone= user.getPhone();
    // 获取用户的
    // 密码password
    String password = user.getPassword();
    // 从Redis中获取该手机号的登录数据
    String password1=(String) redisTemplate.opsForValue().get("userlogindata:"+phone);
    // 判断密码是否匹配!a. equals(a1)
    if(!password.equals(password1)){
        // 如果不匹配，抛出异常
            /**
     * RuntimeException
     * @author 赵康乐
     */
        throw new RuntimeException("用户名或密码错误！");
    }
    // 在控制台输出已访问数据库的消息
    System.out.println("已访问数据库");
    // 创建查询条件包装器对象QW<User> a=new QW<>();
    QueryWrapper<User> queryWrapper=new QueryWrapper<>();
    // 设置查询条件：手机号等于传入的手机号a.eq
    queryWrapper.eq("phone",phone);
    // 设置查询条件：密码等于传入的密码a.eq
    queryWrapper.eq("password",password);
    // 根据查询条件查询user信息
    User user1=userService.getOne(queryWrapper);
    // 生成JWT令牌
        /**
      * to ken
     * JwtUtils
     * @author 赵康乐
     */
    String token=new JwtUtils().generateToken(user1.getId()+"");
    // return返回令牌和用户信息
        /**
     * R
     * @author 赵康乐
     */
    return new R().addData("token",token).addData("user",user1);
}
}
