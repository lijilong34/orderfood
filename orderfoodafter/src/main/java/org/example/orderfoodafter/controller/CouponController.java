package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Coupon;
import org.example.orderfoodafter.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequestMapping("/coupon")
@RestController
public class CouponController extends BaseController<Coupon, CouponService> {
    @Autowired
    private CouponService couponService;

    public CouponController(CouponService couponService) {
        super(couponService);
        this.couponService = couponService;
    }

    @GetMapping("/available")
    public R getAvailableCoupons() {
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("user_id");
        queryWrapper.gt("end_time",new Date());
        List<Coupon> couponList = couponService.list(queryWrapper);
        return new R().addData("couponList", couponList);
    }

    /**
     * 获取可领取的优惠券模板列表
     */
    @GetMapping("/templates")
    public R getCouponTemplates() {
        try {
            // 获取优惠券模板（user_id为null的记录作为模板）
            QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
            queryWrapper.isNull("user_id"); // 优惠券模板
            queryWrapper.le("start_time", new Date()); // 已开始
            queryWrapper.ge("end_time", new Date()); // 未过期
            queryWrapper.eq("status", 1); // 可用状态

            queryWrapper.orderByAsc("min_amount");
            queryWrapper.orderByDesc("discount_value");

            List<Coupon> couponTemplates = couponService.list(queryWrapper);
            return new R().addData("couponList", couponTemplates);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取优惠券模板失败: " + e.getMessage());
        }
    }

    @PostMapping("/use")
    public R useCoupon(@RequestHeader("Authorization") String token, @RequestParam Long couponId) {
        token = token.substring(7);
        String subject = jwtUtils.getSubject(token);
        Long userId = Long.parseLong(subject);

        Coupon coupon = couponService.getById(couponId);
        if (coupon == null) {
            return R.error("优惠券不存在");
        }
        if (!coupon.getUserId().equals(userId)) {
            return R.error("无权使用此优惠券");
        }
        if (coupon.getStatus() != 1) {
            return R.error("优惠券不可用");
        }

        coupon.setStatus((byte) 0);
        boolean flag = couponService.updateById(coupon);
        if (flag) {
            R r = R.ok();
            r.setMessage("优惠券使用成功");
            return r;
        }
        return R.error("优惠券使用失败");
    }

