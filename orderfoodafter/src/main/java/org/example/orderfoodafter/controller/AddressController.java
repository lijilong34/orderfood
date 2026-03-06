package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Address;
import org.example.orderfoodafter.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/address")
@RestController
public class AddressController extends BaseController<Address, AddressService> {
    @Autowired
    private AddressService addressService;
    public AddressController(AddressService addressService) {
        super(addressService);
        this.addressService = addressService;
    }

    @GetMapping("/list")
    public R getAddressList(@RequestHeader("Authorization") String token) {
        token = token.substring(7);
        String subject = jwtUtils.getSubject(token);
        Long userId = Long.parseLong(subject);

        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("is_default");
        queryWrapper.orderByDesc("update_time");

        List<Address> addressList = addressService.list(queryWrapper);
        return new R().addData("addressList", addressList);
    }
}
