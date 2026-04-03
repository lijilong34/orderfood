package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.Member;
import org.example.orderfoodafter.mapper.MemberMapper;
import org.example.orderfoodafter.service.MemberService;

/**
 * 会员Service实现类
 * 实现会员相关的业务逻辑处理功能
 * 
 * @author 李吉隆
 * @date 2025-12-19
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService{

}
