package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.User;
import org.example.orderfoodafter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/ForgetPassword")
@RestController
public class ForgetPasswordController extends BaseController<User, UserService> {
    @Autowired
    public UserService userService;
    public ForgetPasswordController(UserService userService){
        super(userService);
        this.userService=userService;
    }

    /**
     * 判断手机号码是否存在
     * @param phone
     */
    @PostMapping("/selectphoneisexits")
    public R  selectphoneisexists(@RequestBody long phone){
        QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();
        userQueryWrapper.eq("phone",phone);
        boolean phonestatus=userService.exists(userQueryWrapper);
        if (phonestatus){
            return new R().addData("status","手机号码存在");
        }
        throw new RuntimeException("手机号码不存在");
    }
    @PostMapping("/updatepassword")
    public R updatepassword(@RequestBody User user){
        QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();
        userQueryWrapper.eq("phone",user.getPhone());
        userQueryWrapper.eq("password",user.getPassword());
        boolean flag=userService.update(user, userQueryWrapper);
        if (flag){
            return new R().addData("status","密码修改成功");
        }
        throw new RuntimeException("你的旧密码与原密码相同");
    }
}
