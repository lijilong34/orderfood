package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.common.CommontUtil;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Announcement;
import org.example.orderfoodafter.service.AnnouncementService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 名称:通知控制类
 * 时间:2026年3月7日
 * 作者:周子金
 */
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private CommontUtil commontUtil;
    /**
     * 分页查询公告带条件
     * 作者:周子金
     * @param params
     * @return 公告列表
     */
    @PostMapping("/selectbylike")
    public R selectByLike(@RequestBody Map<String, Object> params) throws IOException {
        //使用三木运算符判断当前页面，如果不为空就转换成字符类型，为空就赋值为1
        int page = params.get("page") != null ? (Integer) params.get("page") : 1;
        //获取前端传过来的条件并转换成集合
        List whereList =(List) params.get("where");
        //开启分页
        PageHelper.startPage(page,8);

        //创建查询条件构造器
        QueryWrapper<Announcement> queryWrapper =commontUtil.getWhere(whereList);

        // 默认按发布时间倒序排列
        queryWrapper.orderByDesc("publish_time");
        //获取公告列表
        List<Announcement> result = announcementService.list(queryWrapper);
        //创建分页构造器
        PageInfo pageInfo = new PageInfo<>(result);
        //把公告信息传给前端
            /**
     * R
     * 
     * @author 周子金
     */
        return new  R().addData("pageInfo", pageInfo);
    }

    /**
     * 根据id查询对象
     * 作者:周子金
     * @param id
     * @return 对象
     */
    @GetMapping("/{id}")
    public R getAnnouncementById(@PathVariable Long id) {
        //根据id查询对象
        Announcement announcement = announcementService.getById(id);
        //判断对象是否为空，如果不为空
        if (announcement != null) {
            //返回对象给前端
               /**
     * R
     * 
     * @author 周子金
     */
           return new  R().addData("entity",announcement);
        } else {
            //报错为不存在
               /**
     * RuntimeException
     * 
     * @author 周子金
     */
           throw new RuntimeException("公告不存在");
        }
    }

    /**
     * 添加公告
     * 作者:周子金
     * @param announcement
     * @return 结果
     */
    @PostMapping("/add")
    public R addAnnouncement(@RequestBody Announcement announcement) {
        try {
            // 设置创建时间和更新时间
                /**
     * Date
     * 
     * @author 周子金
     */
            Date now = new Date();
            announcement.setCreateTime(now);
            announcement.setUpdateTime(now);
            // 如果发布时间为空，则设置为当前时间
            if (announcement.getPublishTime() == null) {
                announcement.setPublishTime(now);
            }
            //保存对象
            boolean result = announcementService.save(announcement);
            //判断是否为空。如果为空报错
            if (result) {
                    /**
     * R
     * 
     * @author 周子金
     */
                return new R().addData("status","添加成功");
            } else {
                 /**
     * RuntimeException
     * 
     * @author 周子金
     */
             throw new RuntimeException("添加失败");
            }
        } catch (Exception e) {
                /**
     * RuntimeException
     * 
     * @author 周子金
     */
            throw new RuntimeException("添加失败");
        }
    }

    /**
     * 更新公告
     * 作者: 周子金
     * @param announcement
     * @return 结果
     */
    @PutMapping("/update")
    public R updateAnnouncement(@RequestBody Announcement announcement) {
        try {
            // 设置更新时间
                /**
     * 设置 setUpdateTime
     * 
     * @param setUpdateTime setUpdateTime
     * @author 周子金
     */
            announcement.setUpdateTime(new Date());
            //根据序号判断
            boolean result = announcementService.updateById(announcement);
            //判断是否修改成功
            if (result) {
                   /**
     * R
     * 
     * @author 周子金
     */
               return new R().addData("status","修改成功");
            } else {
                   /**
     * RuntimeException
     * 
     * @author 周子金
     */
               throw new RuntimeException("修改失败");
            }
        } catch (Exception e) {
                /**
     * RuntimeException
     * 
     * @author 周子金
     */
            throw new RuntimeException("修改失败");
        }
    }

    /**
     * 删除公告
     * 作者：周子金
     * @param id
     * @return 结果
     */
    @DeleteMapping("/delete/{id}")
    public R deleteAnnouncement(@PathVariable Long id) {
        try {
            //获取删除结果
            boolean result = announcementService.removeById(id);
            //判断是否删除成功
            if (result) {
                    /**
     * R
     * 
     * @author 周子金
     */
                return new R().addData("status","删除成功");
            } else {
                   /**
     * RuntimeException
     * 
     * @author 周子金
     */
               throw new RuntimeException("删除失败");
            }
        } catch (Exception e) {
                /**
     * RuntimeException
     * 
     * @author 周子金
     */
            throw new RuntimeException("删除失败");
        }
    }
}