    /**
     * 领取优惠券
     */
    @PostMapping("/claim")
    public R claimCoupon(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> couponData) {
        try {
            token = token.substring(7);
            String subject = jwtUtils.getSubject(token);
            Long userId = Long.parseLong(subject);

            Long couponId = Long.valueOf(couponData.get("couponId").toString());
            UpdateWrapper<Coupon> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", couponId).set("user_id", userId);

            // 领取优惠券
            boolean success = couponService.update(null,updateWrapper);
            if (success) {
                R r = R.ok();
                r.setMessage("优惠券领取成功");
                return r;
            } else {
                return R.error("优惠券领取失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("领取优惠券时发生错误: " + e.getMessage());
        }
    }

    /**
     * 获取用户已领取的优惠券列表
     */
    @GetMapping("/couponlist")
    public R getUserCoupons(@RequestHeader("Authorization") String token) {
        try {
            token = token.substring(7);
            String subject = jwtUtils.getSubject(token);
            Long userId = Long.parseLong(subject);
            QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id",userId);
            queryWrapper.gt("end_time",new Date());
            List<Coupon> couponList = couponService.list(queryWrapper);

            R r = R.ok();
            r.addData("couponList", couponList);
            return r;
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取用户优惠券列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取今日已领取的优惠券
     */
    @GetMapping("/today-claimed")
    public R getTodayClaimedCoupons(@RequestHeader("Authorization") String token) {
        try {
            token = token.substring(7);
            String subject = jwtUtils.getSubject(token);
            Long userId = Long.parseLong(subject);

            List<Coupon> couponList = couponService.getTodayClaimedCoupons(userId);

            R r = R.ok();
            r.addData("couponList", couponList);
            return r;
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取今日已领取优惠券失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户所有已领取的优惠券（包括所有时间的）
     */
    @GetMapping("/all-claimed")
    public R getAllClaimedCoupons(@RequestHeader("Authorization") String token) {
        try {
            token = token.substring(7);
            String subject = jwtUtils.getSubject(token);
            Long userId = Long.parseLong(subject);

            List<Coupon> couponList = couponService.getUserCoupons(userId);

            R r = R.ok();
            r.addData("couponList", couponList);
            return r;
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取所有已领取优惠券失败: " + e.getMessage());
        }
    }

    // ==================== 管理员接口 ====================

    /**
     * 管理员获取优惠券模板列表（分页）
     */
    @PostMapping("/admin/list")
    public R getAdminCouponList(@RequestBody Map<String, Object> params) {
        try {
            int page = params.containsKey("page") ? Integer.parseInt(params.get("page").toString()) : 1;
            int pageSize = params.containsKey("pageSize") ? Integer.parseInt(params.get("pageSize").toString()) : 10;

            PageHelper.startPage(page, pageSize);

            QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
//            queryWrapper.isNull("user_id"); // 只获取模板
              queryWrapper.orderByDesc("create_time");
            // 搜索关键词
            if (params.containsKey("keyword") && params.get("keyword") != null) {
                String keyword = params.get("keyword").toString().trim();
                if (!keyword.isEmpty()) {
                    queryWrapper.like("title", keyword);
                }
            }

            // 优惠类型筛选
            if (params.containsKey("discountType") && params.get("discountType") != null) {
                String discountTypeStr = params.get("discountType").toString();
                if (!discountTypeStr.isEmpty()) {
                    queryWrapper.eq("discount_type", Integer.parseInt(discountTypeStr));
                }
            }

            // 状态筛选
            if (params.containsKey("status") && params.get("status") != null) {
                String statusStr = params.get("status").toString();
                if (!statusStr.isEmpty()) {
                    queryWrapper.eq("status", Integer.parseInt(statusStr));
                }
            }

            queryWrapper.orderByDesc("create_time");

            List<Coupon> couponList = couponService.list(queryWrapper);
            PageInfo<Coupon> pageInfo = new PageInfo<>(couponList);

            return new R().addData("pageInfo", pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取优惠券列表失败: " + e.getMessage());
        }
    }

    /**
     * 管理员创建优惠券模板
     */
    @PostMapping("/admin/create")
    public R createCouponTemplate(@RequestBody Map<String, Object> params) {
        try {
            Coupon coupon = new Coupon();

            coupon.setTitle(params.get("title").toString());

            // 优惠类型: 1-满减 2-折扣
            coupon.setDiscountType(Byte.parseByte(params.get("discountType").toString()));

            // 优惠值
            if (params.get("discountValue") != null) {
                coupon.setDiscountValue(new BigDecimal(params.get("discountValue").toString()));
            }

            // 最低使用金额
            if (params.get("minAmount") != null) {
                coupon.setMinAmount(new BigDecimal(params.get("minAmount").toString()));
            } else {
                coupon.setMinAmount(BigDecimal.ZERO);
            }

            // 店铺ID (0表示全店通用)
            if (params.get("shopId") != null) {
                coupon.setShopId(Long.parseLong(params.get("shopId").toString()));
            } else {
                coupon.setShopId(0L);
            }

            // 生效时间
            if (params.get("startTime") != null) {
                coupon.setStartTime(new Date(Long.parseLong(params.get("startTime").toString())));
            }

            // 过期时间
            if (params.get("endTime") != null) {
                coupon.setEndTime(new Date(Long.parseLong(params.get("endTime").toString())));
            }

            // 状态: 1-可用
            coupon.setStatus(Byte.parseByte(params.getOrDefault("status", "1").toString()));

            // 创建时间
            coupon.setCreateTime(new Date());
            coupon.setUpdateTime(new Date());

            // user_id为null表示这是模板
            boolean result = couponService.save(coupon);

            if (result) {
                return new R().addData("coupon", coupon).addData("message", "优惠券创建成功");
            } else {
                return R.error("优惠券创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("优惠券创建失败: " + e.getMessage());
        }
    }

    /**
     * 管理员更新优惠券模板
     */
    @PutMapping("/admin/update")
    public R updateCouponTemplate(@RequestBody Map<String, Object> params) {
        try {
            Long id = Long.parseLong(params.get("id").toString());

            Coupon coupon = couponService.getById(id);
            if (coupon == null) {
                return R.error("优惠券不存在");
            }

            // 更新字段
            if (params.containsKey("title")) {
                coupon.setTitle(params.get("title").toString());
            }

            if (params.containsKey("discountType")) {
                coupon.setDiscountType(Byte.parseByte(params.get("discountType").toString()));
            }

            if (params.containsKey("discountValue")) {
                coupon.setDiscountValue(new BigDecimal(params.get("discountValue").toString()));
            }

            if (params.containsKey("minAmount")) {
                coupon.setMinAmount(new BigDecimal(params.get("minAmount").toString()));
            }

            if (params.containsKey("shopId")) {
                coupon.setShopId(Long.parseLong(params.get("shopId").toString()));
            }

            if (params.containsKey("startTime")) {
                coupon.setStartTime(new Date(Long.parseLong(params.get("startTime").toString())));
            }

            if (params.containsKey("endTime")) {
                coupon.setEndTime(new Date(Long.parseLong(params.get("endTime").toString())));
            }

            if (params.containsKey("status")) {
                coupon.setStatus(Byte.parseByte(params.get("status").toString()));
            }

            coupon.setUpdateTime(new Date());

            boolean result = couponService.updateById(coupon);

            if (result) {
                return new R().addData("coupon", coupon).addData("message", "优惠券更新成功");
            } else {
                return R.error("优惠券更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("优惠券更新失败: " + e.getMessage());
        }
    }

    /**
     * 管理员删除优惠券模板
     */
    @DeleteMapping("/admin/delete/{id}")
    public R deleteCouponTemplate(@PathVariable Long id) {
        try {
            Coupon coupon = couponService.getById(id);
            if (coupon == null) {
                return R.error("优惠券不存在");
            }

            // 只能删除模板，不能删除用户已领取的优惠券
            if (coupon.getUserId() != null) {
                return R.error("不能删除用户已领取的优惠券");
            }

            boolean result = couponService.removeById(id);

            if (result) {
                return new R().addData("message", "优惠券删除成功");
            } else {
                return R.error("优惠券删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("优惠券删除失败: " + e.getMessage());
        }
    }

    /**
     * 管理员获取优惠券详情
     */
    @GetMapping("/admin/detail/{id}")
    public R getCouponDetail(@PathVariable Long id) {
        try {
            Coupon coupon = couponService.getById(id);
            if (coupon == null) {
                return R.error("优惠券不存在");
            }

            return new R().addData("coupon", coupon);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取优惠券详情失败: " + e.getMessage());
        }
    }
}
