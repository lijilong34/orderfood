package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.mapper.MenusMapper;
import org.example.orderfoodafter.entity.Menus;
import org.example.orderfoodafter.service.MenusService;
@Service
public class MenusServiceImpl extends ServiceImpl<MenusMapper, Menus> implements MenusService{

    @Override
    public List<Menus> selectMenusbyId(int employeeid) {
        List<Menus> menusList=baseMapper.selectMenusbyId(employeeid);
        return menusList;
    }
}
