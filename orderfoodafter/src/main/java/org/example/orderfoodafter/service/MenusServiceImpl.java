package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.mapper.MenusMapper;
import org.example.orderfoodafter.entity.Menus;
import org.example.orderfoodafter.service.MenusService;

/**
 * 菜单Service实现类
 * 实现菜单相关的业务逻辑处理功能
 * 
 * @author 李吉隆
 * @date 2025-12-20
 */
@Service
public class MenusServiceImpl extends ServiceImpl<MenusMapper, Menus> implements MenusService{
/**
 * selectMenusbyId方法
 *
 * @author 李吉隆
 */

    @Override
    public List<Menus> selectMenusbyId(int employeeid) {
        List<Menus> menusList=baseMapper.selectMenusbyId(employeeid);
        return menusList;
    }
}
