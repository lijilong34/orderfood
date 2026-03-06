package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Employee;
import org.example.orderfoodafter.service.EmployeeService;
import org.example.orderfoodafter.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 员工管理控制器
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController extends BaseController<Employee, EmployeeService> {

    @Autowired
    public EmployeeController(EmployeeService service) {
        super(service);
    }

    /**
     * 暂停员工
     */
    @PostMapping("/pause/{id}")
    public R pauseEmployee(@PathVariable("id") Long id) {
        Employee employee = service.getById(id);
        if (employee != null) {
            employee.setWorkStatus((byte) 0); // 0表示休假/暂停
            service.updateById(employee);
            R r = new R();
            r.setMessage("员工已暂停");
            return r;
        }
        R r = new R();
        r.setCode(500);
        r.setMessage("员工不存在");
        return r;
    }

    /**
     * 恢复员工工作
     */
    @PostMapping("/resume/{id}")
    public R resumeEmployee(@PathVariable("id") Long id) {
        Employee employee = service.getById(id);
        if (employee != null) {
            employee.setWorkStatus((byte) 1); // 1表示在岗
            service.updateById(employee);
            R r = new R();
            r.setMessage("员工已恢复工作");
            return r;
        }
        R r = new R();
        r.setCode(500);
        r.setMessage("员工不存在");
        return r;
    }
    /*
    连接商店表查询员工
     */
    @PostMapping("/selectemployee")
    public R selectemployee(@RequestBody Map<String, Object> selectwhere)throws Exception{
        List where = (List) selectwhere.get("where");

        QueryWrapper queryWrapper = commontUtil.getWhere(where);
        if (selectwhere.get("page") != null) {
            int page = Integer.parseInt(selectwhere.get("page").toString());

            PageHelper.startPage(page, 6);
        }

        List<Employee> employeeList=service.selectemployee(queryWrapper);
        PageInfo pageInfo = new PageInfo<>(employeeList);

        return new R().addData("pageInfo", pageInfo);
    }
    @PostMapping("/selectemployeebyemployee")
    public R selectemployeebyemployee(@RequestBody Employee employee){
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", employee.getUsername());
        queryWrapper.eq("password", employee.getPassword());
        Employee employee1=service.getOne(queryWrapper);
      return new R().addData("employee", employee1);
    }
}
