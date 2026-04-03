package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.mapper.UserMapper;
import org.example.orderfoodafter.entity.User;
import org.example.orderfoodafter.service.UserService;

/**
 * 用户Service实现类
 * 实现用户相关的业务逻辑处理功能
 *
 * @author 赵康乐
 * @date 2025-11-24
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
/**
 * updateBalance方法
 *
 * @author 赵康乐
 */

    @Override
    public boolean updateBalance(Long userId, BigDecimal newBalance) {
            /**
     * User
     * 
     * @author 赵康乐
     */
        User user = new User();
        user.setId(userId);
        user.setBalance(newBalance);
            /**
     * updateById
     * 
     * @author 赵康乐
     */
        return updateById(user);
    }
/**
 * addBalance方法
 *
 * @author 赵康乐
 */

    @Override
    public boolean addBalance(Long userId, BigDecimal amount) {
        User user = baseMapper.selectById(userId);
        if (user != null) {
            BigDecimal currentBalance = user.getBalance();
            if (currentBalance == null) {
                    /**
     * BigDecimal
     * 
     * @author 赵康乐
     */
                currentBalance = new BigDecimal("0");
            }
            BigDecimal newBalance = currentBalance.add(amount);
                /**
     * updateBalance
     * 
     * @author 赵康乐
     */
            return updateBalance(userId, newBalance);
        }
        return false;
    }
}
