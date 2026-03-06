package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.PaymentRecord;
import org.example.orderfoodafter.mapper.PaymentRecordMapper;
import org.example.orderfoodafter.service.PaymentRecordService;
@Service
public class PaymentRecordServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecord> implements PaymentRecordService{

}
