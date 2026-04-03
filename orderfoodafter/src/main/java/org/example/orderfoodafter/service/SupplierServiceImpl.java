package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.mapper.SupplierMapper;
import org.example.orderfoodafter.entity.Supplier;
import org.example.orderfoodafter.service.SupplierService;

/**
 * 供应商Service实现类
 * 实现供应商相关的业务逻辑处理功能
 * 
 * @author 陈逸磊
 * @date 2026-01-22
 */
@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService{

}
