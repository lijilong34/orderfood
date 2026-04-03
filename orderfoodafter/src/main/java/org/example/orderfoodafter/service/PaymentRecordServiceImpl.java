package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.PaymentRecord;
import org.example.orderfoodafter.mapper.PaymentRecordMapper;
import org.example.orderfoodafter.service.PaymentRecordService;

/**
 * 支付记录Service实现类
 * 实现支付记录相关的业务逻辑处理功能
 * 
 * @author 李吉隆
 * @date 2025-12-06
 */
@Service
public class PaymentRecordServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecord> implements PaymentRecordService{

}
