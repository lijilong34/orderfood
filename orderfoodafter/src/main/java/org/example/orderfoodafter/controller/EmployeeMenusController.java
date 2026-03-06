package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.EmployeeMenus;
import org.example.orderfoodafter.service.EmployeeMenusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/EmployeeMenus")
public class EmployeeMenusController extends BaseController<EmployeeMenus,EmployeeMenusService>{
   @Autowired
    private EmployeeMenusService employeeMenusService;
   public EmployeeMenusController(EmployeeMenusService employeeMenusService) {
       super(employeeMenusService);
       this.employeeMenusService = employeeMenusService;
   }
   @PostMapping("/delEmployeeMenus")
    public R delEmployeeMenus(@RequestBody Map<String,Object> selectwhere) throws Exception{
       List where = (List) selectwhere.get("where");
       QueryWrapper queryWrapper = commontUtil.getWhere(where);
       boolean flag=employeeMenusService.remove(queryWrapper);
       if(flag){
           return new R().addData("status","删除成功");
       }
       throw new RuntimeException("删除失败");
   }
   @PostMapping("/addEmployeeMenus")
    public R addEmployeeMenus(@RequestBody Map<String,Object> map) throws Exception{
     boolean flag=employeeMenusService.addEmployeeMenus(Integer.parseInt(map.get("employeeId")+""),(List)map.get("menuIds"));
     if(flag){
         return new R().addData("status","添加成功");
     }
     throw new RuntimeException("添加失败");
   }
}