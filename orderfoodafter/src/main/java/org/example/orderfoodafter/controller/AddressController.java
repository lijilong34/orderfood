package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Address;
import org.example.orderfoodafter.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 名称:地址控制类
 * 日期:2026年3月7日
 * 作者:李梦瑶、周子金
 */
@RequestMapping("/address")
@RestController
public class AddressController extends BaseController<Address, AddressService> {
    @Autowired
    private AddressService addressService;

    /**
     * 地址构造方法
     * @param addressService
     */
    public AddressController(AddressService addressService) {
        super(addressService);
        this.addressService = addressService;
    }

    /**
     * 查询地址列表
     * @param token
     * 作者:周子金
     * @return 地址列表
     */
    @GetMapping("/list")
    public R getAddressList(@RequestHeader("Authorization") String token) {
        //截取字符串获取真实的访问令牌
        token = token.substring(7);

        //解密令牌获取用户序号
        String subject = jwtUtils.getSubject(token);

        //把字符串类型转化成数值类型(长整形)
        Long userId = Long.parseLong(subject);

        //创建查询的条件构造器
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();

        //增加根据用户序号查询(等于)
        queryWrapper.eq("user_id", userId);

        //添加按默认地址倒序，把默认地址排在最前面
        queryWrapper.orderByDesc("is_default");

        //添加按更新时间倒序
        queryWrapper.orderByDesc("update_time");

         //调用服务类原有的查询方法，并加上条件
        List<Address> addressList = addressService.list(queryWrapper);


        //把获取的地址列表用返回类返回给前端
            /**
     * R
     * 
     * @author 周子金
     */
        return new R().addData("addressList", addressList);
    }
}
