package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.mapper.UserMapper;
import org.example.orderfoodafter.entity.User;
import org.example.orderfoodafter.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public boolean updateBalance(Long userId, BigDecimal newBalance) {
        User user = new User();
        user.setId(userId);
        user.setBalance(newBalance);
        return updateById(user);
    }

    @Override
    public boolean addBalance(Long userId, BigDecimal amount) {
        User user = baseMapper.selectById(userId);
        if (user != null) {
            BigDecimal currentBalance = user.getBalance();
            if (currentBalance == null) {
                currentBalance = new BigDecimal("0");
            }
            BigDecimal newBalance = currentBalance.add(amount);
            return updateBalance(userId, newBalance);
        }
        return false;
    }
}
