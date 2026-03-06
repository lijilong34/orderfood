package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Complaint;
import org.example.orderfoodafter.entity.Product;
import org.example.orderfoodafter.service.ComplaintService;
import org.example.orderfoodafter.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 投诉管理控制器
 */
@RestController
@RequestMapping("/complaint")
public class ComplaintController extends BaseController<Complaint, ComplaintService> {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private ProductService productService;

    public ComplaintController(ComplaintService service) {
        super(service);
        this.complaintService = service;
    }

    /**
     * 添加投诉
     */
    @PostMapping("/addComplaint")
    public R addComplaint(@RequestHeader("Authorization") String token,@RequestBody Complaint complaint) {
        try {
            // 设置默认状态为待处理
            if (complaint.getStatus() == null) {
                complaint.setStatus((byte) 0);
            }
            token = token.substring(7);
            String subject = jwtUtils.getSubject(token);
            Long userId = Long.parseLong(subject);
            complaint.setUserId(userId);
            // 设置创建时间
            complaint.setCreateTime(new Date());
            complaint.setUpdateTime(new Date());
            
            // 保存投诉
            boolean result = complaintService.save(complaint);
            
            if (result) {
                return new R().addData("status", "投诉提交成功");
            } else {
                return R.error("投诉提交失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("投诉提交失败: " + e.getMessage());
        }
    }

    /**
     * 获取投诉列表
     */
    @PostMapping("/selectcomplaint")
    public R getComplaintList(@RequestBody Map<String, Object> params) {
        try {
            int page = params.containsKey("page") ? Integer.parseInt(params.get("page").toString()) : 1;
            int pageSize = params.containsKey("pageSize") ? Integer.parseInt(params.get("pageSize").toString()) : 10;
            int shopId = params.containsKey("shopId") ? Integer.parseInt(params.get("shopId").toString()) : 0;

            PageHelper.startPage(page, pageSize);
            
            QueryWrapper<Complaint> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("shop_id", shopId);
            queryWrapper.orderByDesc("create_time");
            
            List<Complaint> complaints = complaintService.selectComplaint(queryWrapper);
            PageInfo<Complaint> pageInfo = new PageInfo<>(complaints);
            return new R().addData("pageInfo", pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取投诉列表失败: " + e.getMessage());
        }
    }

    /**
     * 删除投诉
     */
    @DeleteMapping("/{id}")
    public R deleteComplaint(@PathVariable Long id) {
        try {
            Complaint complaint = complaintService.getById(id);
            if (complaint == null) {
                return R.error("投诉不存在");
            }
            
            // 判断状态：待处理(status=0)的情况下禁止删除
            if (complaint.getStatus() != null && complaint.getStatus() == 0) {
                return R.error("待处理状态的投诉不能删除");
            }
            
            boolean result = complaintService.removeById(id);
            if (result) {
                return new R().addData("status","删除成功");
            } else {
                return R.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 处理投诉
     */
    @PutMapping("/handle")
    public R handleComplaint(@RequestBody Map<String, Object> params) {
        try {
            Long id = Long.parseLong(params.get("id").toString());
            Integer status = params.containsKey("status") ? Integer.parseInt(params.get("status").toString()) : 1;
            String handler = params.containsKey("handler") ? params.get("handler").toString() : null;
            
            Complaint complaint = complaintService.getById(id);
            if (complaint == null) {
                return R.error("投诉不存在");
            }
            
            complaint.setStatus(status.byteValue());
            complaint.setUpdateTime(new Date());
            
            boolean result = complaintService.updateById(complaint);
            if (result) {
                return new R().addData("status","处理成功");
            } else {
                return R.error("处理失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("处理失败: " + e.getMessage());
        }
    }

    /**
     * 修改投诉状态
     */
    @PostMapping("/updateStatus")
    public R updateStatus(@RequestBody Map<String, Object> params) {
        try {
            Long id = Long.parseLong(params.get("id").toString());
            Integer status = Integer.parseInt(params.get("status").toString());

            Complaint complaint = complaintService.getById(id);
            if (complaint == null) {
                return R.error("投诉不存在");
            }

            complaint.setStatus(status.byteValue());
            complaint.setUpdateTime(new Date());



            boolean result = complaintService.updateById(complaint);
            if (result) {
                return new R().addData("status","状态修改成功");
            } else {
                return R.error("状态修改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("状态修改失败: " + e.getMessage());
        }
    }
}