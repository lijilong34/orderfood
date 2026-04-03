package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.mapper.AddressMapper;
import org.example.orderfoodafter.entity.Address;
import org.example.orderfoodafter.service.AddressService;

/**
 * 地址Service实现类
 * 实现地址相关的业务逻辑处理功能
 * 
 * @author 李梦瑶、周子金
 * @date 2025-11-15
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService{

}
