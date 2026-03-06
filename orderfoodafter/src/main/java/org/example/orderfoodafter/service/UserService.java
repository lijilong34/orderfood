package org.example.orderfoodafter.service;

import org.example.orderfoodafter.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import java.math.BigDecimal;

public interface UserService extends IService<User> {

    /**
     * 更新用户余额
     * @param userId 用户ID
     * @param newBalance 新余额
     * @return 是否更新成功
     */
    boolean updateBalance(Long userId, BigDecimal newBalance);
    
    /**
     * 增加用户余额
     * @param userId 用户ID
     * @param amount 增加的金额
     * @return 是否更新成功
     */
    boolean addBalance(Long userId, BigDecimal amount);
}
