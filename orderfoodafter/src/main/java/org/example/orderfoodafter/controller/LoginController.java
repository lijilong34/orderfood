package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.common.JwtUtils;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.common.UpdateUserRedis;
import org.example.orderfoodafter.entity.Employee;
import org.example.orderfoodafter.entity.User;
import org.example.orderfoodafter.service.EmployeeService;
import org.example.orderfoodafter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 描述:登录控制类
 * 日期：2025/12/2
 * 作者：李吉隆
 */
@RestController
public class LoginController {
@Autowired
EmployeeService employeeService;
@Autowired
UserService userService;
@Autowired
private org.example.orderfoodafter.service.ShopService shopService;
@Autowired
RedisTemplate redisTemplate;


//管理人员登录方法
@PostMapping("/managerlogin")
public R managerLogin(@RequestBody Map<String, Object> loginlist) {
    String username = (String) loginlist.get("username");
    String password = (String) loginlist.get("password");
    int identity = (int) loginlist.get("Identity"); // 前端传递的身份（0=管理员，1=店长）

    QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("username", username);
    queryWrapper.eq("password", password); // 注意：生产环境必须加密密码！

    // 核心修复1：按所选身份，强制匹配对应的shop_id规则
    if (identity == 0) {
        // 管理员：必须shop_id=0
        queryWrapper.eq("shop_id", 0);
    } else if (identity == 1) {
        // 店长：必须shop_id>0（排除管理员）
        queryWrapper.gt("shop_id", 0);
    } else {
        // 非法身份，直接返回失败
        throw new RuntimeException("身份选择异常，请重新选择！");
    }

    // 添加 LIMIT 1 防止查询返回多条记录导致错误
    queryWrapper.last("LIMIT 1");

    Employee employee = employeeService.getOne(queryWrapper);
    if (employee == null) {
        // 核心修复2：错误提示更精准，避免用户猜账号
        throw new RuntimeException("用户名、密码或身份不匹配，请重试！");
    }

    if (identity == 1 && employee.getShopId() != null && employee.getShopId() > 0) {
        org.example.orderfoodafter.entity.Shop shop = shopService.getById(employee.getShopId());
        if (shop != null && shop.getStatus() != null && shop.getStatus() == 3) {
            throw new RuntimeException("所属店铺还未通过审核，无法登录！");
        }
    }

    // 生成JWT令牌（保留原有逻辑）
    String token = new JwtUtils().generateToken(employee.getId() + "");
    return new R().addData("token", token).addData("username", username).addData("shopid", employee.getShopId());
}
//用户登录
@PostMapping("/UserLogin")
public R userLogin(@RequestBody User user){
    String phone= user.getPhone();
    String password = user.getPassword();
    String password1=(String) redisTemplate.opsForValue().get("userlogindata:"+phone);
    if(!password.equals(password1)){
        throw new RuntimeException("用户名或密码错误！");
    }
    System.out.println("已访问数据库");
    QueryWrapper<User> queryWrapper=new QueryWrapper<>();
    queryWrapper.eq("phone",phone);
    queryWrapper.eq("password",password);
    User user1=userService.getOne(queryWrapper);
    String token=new JwtUtils().generateToken(user1.getId()+"");
    return new R().addData("token",token).addData("user",user1);
}
}
