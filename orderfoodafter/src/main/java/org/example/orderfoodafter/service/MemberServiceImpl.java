package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.Member;
import org.example.orderfoodafter.mapper.MemberMapper;
import org.example.orderfoodafter.service.MemberService;
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService{

}
