// 定义忘记密码控制器的包路径
package org.example.orderfoodafter.controller;
// 导入MyBatis Plus的查询条件包装器类
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入通用响应结果类
import org.example.orderfoodafter.common.R;
// 导入用户实体类
import org.example.orderfoodafter.entity.User;
// 导入用户服务接口
import org.example.orderfoodafter.service.UserService;
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

/**
 * 忘记密码控制器
 * 负责处理用户忘记密码的场景，包括验证手机号、重置密码等功能
 * 
 * @author 赵康乐
 * @date 2025-11-19
 */
// 定义该控制器的请求路径前缀为/ForgetPassword
@RequestMapping("/ForgetPassword")
// 标记该类为REST控制器，所有方法返回JSON格式数据
@RestController
// 定义忘记密码控制器类，继承基础控制器，指定实体类型为User，服务类型为UserService
/**
 * ForgetPasswordController类
 *
 * @author 赵康乐
 * @date 2026-03-18
 */

public class ForgetPasswordController extends BaseController<User, UserService> {
    // 自动注入用户服务，使用依赖注入方式获取实例
    @Autowired
    public UserService userService;
    // 构造函数，接收用户服务作为参数
/**
 * ForgetPasswordController方法
 *
 * @author 赵康乐
 */
    public ForgetPasswordController(UserService userService){
        // 调用父类构造函数，传入服务实例
        super(userService);
        // 将传入的服务实例赋值给本地变量
        this.userService=userService;
    }

    /**
     * 判断手机号码是否存在
     * @param phone
     */
    // 定义处理检查手机号是否存在的POST请求接口，路径为/selectphoneisexits
    @PostMapping("/selectphoneisexits")
    // 定义检查手机号是否存在的方法，接收手机号参数
        /**
     * selectphoneisexists
     * QueryWrapper<User>
     * @author 赵康乐
     */
    public R  selectphoneisexists(@RequestBody long phone){
        // qu创建查询条件包装器对
        QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();
        // us设置查询条件：手机号等于传入的手机号
        userQueryWrapper.eq("phone",phone);
        // 调用服务检查是否存在该手机号的用户bo
        boolean phonestatus=userService.exists(userQueryWrapper);
        // 判断/手机号ph是否存在
        if (phonestatus){
            // 如果存在，返回成功状态消息
                /**
     * R
     * 
     * @author 赵康乐
     */
            return new R().addData("status","手机号码存在");
        }
        // 如果不存在，抛出运行时异常
            /**
     * RuntimeException
     * throw
     * @author 赵康乐
     */
        throw new RuntimeException("手机号码不存在");
    }
    // 定义处理更新密码的POST请求接口，路径为/updatepassword
    @PostMapping("/updatepassword")
    // 定义更新密码的方法，接收用户实体对象
/**
 * updatepassword方法
 *QueryWrapper<User>
 * @author 赵康乐
 */
    public R updatepassword(@RequestBody User user){
        // 创建查询条件包装器对象
        QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();
        // 设置查询条件：手机号等于用户的手机号
        userQueryWrapper.eq("phone",user.getPhone());
        // 设置查询条件：密码等于用户的密码
        userQueryWrapper.eq("password",user.getPassword());
        // 调用服务更新用户密码
        boolean flag=userService.update(user, userQueryWrapper);
        // 判断/更新f g是否成功
        if (flag){
            // 如果更新成功，返回成功状态消息
                /**
     * R
     * 
     * @author 赵康乐
     */
            return new R().addData("status","密码修改成功");
        }
        // 如果更新失败，抛出运行时异常
            /**
     * RuntimeException
     * throw
     * @author 赵康乐
     */
        throw new RuntimeException("你的旧密码与原密码相同");
    }
}
