package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.mapper.AddressMapper;
import org.example.orderfoodafter.entity.Address;
import org.example.orderfoodafter.service.AddressService;
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService{

}
